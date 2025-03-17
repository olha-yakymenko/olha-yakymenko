const express = require('express');
const app = express();
const nodemailer=require('nodemailer')
const router = express.Router();
const User = require('../models/User');
const Cart = require('../models/Cart');
const fs=require('fs').promises
const path = require('path');
const salesDataPath = path.join(__dirname, '../sales_data.json');
router.get('/api/cart', async (req, res) => {
    const { email } = req.query;
  
    try {
      const user = await User.findOne({
        where: { email },
        include: {
          model: Cart, 
          attributes: ['id', 'product_id', 'quantity', 'added_at'] 
        }
      });
  
      if (!user) {
        return res.status(400).json({ message: 'User not found' });
      }
  
      if (!user.is_authenticated) {
        return res.status(403).json({ message: 'Access denied, please log in' });
      }
  
      res.status(200).json({ cart: user.Carts });
    } catch (err) {
      console.error('Error during fetching cart:', err);
      res.status(500).json({ message: 'Error processing request' });
    }
  });
  
  router.post('/api/get-cart', async (req, res) => {
    const { email } = req.body;
  
    try {
      const user = await User.findOne({
        where: { email },
      });
  
      if (!user) {
        return res.status(404).json({ message: 'User not found' });
      }
  
      const cartItems = await Cart.findAll({
        where: { user_id: user.id },
      });
  
      if (!cartItems || cartItems.length === 0) {
        return res.status(200).json({ message: 'Cart is empty' });
      }
  
      const cartData = cartItems.map(item => ({
        product_id: item.product_id,
        quantity: item.quantity,
      }));
  
      res.status(200).json({ cart: cartData });
    } catch (err) {
      console.error('Error fetching cart:', err);
      res.status(500).json({ message: 'Error processing request' });
    }
  });
  
  router.post('/api/add-to-cart', async (req, res) => {
    const { email, productId } = req.body;
  
    try {
      if (!email || !productId) {
        return res.status(400).json({ message: 'Email and productId are required' });
      }
  
      const user = await User.findOne({
        where: { email },
      });
  
      if (!user) {
        return res.status(404).json({ message: 'User not found' });
      }
  
      const cartItem = await Cart.findOne({
        where: { user_id: user.id, product_id: productId }, 
      });
  
      if (cartItem) {
        cartItem.quantity += 1;
        await cartItem.save(); 
      } else {
        await Cart.create({
          user_id: user.id,      
          product_id: productId, 
          quantity: 1,           
        });
      }
  
      res.status(200).json({ message: 'Product added to cart' });
    } catch (err) {
      console.error('Error adding product to cart:', err);
      res.status(500).json({ message: 'Error processing request' });
    }
  });
  
  router.post('/api/update-cart', async (req, res) => {
    const { email, productId, quantity } = req.body;
  
    try {
      const user = await User.findOne({
        where: { email }, 
      });
  
      if (!user) {
        return res.status(404).json({ message: 'User not found' });
      }
  
      const cartItem = await Cart.findOne({
        where: { user_id: user.id, product_id: productId }, 
      });
  
      if (cartItem) {
        cartItem.quantity = quantity;
        await cartItem.save(); 
        console.log("edeted")
        res.status(200).json({ message: 'Cart updated successfully' });
      } else {
        await Cart.create({
          user_id: user.id,
          product_id: productId,
          quantity: quantity,
        });
        res.status(200).json({ message: 'Product added to cart' });
      }
    } catch (err) {
      console.error('Error updating cart:', err);
      res.status(500).json({ message: 'Error processing request' });
    }
  });
  
  router.post('/api/remove-from-cart', async (req, res) => {
    const { email, productId } = req.body;
  console.log(email)
  console.log(productId)
    try {
      const user = await User.findOne({
        where: { email }, 
      });
  
      if (!user) {
        return res.status(404).json({ message: 'User not found' });
      }
  
      const cartItem = await Cart.findOne({
        where: { user_id: user.id, product_id: productId }, 
      });
      console.log(cartItem)
  
      if (!cartItem) {
        return res.status(400).json({ message: 'Product not found in cart' });
      }
  
      await cartItem.destroy();
  
      res.status(200).json({ message: 'Product removed from cart' });
    } catch (err) {
      console.error('Error removing product from cart:', err);
      res.status(500).json({ message: 'Error processing request' });
    }
  });
  
  router.post('/api/clear-cart', async (req, res) => {
    const { email } = req.body;
  
    try {
      const user = await User.findOne({
        where: { email }, 
      });
  
      if (!user) {
        return res.status(404).json({ message: 'User not found' });
      }
  
      await Cart.destroy({
        where: { user_id: user.id }, 
      });
  
      res.status(200).json({ message: 'Cart has been cleared' });
    } catch (err) {
      console.error('Error clearing cart:', err);
      res.status(500).json({ message: 'Error processing request' });
    }
  });
  
  const transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
      user: 'onlineshopyak@gmail.com', 
      pass: 'lrop lcqd wqer xaqm', 
    },
  });
  
  router.post('/api/send-confirmation', (req, res) => {
    const { contactInfo, address, paymentMethod, totalAmount } = req.body;
    const mailOptions = {
      from: 'onlineshopyak@gmail.com',
      to: contactInfo.email, 
      subject: 'Potwierdzenie zamówienia',
      text: `Dziękujemy za złożenie zamówienia! Oto szczegóły:
  
        Adres: ${address.city}, ${address.street} ${address.nr}
        Imię i Nazwisko: ${contactInfo.name} ${contactInfo.surname}
        E-mail: ${contactInfo.email}
        Metoda płatności: ${paymentMethod}
        Kwota całkowita: $${totalAmount}
      `,
    };
  
    transporter.sendMail(mailOptions, (error, info) => {
      if (error) {
        console.error('Error sending email:', error);
        return res.status(500).json({ message: 'Błąd wysyłania e-maila' });
      }
      console.log("sukces potw")
      res.status(200).json({ message: 'Potwierdzenie wysłane' });
    });
  });
  
  
