const express = require("express");
const jwt = require("jsonwebtoken");
const fs = require("fs");
const fsp = require("fs/promises");
const cors = require("cors");
const axios = require('axios');

const { Pool } = require("pg");
require("dotenv").config();

const app = express();
app.use(express.json());

const origins = ["http://localhost:3000", "http://localhost"];
app.use(cors({
  origin: origins,
  credentials: true,
  methods: ["GET", "POST", "PUT", "DELETE"],
  allowedHeaders: ["Content-Type", "Authorization"],
}));

// Wczytaj zmienne z pliku konfiguracyjnego
let configMap = {};
try {
  const configData = fs.readFileSync("/config/ex-backend-config.env", "utf8");
  configData.split("\n").forEach(line => {
    const [key, value] = line.split('=');
    if (key && value) {
      configMap[key.trim()] = value.trim().replace(/^"|"$/g, '');
    }
  });
} catch (e) {
  console.warn("Nie znaleziono /config/ex-backend-config.env. Kontynuuję bez niego.");
}

// Połączenie z bazą danych
let pool;
async function connectToDatabase() {
  try {
    const secretPath = process.env.PGPASSWORD_FILE;
    let password = process.env.PGPASSWORD;

    if (secretPath && fs.existsSync(secretPath)) {
      password = (await fsp.readFile(secretPath, "utf8")).trim();
    }

    pool = new Pool({
      user: configMap.PGUSER || process.env.PGUSER,
      host: configMap.PGHOST || process.env.PGHOST,
      database: configMap.PGDATABASE || process.env.PGDATABASE,
      password,
      port: parseInt(configMap.PGPORT || process.env.PGPORT),
    });

    const client = await pool.connect();

    // Tworzenie tabeli
    await client.query(`
      CREATE TABLE IF NOT EXISTS admin_requests (
        id SERIAL PRIMARY KEY,
        username VARCHAR(255) NOT NULL UNIQUE,
        status VARCHAR(50) DEFAULT 'pending'  -- 'pending', 'approved', 'rejected'
      );
    `);

    client.release();
    console.info("Connected to DB and ensured table exists.");
  } catch (err) {
    console.error("DB connection error:", err.message);
    process.exit(1);
  }
}

// Middleware JWT
const verifyToken = async (req, res, next) => {
  console.log("=== START TOKEN VERIFICATION ===");

  const authHeader = req.headers['authorization'];

  if (!authHeader) {
    console.error("Brak nagłówka Authorization");
    return res.status(401).json({ error: "Brak nagłówka Authorization" });
  }

  if (!authHeader.startsWith('Bearer ')) {
    console.error("Niepoprawny format nagłówka Authorization");
    return res.status(401).json({ error: "Invalid authorization header" });
  }

  const token = authHeader.replace('Bearer ', '');
  console.log("Token extracted");


  const keycloakUrl = configMap.keycloak_url;
  const realm = configMap.realm;
  const clientId = configMap.client_id;
  const clientSecret = configMap.client_secret;

  console.log(configMap)


  if (!keycloakUrl || !realm || !clientId || !clientSecret) {
    console.error("Brak wymaganych zmiennych środowiskowych do introspekcji Keycloak");
    return res.status(500).json({ error: "Server configuration error" });
  }

  const introspectionUrl = `${keycloakUrl}/realms/${realm}/protocol/openid-connect/token/introspect`;

  try {
    const response = await axios.post(
      introspectionUrl,
      new URLSearchParams({ token }),
      {
        auth: {
          username: clientId,
          password: clientSecret
        },
        timeout: 5000
      }
    );

    const tokenData = response.data;

    if (!tokenData.active) {
      console.error("Token jest nieaktywny lub nieważny");
      return res.status(401).json({ error: "Invalid or expired token" });
    }

    console.log("Token jest aktywny, introspekcja zakończona sukcesem");
    req.tokenData = tokenData;
    next();

  } catch (err) {
    console.error("Błąd podczas wywołania introspekcji Keycloak:", err.message);
    return res.status(500).json({ error: "Introspection request failed" });
  }
};


function checkAdmin(req, res, next) {
  const roles = req.tokenData.realm_access.roles || [];
  console.log(req.tokenData.realm_access.roles)
  if (!roles.includes("admin")) {
    return res.status(403).json({ error: "Wymagana rola administratora" });
  }
  next();
}

