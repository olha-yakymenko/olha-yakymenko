import Keycloak from "keycloak-js";

const keycloak = new Keycloak({
  url: "http://localhost:8080",
  realm: "task-realm",
  clientId: "frontend",
  pkceMethod: 'S256'
});

export default keycloak;