router.get('/api/sales-report', async (req, res) => {
  const { startDate, endDate } = req.query;

  try {
    const data = await fs.readFile(salesDataPath, 'utf-8');
    const sales = JSON.parse(data);
    console.log('Sales data:', sales);  

    const start = startDate ? new Date(startDate) : new Date(0); 
    const end = endDate ? new Date(endDate) : new Date(); 

    console.log('Start date:', start);
    console.log('End date:', end);

    const formatDate = (date) => new Date(date).toISOString().split('T')[0];

    const filteredSales = sales.map((sale) => {
      const filteredSalesForProduct = Object.entries(sale.sales)
        .filter(([date, { quantity, revenue }]) => {
          const saleDate = new Date(date);
          console.log('Sale date:', saleDate);
          return saleDate >= start && saleDate <= end; 
        })
        .map(([date, { quantity, revenue }]) => ({
          date,
          quantity,
          revenue,
        }));

      if (filteredSalesForProduct.length > 0) {
        const totalQuantity = filteredSalesForProduct.reduce((acc, { quantity }) => acc + quantity, 0);
        const totalRevenue = filteredSalesForProduct.reduce((acc, { revenue }) => acc + revenue, 0);

        return {
          name: sale.name,
          totalQuantity,
          totalRevenue,
          sales: filteredSalesForProduct,
        };
      }
      return null; 
    }).filter(Boolean); 

    console.log('Filtered sales:', filteredSales);  

    res.json(filteredSales);
  } catch (err) {
    console.error('Error reading sales data:', err);
    res.status(500).json({ message: 'Error fetching sales report' });
  }
});



router.post('/api/submit-order', async (req, res) => {
  const { address, contactInfo, paymentMethod, totalAmount, cartItems } = req.body;
  console.log('Received order:', req.body);

  try {
    try {
      await fs.access(salesDataPath);
    } catch (err) {
      console.error('Sales data file does not exist:', err);
      return res.status(500).json({ message: 'Sales data file does not exist' });
    }

    const currentSalesData = JSON.parse(await fs.readFile(salesDataPath, 'utf8'));

    const formatDate = (date) => new Date(date).toISOString().split('T')[0];

    cartItems.forEach((item) => {
      const existingProduct = currentSalesData.find((product) => product.name === item.name);

      const transactionDate = formatDate(new Date().toISOString());
      const totalRevenueForItem = item.quantity * item.price;

      if (existingProduct) {
        if (existingProduct.sales[transactionDate]) {
          existingProduct.sales[transactionDate].quantity += item.quantity;
          existingProduct.sales[transactionDate].revenue += totalRevenueForItem;
        } else {
          existingProduct.sales[transactionDate] = {
            quantity: item.quantity,
            revenue: totalRevenueForItem,
          };
        }
        existingProduct.totalQuantity += item.quantity;
        existingProduct.totalRevenue += totalRevenueForItem;
      } else {
        currentSalesData.push({
          name: item.name,
          totalQuantity: item.quantity,
          totalRevenue: totalRevenueForItem,
          sales: {
            [transactionDate]: {
              quantity: item.quantity, 
              revenue: totalRevenueForItem, 
            },
          },
        });
      }
    });

    await fs.writeFile(salesDataPath, JSON.stringify(currentSalesData, null, 2));
    res.status(200).json({ message: 'Zamówienie zapisane' });
  } catch (error) {
    console.error('Error saving order:', error);
    res.status(500).json({ message: 'Błąd zapisu zamówienia' });
  }
});
module.exports = router;