// Endpoint: user zgłasza prośbę o zostanie adminem
app.post("/api-ex/request-admin", verifyToken, async (req, res) => {
  console.log(req)
  const username = req.tokenData.preferred_username;

  try {
    await pool.query(
      "INSERT INTO admin_requests (username) VALUES ($1) ON CONFLICT (username) DO UPDATE SET status = 'pending'",
      [username]
    );
    res.json({ message: "Zgłoszenie wysłane" });
  } catch (err) {
    console.error("Błąd zgłoszenia:", err.message);
    res.status(500).json({ error: "Błąd serwera" });
  }
});

// Endpoint: admin pobiera wszystkie zgłoszenia
app.get("/api-ex/admin/requests", verifyToken, checkAdmin, async (req, res) => {
  try {
    // const result = await pool.query("SELECT * FROM admin_requests ORDER BY id DESC");

    const result = await pool.query(`
      SELECT * FROM admin_requests 
      WHERE status IS DISTINCT FROM 'approved' 
        AND status IS DISTINCT FROM 'rejected' 
      ORDER BY id DESC
    `);
    
    res.json({ requests: result.rows });
  } catch (err) {
    console.error("Błąd pobierania zgłoszeń:", err.message);
    res.status(500).json({ error: "Błąd serwera" });
  }
});


async function assignRoleToUser(username, roleName) {
  const keycloakUrl = configMap.keycloak_url;
  const realm = configMap.realm;
  const adminUsername = configMap.admin_username;
  const adminPassword = configMap.admin_password;
  const clientId = "admin-cli";

  try {
    // 1. Pobierz token admina
    const tokenRes = await axios.post(
      `${keycloakUrl}/realms/master/protocol/openid-connect/token`,
      new URLSearchParams({
        grant_type: "password",
        client_id: clientId,
        username: adminUsername,
        password: adminPassword
      }),
      { headers: { "Content-Type": "application/x-www-form-urlencoded" } }
    );

    const adminToken = tokenRes.data.access_token;

    // 2. Pobierz użytkownika
    const userRes = await axios.get(
      `${keycloakUrl}/admin/realms/${realm}/users?username=${username}`,
      {
        headers: { Authorization: `Bearer ${adminToken}` }
      }
    );

    const user = userRes.data[0];
    if (!user) throw new Error("Nie znaleziono użytkownika");

    // 3. Pobierz dane roli
    const roleRes = await axios.get(
      `${keycloakUrl}/admin/realms/${realm}/roles/${roleName}`,
      {
        headers: { Authorization: `Bearer ${adminToken}` }
      }
    );

    const role = roleRes.data;

    // 4. Przypisz rolę
    await axios.post(
      `${keycloakUrl}/admin/realms/${realm}/users/${user.id}/role-mappings/realm`,
      [role],
      {
        headers: {
          Authorization: `Bearer ${adminToken}`,
          "Content-Type": "application/json"
        }
      }
    );

    console.log(`Rola '${roleName}' została przypisana użytkownikowi ${username}`);
  } catch (err) {
    console.error("Błąd podczas przypisywania roli:", err.message);
    throw err;
  }
}


// Endpoint: admin odrzuca zgłoszenie
app.post("/api-ex/admin/reject", verifyToken, checkAdmin, async (req, res) => {
  const { username } = req.body;

  if (!username) return res.status(400).json({ error: "Brak nazwy użytkownika" });

  try {
    await pool.query(
      "UPDATE admin_requests SET status = 'rejected' WHERE username = $1",
      [username]
    );
    res.json({ message: "Zgłoszenie odrzucone" });
  } catch (err) {
    console.error("Błąd odrzucania:", err.message);
    res.status(500).json({ error: "Błąd serwera" });
  }
});

app.post("/api-ex/admin/approve", verifyToken, checkAdmin, async (req, res) => {
  const { username } = req.body;

  if (!username) return res.status(400).json({ error: "Brak nazwy użytkownika" });

  try {
    await pool.query(
      "UPDATE admin_requests SET status = 'approved' WHERE username = $1",
      [username]
    );

    // Przydziel rolę w Keycloak
    await assignRoleToUser(username, "admin");

    res.json({ message: "Zgłoszenie zatwierdzone i rola nadana" });
  } catch (err) {
    console.error("Błąd zatwierdzania:", err.message);
    res.status(500).json({ error: "Błąd serwera" });
  }
});


// Health check
app.get("/health", (req, res) => {
  res.json({ status: "OK" });
});

// Start serwera
const PORT = process.env.PORT || 3002;
connectToDatabase().then(() => {
  app.listen(PORT, () => {
    console.log(`Serwer działa na porcie ${PORT}`);
  });
});
