import os
from fastapi import FastAPI, Request, Header, HTTPException, Depends
from fastapi.middleware.cors import CORSMiddleware
from jose import jwt, JWTError
from dotenv import load_dotenv
import asyncpg
from typing import List, Optional
from pydantic import BaseModel
import logging
import requests




load_dotenv("/config/backend-config.env")

app = FastAPI()

origins = ["http://localhost:3000", "http://localhost"]
app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

print("host", os.getenv("PGHOST"))

async def get_db():
    pg_user = os.getenv("PGUSER")
    pg_password_file = os.getenv("PGPASSWORD_FILE")
    pg_database = os.getenv("PGDATABASE")
    pg_host = os.getenv("PGHOST")
    pg_port = os.getenv("PGPORT")

    if pg_password_file:
        try:
            with open(pg_password_file, "r") as f:
                pg_password = f.read().strip()
        except Exception as e:
            print(f"Błąd podczas odczytu pliku z hasłem: {e}")
            raise Exception("Błąd odczytu hasła z pliku!")
    else:
        pg_password = os.getenv("PGPASSWORD")  

    if pg_user is None or pg_password is None or pg_database is None:
        print("Błąd: Zmienne środowiskowe nie zostały załadowane!")
        raise Exception("Brak wymaganych zmiennych środowiskowych!")  
    else:
        print(f"Zmienna PGUSER: {pg_user}")
        print(f"Zmienna PGPASSWORD: {pg_password}")
        print(f"Zmienna PGDATABASE: {pg_database}")

    conn = await asyncpg.connect(
        user=pg_user,
        password=pg_password,
        database=pg_database,
        host=pg_host,
        port=pg_port
    )

    return conn


@app.on_event("startup")
async def startup():
    conn = await get_db()
    await conn.execute("""

        CREATE TABLE IF NOT EXISTS tasks (
            id SERIAL PRIMARY KEY,
            username VARCHAR(255) NOT NULL,
            title VARCHAR(255) NOT NULL,
            date VARCHAR(255) NOT NULL,
            completed BOOLEAN DEFAULT false,
            employees TEXT[] NOT NULL 
        );
                       
        CREATE TABLE IF NOT EXISTS task_status (
        task_id INT REFERENCES tasks(id) ON DELETE CASCADE,  
        username VARCHAR(255) NOT NULL,  
        completed BOOLEAN DEFAULT false, 
        PRIMARY KEY (task_id, username)  
);

    """)
    await conn.close()



logger = logging.getLogger("verify_token")
logging.basicConfig(level=logging.INFO)


# def verify_token(authorization: Optional[str] = Header(None)) -> dict:
#     logger.info("=== START TOKEN VERIFICATION ===")
    
#     if not authorization:
#         logger.error("Brak nagłówka Authorization")
#         raise HTTPException(status_code=401, detail="Brak nagłówka Authorization")

#     logger.info("Authorization header received: %s", authorization)

#     if not authorization.startswith("Bearer "):
#         logger.error("Niepoprawny format nagłówka Authorization")
#         raise HTTPException(status_code=401, detail="Invalid authorization header")

#     token = authorization.replace("Bearer ", "")
#     logger.info("Token extracted: %s", token)

#     public_key_path = os.getenv("PUBLIC_KEY_FILE", "/run/secrets/jwt_private_key")

#     try:
#         with open(public_key_path, "r") as key_file:
#             formatted_key = key_file.read()
#         public_key = f"-----BEGIN PUBLIC KEY-----\n{formatted_key.strip()}\n-----END PUBLIC KEY-----"
#         logger.info("Public key loaded from file: %s", public_key_path)
#     except Exception as e:
#         logger.error("Błąd podczas odczytu klucza publicznego z pliku: %s", str(e))
#         raise HTTPException(status_code=500, detail="Błąd podczas odczytu klucza publicznego")

#     try:
#         audience = "account"
#         unverified_payload = jwt.get_unverified_claims(token)
#         logger.info("Unverified token payload: %s", unverified_payload)

#         payload = jwt.decode(token, public_key, algorithms=["RS256"], audience=audience)
#         logger.info("Token verified successfully: %s", payload)

#         if "realm_access" in payload and "roles" in payload["realm_access"]:
#             logger.info("User roles: %s", payload["realm_access"]["roles"])
#         else:
#             logger.info("No roles found in the token.")

#         logger.info("=== END TOKEN VERIFICATION ===")
#         return payload

#     except JWTError as err:
#         logger.error("Token verification error: %s", str(err))
#         raise HTTPException(status_code=401, detail="Invalid or expired token")

logger = logging.getLogger(__name__)

