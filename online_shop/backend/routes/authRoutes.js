const express = require('express');
const bcrypt = require('bcrypt');
const router = express.Router();
const User = require('../models/User');
const Cart = require('../models/Cart');

router.post('/api/login', async (req, res) => {
    const { email, password } = req.body;
  
    try {
      const user = await User.findOne({
        where: { email }
      });
      console.log(user)
   
  
      if (!user) {
        return res.status(400).json({ message: 'Invalid credentials' });
      }
  
      const isPasswordValid = await bcrypt.compare(password, user.password);
  
      if (!isPasswordValid) {
        console.log("tuta")
        return res.status(400).json({ message: 'Invalid credentials' });
      }
  
      const cart = await Cart.findOne({
        where: { user_id: user.id }
      });
  
      res.status(200).json({
        message: 'Login successful',
        user,
        cart 
      });
    } catch (err) {
      console.error('Error during login:', err);
      res.status(500).json({ message: 'Error processing request' });
    }
  });
  
  router.post('/api/register', async (req, res) => {
    const { firstName, lastName, email, password } = req.body;
  
    try {
      const existingUser = await User.findOne({
        where: { email }
      });
  
      if (existingUser) {
        return res.status(400).json({ message: 'User already exists' });
      }
  
      const hashedPassword = await bcrypt.hash(password, 10);
  
      const newUser = await User.create({
        first_name: firstName,
        last_name: lastName,
        email,
        password: hashedPassword,
        is_authenticated: false,
        role: 'user',
      });
  
  
      res.status(201).json({
        message: 'User registered successfully',
        user: {
          id: newUser.id,
          first_name: newUser.first_name,
          last_name: newUser.last_name,
          email: newUser.email,
          role: newUser.role,
          is_authenticated: newUser.is_authenticated,
        },
      });
    } catch (err) {
      console.error('Error during registration:', err);
      res.status(500).json({ message: 'Error processing request' });
    }
  });
  
  router.post('/api/logout', async (req, res) => {
    const { email } = req.body;
  
    try {
      const user = await User.findOne({ where: { email } });
  
      if (!user) {
        return res.status(400).json({ message: 'User not found' });
      }
  
      user.is_authenticated = false;
  
      await user.save();
  
      res.status(200).json({ message: 'Logout successful' });
    } catch (err) {
      console.error('Error during logout:', err);
      res.status(500).json({ message: 'Error processing request' });
    }
  });
  
  router.get('/api/isAuthenticated', async (req, res) => {
    const { email } = req.query;
  
    try {
      const user = await User.findOne({ where: { email } });
  
      if (!user) {
        return res.status(400).json({ message: 'User not found' });
      }
  
      res.status(200).json({ isAuthenticated: user.is_authenticated });
    } catch (err) {
      console.error('Error during isAuthenticated check:', err);
      res.status(500).json({ message: 'Error processing request' });
    }
  });
  
  module.exports = router;