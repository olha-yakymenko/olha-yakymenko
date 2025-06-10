import React from "react";
import { useKeycloak } from "@react-keycloak/web";
import "./Nav.css"; 

const Nav = () => {
  const { keycloak } = useKeycloak();

  const isAdmin = () => {
    const roles = keycloak.tokenParsed?.realm_access?.roles || [];
    return roles.includes("admin");
  };

  return (
    <header className="nav-header">
      <nav className="nav-container">
        <div className="nav-left">
          <h1 className="nav-title">Manage tasks app</h1>
        </div>

        <ul className="nav-links">
          <li><a href="/">Home</a></li>
          <li><a href="/secured">My Tasks</a></li>
          {isAdmin() && <li><a href="/admin">Admin Panel</a></li>}
        </ul>

        <div className="nav-auth">
          {!keycloak.authenticated ? (
            <button onClick={() => keycloak.login()} className="btn-login">
              Login
            </button>
          ) : (
            <>
              <span className="username">
                {keycloak.tokenParsed?.preferred_username}
              </span>
              <button onClick={() => keycloak.logout()} className="btn-logout">
                Logout
              </button>
            </>
          )}
        </div>
      </nav>
    </header>
  );
};

export default Nav;