def verify_token(authorization: Optional[str] = Header(None)) -> dict:
    logger.info("=== START TOKEN VERIFICATION ===")

    if not authorization:
        logger.error("Brak nagłówka Authorization")
        raise HTTPException(status_code=401, detail="Brak nagłówka Authorization")

    if not authorization.startswith("Bearer "):
        logger.error("Niepoprawny format nagłówka Authorization")
        raise HTTPException(status_code=401, detail="Invalid authorization header")

    token = authorization.replace("Bearer ", "")
    logger.info("Token extracted")

    # Endpoint introspekcji Keycloak
    keycloak_url = os.getenv("keycloak_url")
    realm = os.getenv("realm")
    client_id = os.getenv("client_id")
    client_secret = os.getenv("client_secret")


    if not all([keycloak_url, realm, client_id, client_secret]):
        logger.error("Brak wymaganych zmiennych środowiskowych do introspekcji Keycloak")
        raise HTTPException(status_code=500, detail="Server configuration error")

    introspection_url = f"{keycloak_url}/realms/{realm}/protocol/openid-connect/token/introspect"

    try:
        response = requests.post(
            introspection_url,
            data={'token': token},
            auth=(client_id, client_secret),
            timeout=5
        )
        response.raise_for_status()
        token_data = response.json()
    except Exception as e:
        logger.error(f"Błąd podczas wywołania introspekcji Keycloak: {str(e)}")
        raise HTTPException(status_code=500, detail="Introspection request failed")

    if not token_data.get("active", False):
        logger.error("Token jest nieaktywny lub nieważny")
        raise HTTPException(status_code=401, detail="Invalid or expired token")

    logger.info("Token jest aktywny, introspekcja zakończona sukcesem")
    return token_data

# Admin check
def check_admin(decoded_token: dict):
    print("token", decoded_token)
    roles = decoded_token.get("realm_access", {}).get("roles", [])
    if "admin" not in roles:
        raise HTTPException(status_code=403, detail="Admin role required")


# Models
class TaskInput(BaseModel):
    title: str
    date: str
    employees: List[str]

class TaskStatusUpdate(BaseModel):
    task_id: int
    completed: bool

class TaskDeleteInput(BaseModel):
    task_id: int

# Endpointy dla zadań
@app.post("/api/task/assign")
async def create_task_by_admin(
    task: TaskInput, 
    token: dict = Depends(verify_token)
):
    """Tylko admin może tworzyć zadania dla wielu użytkowników"""
    check_admin(token)
    
    if not task.title or not task.employees:
        raise HTTPException(status_code=400, detail="Title and employees are required")

    conn = await get_db()
    try:
        task_record = await conn.fetchrow(
            """INSERT INTO tasks (username, title, date, employees) 
               VALUES ($1, $2, $3, $4) RETURNING *""",
            token["preferred_username"],  
            task.title,
            task.date,
            task.employees
        )
        
        for employee in task.employees:
            await conn.execute(
                """INSERT INTO task_status (task_id, username, completed)
                   VALUES ($1, $2, $3)""",
                task_record["id"],
                employee,
                False
            )
        
        return {"message": "Task created successfully", "task": dict(task_record)}
    except Exception as e:
        logger.error(f"Error creating task: {str(e)}")
        logger.error(f"Task data: {task.dict()}")
        logger.error(f"Database connection: {conn}")
        raise HTTPException(status_code=500, detail=str(e))

