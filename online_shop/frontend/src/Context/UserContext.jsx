import React, { createContext, useState, useEffect } from 'react';

export const UserContext = createContext();

export const UserProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const storedUser = localStorage.getItem('loggedUser');
    return storedUser ? JSON.parse(storedUser) : null; 
  });

  const [cart, setCart] = useState([]); 

  const loginUser = (user) => {
    setUser(user); 
    localStorage.setItem('loggedUser', JSON.stringify(user)); 
  };

  const logoutUser = () => {
    setUser(null); 
    setCart([]); 
    localStorage.removeItem('loggedUser'); 
  };

  useEffect(() => {
    const storedCart = localStorage.getItem('cart');
    if (storedCart) {
      setCart(JSON.parse(storedCart));
    }
  }, []);

  useEffect(() => {
    localStorage.setItem('cart', JSON.stringify(cart));
  }, [cart]);

  return (
    <UserContext.Provider value={{ user, loginUser, logoutUser, cart, setCart }}>
      {children}
    </UserContext.Provider>
  );
};
