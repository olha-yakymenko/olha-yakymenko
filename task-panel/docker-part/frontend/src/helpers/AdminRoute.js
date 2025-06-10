import React, { useState, useEffect } from "react";
import { useKeycloak } from "@react-keycloak/web";


const AdminRoute = ({ children }) => {
  const { keycloak } = useKeycloak();

  console.log('[AdminRoute] Authentication status:', keycloak.authenticated);
  
  if (keycloak.authenticated) {
    console.log('[AdminRoute] User roles:', keycloak.realmAccess?.roles);
    console.log('[AdminRoute] Has admin role:', keycloak.hasResourceRole('admin'));
  }

  const isLoggedIn = keycloak.authenticated && keycloak.realmAccess?.roles.includes('admin');

  return isLoggedIn ? children : null;
};

export default AdminRoute;