@app.delete("/api/task")
async def delete_task_by_admin(
    task: TaskDeleteInput, 
    token: dict = Depends(verify_token)
):
    """Tylko admin może usuwać zadania"""
    check_admin(token)
    
    conn = await get_db()
    try:
        # Sprawdź czy zadanie istnieje i należy do admina
        existing_task = await conn.fetchrow(
            "SELECT * FROM tasks WHERE id = $1 AND username = $2",
            task.task_id,
            token["preferred_username"]
        )
        
        if not existing_task:
            raise HTTPException(status_code=404, detail="Task not found or not authorized")

        await conn.execute("DELETE FROM tasks WHERE id = $1", task.task_id)
        
        return {"message": "Task deleted successfully"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        await conn.close()

@app.get("/api/admin/tasks")
async def get_all_tasks_with_status(
    token: dict = Depends(verify_token)
):
    """Pobierz wszystkie zadania z statusami wykonania (tylko admin)"""
    check_admin(token)
    
    conn = await get_db()
    try:
        tasks = await conn.fetch(
            "SELECT * FROM tasks WHERE username = $1",
            token["preferred_username"]
        )
        
        result = []
        for task in tasks:
            statuses = await conn.fetch(
                "SELECT * FROM task_status WHERE task_id = $1",
                task["id"]
            )
            result.append({
                "task": dict(task),
                "statuses": [dict(s) for s in statuses]
            })
        
        return {"tasks": result}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        await conn.close()

@app.put("/api/task")
async def update_task_status(
    update: TaskStatusUpdate,
    token: dict = Depends(verify_token)
):
    """Użytkownik może zaktualizować swój status zadania"""
    conn = await get_db()
    try:
        assigned = await conn.fetchrow(
            """SELECT 1 FROM tasks t 
               WHERE t.id = $1 AND $2 = ANY(t.employees)""",
            update.task_id,
            token["preferred_username"]
        )
        
        if not assigned:
            raise HTTPException(status_code=403, detail="Not authorized to update this task")

        await conn.execute(
            """UPDATE task_status 
               SET completed = $1 
               WHERE task_id = $2 AND username = $3""",
            update.completed,
            update.task_id,
            token["preferred_username"]
        )
        
        return {"message": "Task status updated successfully"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        await conn.close()

@app.get("/api/my-tasks")
async def get_my_tasks(
    token: dict = Depends(verify_token)
):
    """Pobierz zadania przypisane do bieżącego użytkownika"""
    conn = await get_db()
    try:
        tasks = await conn.fetch(
            """SELECT t.*, ts.completed 
               FROM tasks t
               JOIN task_status ts ON t.id = ts.task_id
               WHERE ts.username = $1""",
            token["preferred_username"]
        )
        
        return {"tasks": [dict(t) for t in tasks]}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        await conn.close()

@app.get("/api/task")
async def get_user_tasks(token: dict = Depends(verify_token)):
    conn = await get_db()
    try:
        tasks = await conn.fetch("""
            SELECT 
                t.id,
                t.title,
                t.date,
                t.username,
                ts.completed
            FROM tasks t
            JOIN task_status ts ON t.id = ts.task_id
            WHERE ts.username = $1
            ORDER BY t.date DESC
        """, token["preferred_username"])

        return {
            "tasks": [
                {
                    "id": task["id"],
                    "title": task["title"],
                    "date": task["date"],
                    "admin": task["username"],
                    "completed": task["completed"]
                }
                for task in tasks
            ]
        }
    except Exception as e:
        logger.error(f"Error fetching tasks: {str(e)}")
        raise HTTPException(status_code=500, detail="Internal server error")
    finally:
        await conn.close()

@app.get("/api/employees")
async def get_employees(token: dict = Depends(verify_token)):
    check_admin(token)
    conn = await get_db()

    query = """
        SELECT username AS email
        FROM user_entity
        WHERE email IS NOT NULL
    """
    employees = await conn.fetch(query)
    await conn.close()

    return {"employees": [dict(e) for e in employees]}

@app.get("/api/summary")
async def get_summary(token: dict = Depends(verify_token)):
    print("doctalem summary")
    conn = None
    try:
        conn = await get_db()
        print("TOKa", token)
        username = token["preferred_username"]
        print("sss", username)

        try:
            check_admin(token)
            print("admina")
            total_tasks = await conn.fetchval("SELECT COUNT(*) FROM tasks")
            total_statuses = await conn.fetch("SELECT completed FROM task_status")

            completed = sum(1 for row in total_statuses if row["completed"] is True)
            pending = len(total_statuses) - completed
            unique_employees = await conn.fetchval("""
                SELECT COUNT(DISTINCT e)
                FROM tasks, unnest(employees) AS e
            """)

            print("Zaraz zwracam dane:")
            print({
                "total_tasks": total_tasks,
                "total_employees": unique_employees,
                "completed_statuses": completed,
                "pending_statuses": pending
            })

            return {
                "type": "admin",
                "total_tasks": total_tasks,
                "total_employees": unique_employees,
                "completed_statuses": completed,
                "pending_statuses": pending
            }

        except HTTPException as e:
            if e.status_code != 403:
                raise e  # inny błąd, np. zły token → przerywamy

            # Jeśli to 403, traktuj jako zwykłego użytkownika
            print("Jestem użytkownikiem (nie adminem)")
            assigned = await conn.fetch(
                "SELECT completed FROM task_status WHERE username = $1",
                username
            )
            print("assigned:", assigned)
            total = len(assigned)
            completed = sum(1 for row in assigned if row["completed"] is True)
            pending = total - completed
            rate = round((completed / total) * 100, 2) if total > 0 else 0

            return {
                "type": "user",
                "assigned_tasks": total,
                "completed": completed,
                "pending": pending,
                "completion_rate": rate
            }

    except Exception as e:
        print("Błąd:", e)
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        if conn:
            await conn.close()


@app.get("/health")
def health():
    return {"status": "OK"}


