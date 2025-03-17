import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';

import { UserContext } from '../../Context/UserContext';

const AdminNavbar = () => {
  const { logoutUser } = useContext(UserContext);

  return (
    <AppBar position="static" className="nav-logo">
      <Toolbar>
        <Typography variant="h6" sx={{ flexGrow: 1 }}>
          Admin Panel
        </Typography>
        <Button color="inherit" component={Link} to="/admin" className="nav-menu-link">Store Status</Button>
        <Button color="inherit" component={Link} to="/admin/report" className="nav-menu-link">Generate Report</Button>
        <Button color="inherit" component={Link} to="/admin/user-panel" className="user-panel">User Panel</Button>
        <Button color="inherit" component={Link} to="/admin/order-panel" className="order-panel">Orders</Button>
        <Button color="inherit" onClick={logoutUser} component={Link} to="/" className="logout-button">Log Out</Button>
      </Toolbar>
    </AppBar>
  );
};

export default AdminNavbar;
