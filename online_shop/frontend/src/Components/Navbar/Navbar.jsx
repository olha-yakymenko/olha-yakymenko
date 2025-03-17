import React, { useState, useContext, useEffect, useLayoutEffect } from 'react';
import './Navbar.css';
import logo from '../Assets/logo.png';
import cart_icon from '../Assets/cart_icon.png';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { UserContext } from '../../Context/UserContext';
import Search from '../Search/Search';

const Navbar = () => {
  const [menu, setMenu] = useState("shop");
  const [isMobile, setIsMobile] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false); 
  const { user, logoutUser } = useContext(UserContext);
  const navigate = useNavigate();
  const location = useLocation();

  useLayoutEffect(() => {
    const handleResize = () => {
      console.log(window.innerWidth)
      setIsMobile(window.innerWidth <= 768);
    };

    handleResize();
    window.addEventListener('resize', handleResize);

    return () => {
      window.removeEventListener('resize', handleResize);
    };
  }, []);

  useEffect(() => {
    if (location.pathname === '/') {
      setMenu("shop");
    } else if (location.pathname === '/mens') {
      setMenu("mens");
    } else if (location.pathname === '/womens') {
      setMenu("womens");
    } else if (location.pathname === '/kids') {
      setMenu("kids");
    } else {
      setMenu(null);
    }
  }, [location]);

  const handleLogout = () => {
    logoutUser();
    navigate('/');
  };

  return (
    <div className="navbar">
      <div className="nav-logo">
        <p>YAK</p>
      </div>

      {isMobile ? (
        <div className="burger-menu">
          <button onClick={() => setMenuOpen(!menuOpen)}>
            ☰ 
          </button>
          {menuOpen && (
            <ul className="nav-menu-mobile">
              <li onClick={() => setMenu("shop")}>
                <Link to='/'>Shop</Link>
              </li>
              <li onClick={() => setMenu("mens")}>
                <Link to='/mens'>Men</Link>
              </li>
              <li onClick={() => setMenu("womens")}>
                <Link to='/womens'>Women</Link>
              </li>
              <li onClick={() => setMenu("kids")}>
                <Link to='/kids'>Kids</Link>
              </li>
            </ul>
          )}
        </div>
      ) : (
        <ul className="nav-menu">
          <li onClick={() => setMenu("shop")}>
            <Link style={{ textDecoration: 'none' }} to='/'>Shop</Link>
            {menu === 'shop' ? <hr /> : null}
          </li>
          <li onClick={() => setMenu("mens")}>
            <Link style={{ textDecoration: 'none' }} to='/mens'>Men</Link>
            {menu === 'mens' ? <hr /> : null}
          </li>
          <li onClick={() => setMenu("womens")}>
            <Link style={{ textDecoration: 'none' }} to='/womens'>Women</Link>
            {menu === 'womens' ? <hr /> : null}
          </li>
          <li onClick={() => setMenu("kids")}>
            <Link style={{ textDecoration: 'none' }} to='/kids'>Kids</Link>
            {menu === 'kids' ? <hr /> : null}
          </li>
        </ul>
      )}

      <Search />

      <div className="nav-login-cart">
        {!user ? (
          <Link to='/login'><button>Login</button></Link>
        ) : (
          <>
            <Link to='/cart'><img src={cart_icon} alt="Cart" /></Link>
            <Link to='/order'><img src={logo} alt="Orders" /></Link>
            <button onClick={handleLogout} className="logout-button">Logout</button>
          </>
        )}
      </div>
    </div>
  );
};

export default Navbar;
