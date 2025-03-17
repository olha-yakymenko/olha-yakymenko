const express = require('express');
const router = express.Router();
const { Op } = require('sequelize');
const User = require('../models/User');
const Sale = require('../models/Sale');
const Product = require('../models/Product')
const ProductComments = require('../models/ProductComments')
const Order = require('../models/Order')
router.post('/api/save-product', async (req, res) => {
    const { id, isavailable, popular, new: isNew } = req.body;
    console.log(id, isavailable, popular, isNew);
    const updates = {};
    if (typeof isavailable !== 'undefined') updates.isavailable = isavailable;
    if (typeof popular !== 'undefined') updates.popular = popular;
    if (typeof isNew !== 'undefined') updates.new = isNew;
  
    try {
      const updatedProduct = await Product.update(updates, {
        where: { id },
        returning: true, 
      });
  
      if (!updatedProduct[0]) {
        return res.status(404).send('Produkt nie został znaleziony');
      }
      res.status(200).json(updatedProduct[1][0]);
    } catch (error) {
      console.error(error);
      res.status(500).send('Błąd serwera');
    }
  });

  
router.delete('/api/delete-user', async (req, res) => {
    const { email } = req.body;
  
    try {
      const user = await User.findOne({
        where: { email },
      });
  
      if (!user) {
        return res.status(404).json({ message: 'User not found' });
      }
  
      await user.destroy();
  
      res.status(200).json({ message: 'User deleted successfully' });
    } catch (err) {
      console.error('Error during user deletion:', err);
      res.status(500).json({ message: 'Error processing request' });
    }
  });
  
  const crypto = require('crypto'); 
  
  const generateSaleCode = () => {
    return crypto.randomBytes(3).toString('hex').toUpperCase(); 
  };
  
  router.post('/api/add-sale-code', async (req, res) => {
    const { email } = req.body;
  
    try {
      const user = await User.findOne({
        where: { email, role: { [Op.ne]: 'admin' } },
      });
  
      if (!user) {
        return res.status(404).json({ message: 'User not found' });
      }
  
      const saleCode = generateSaleCode();
  
      const existingSale = await Sale.findOne({
        where: { user_id: user.id, sale_code: saleCode },
      });
  
      if (existingSale) {
        return res.status(400).json({ message: 'Sale code already exists for this user' });
      }
  
      const newSale = await Sale.create({
        user_id: user.id,
        sale_code: saleCode,
      });
  
      res.status(200).json({ message: 'Sale code added successfully', saleCode: newSale.sale_code });
    } catch (err) {
      console.error('Error during sale code addition:', err);
      res.status(500).json({ message: 'Error processing request' });
    }
  });
  
  router.delete('/api/remove-sale-code', async (req, res) => {
    const { email, saleCode } = req.body;
    
    try {
      const user = await User.findOne({
        where: { email, role: 'user' } 
      });
  
      if (!user) {
        return res.status(404).json({ message: 'User not found' });
      }
  
      const sale = await Sale.findOne({
        where: { user_id: user.id, sale_code: saleCode }
      });
  
      if (!sale) {
        return res.status(404).json({ message: 'Sale code not found for this user' });
      }
  
      await sale.destroy();
  
      res.status(200).json({ message: 'Sale code removed successfully', saleCode });
    } catch (err) {
      console.error('Error during sale code removal:', err);
      res.status(500).json({ message: 'Error processing request' });
    }
  });
  
  router.get('/api/get-sale-codes', async (req, res) => {
    const { email } = req.query;  
    console.log(email);
  
    if (!email) {
      return res.status(400).json({ message: 'Email is required' });
    }
  
    try {
      const user = await User.findOne({
        where: { email, role: { [Op.ne]: 'admin' } }, 
      });
  
      if (!user) {
        return res.status(404).json({ message: 'User not found' });
      }
  
      const sales = await Sale.findAll({
        where: { user_id: user.id },
      });
  
      if (sales.length > 0) {
        const saleCodes = sales.map(sale => sale.sale_code);
        res.status(200).json({ saleCodes });
      } else {
        res.status(200).json({ message: 'No sale codes found for this user' });
      }
    } catch (err) {
      console.error('Error during fetching sale codes:', err);
      res.status(500).json({ message: 'Error processing request' });
    }
  });
  
  router.get('/api/get-users', async (req, res) => {
    try {
      const users = await User.findAll({
        where: {
          role: {
            [Op.ne]: 'admin', 
          },
        },
        attributes: ['id', 'first_name', 'last_name', 'email', 'role'],
      });
  
      res.status(200).json(users);
    } catch (err) {
      console.error('Error fetching users:', err);
      res.status(500).json({ message: 'Error fetching users' });
    }
  });

  
router.get('/api/product/:id', async (req, res) => {
    const productId = req.params.id;
  
    try {
      const product = await Product.findOne({ where: { id: productId } });
  
      if (!product) {
        return res.status(404).json({ message: 'Product not found' });
      }
  
      const imageBuffer = product.image;
  
      const imageType = 'image/png'; 
  
      res.setHeader('Content-Type', imageType);
  
      res.send(imageBuffer);
    } catch (error) {
      console.error('Error fetching product:', error);
      res.status(500).json({ message: 'Error fetching product' });
    }
  });
  
  router.get('/api/products', async (req, res) => {
    try {
      const products = await Product.findAll();
  
      const productsWithImages = products.map(product => {
        let imageBase64 = null;
        if (product.image) {
          imageBase64 = product.image.toString('base64');
        }
        return {
          ...product.toJSON(), 
          image: imageBase64 ? `data:image/jpeg;base64,${imageBase64}` : null 
        };
      });
  
      res.status(200).json(productsWithImages);
    } catch (error) {
      console.error(error);
      res.status(500).json({ message: 'Error fetching products', error: error.message });
    }
  });
  
  router.post('/api/add-comment', async (req, res) => {
    const { product_id, user_name, comment, rating } = req.body;
    console.log(req.body)
    if (!product_id || !user_name || !comment || rating === undefined) {
      return res.status(400).json({ message: 'Missing required fields' });
    }
  
    if (rating < 1 || rating > 5) {
      return res.status(400).json({ message: 'Rating must be between 1 and 5' });
    }
  
    try {
      const newComment = await ProductComments.create({
        product_id,
        user_name,
        comment,
        rating,
      });
  
      res.status(201).json({
        message: 'Comment added successfully',
        comment: newComment,
      });
    } catch (error) {
      res.status(500).json({ message: 'Error saving comment', error: error.message });
    }
  });
  
  const multer = require('multer');
  
  const storage = multer.memoryStorage();
  const upload = multer({
    storage: storage,
    limits: {
      fileSize: 10 * 1024 * 1024, 
      fields: 300, 
      fieldSize: 1024 * 1024 
    }
  });
  
  router.put('/api/update-product', upload.single('image'), async (req, res) => {
    const { id, name, category, new_price, description, newLike,sizes, colors } = req.body;
    console.log("Dane w req.body:", req.body); 
    const image = req.file ? req.file.buffer : null;
  
    const updates = {};
  
    if (name !== undefined) updates.name = name;
    if (category !== undefined) updates.category = category;
    if (new_price !== undefined) {
      const product = await Product.findByPk(id);
      if (product) {
        updates.old_price = product.new_price; 
        updates.new_price = new_price; 
      } else {
        return res.status(404).send('Produkt nie został znaleziony');
      }
    }
    if (description !== undefined) updates.description = description;
    if (description !== undefined) updates.description = description;
    
  
    if (newLike !== undefined) {
      const likeValue = parseInt(newLike, 10);
      if (!isNaN(likeValue) && likeValue >= 1 && likeValue <= 5) {
        const product = await Product.findByPk(id);
        if (product && Array.isArray(product.likes)) {
          updates.likes = [...product.likes, likeValue];
        } else {
          updates.likes = [likeValue];
        }
      } else {
        return res.status(400).send('Nieprawidłowa wartość oceny');
      }
    }
  
    if (Array.isArray(sizes)) {
      updates.sizes = sizes; 
    }
  
    if (Array.isArray(colors)) {
      updates.colors = colors; 
    } 
    if (image) {
      updates.image = image;
    }
  
    try {
      const [updated] = await Product.update(updates, {
        where: { id },
      });
  
      if (!updated) {
        return res.status(404).send('Produkt nie został znaleziony');
      }
  
      const updatedProduct = await Product.findByPk(id);
      res.status(200).json(updatedProduct);
    } catch (error) {
      console.error('Błąd podczas aktualizacji produktu:', error);
      res.status(500).send('Błąd serwera');
    }
  });
  
  
  
  router.post('/api/add-product', upload.single('image'), async (req, res) => {
    const { 
      name, 
      category, 
      price, 
      type,
      description, 
      sizes, 
      colors 
    } = req.body;
    console.log(req.body)
   
    const image = req.file ? req.file.buffer : null;
  
    const updates = {};
  
    updates.name = name;
    updates.category = category;
    updates.new_price = price;
    updates.type = type;
    updates.description = description;
    if (Array.isArray(sizes)) {
      updates.sizes = sizes; 
    }
  
    if (Array.isArray(colors)) {
      updates.colors = colors; 
    } 
    updates.image = image; 
    try {
      const newProduct = await Product.create(updates);
  
      res.status(201).json(newProduct); 
    } catch (error) {
      console.error('Błąd podczas dodawania produktu:', error);
      res.status(500).send('Błąd serwera');
    }
  });
  
  router.delete('/api/delete-product/:id', async (req, res) => {
    const { id } = req.params; 
  
    if (!id) {
      return res.status(400).json({ success: false, message: 'ID is required' });
    }
  
    try {
      const product = await Product.findByPk(id);
  
      if (!product) {
        return res.status(404).json({ success: false, message: 'Product not found' });
      }
  
      await ProductComments.destroy({ where: { product_id: id } });
      await product.destroy();
  
      res.status(200).json({ success: true, message: 'Product deleted successfully' });
    } catch (error) {
      console.error('Error deleting product:', error);
      res.status(500).json({ success: false, message: 'Failed to delete product' });
    }
  });
  
  router.post('/api/orders', async (req, res) => {
    const { user_id, products, address, status, price } = req.body;
  
    if (!user_id || !products || !address) {
      return res.status(400).json({ message: 'Wszystkie dane są wymagane!' });
    }
  
    const orderStatus = status || 'Przyjęte';
  
    try {
      const newOrder = await Order.create({
        user_id,
        status: orderStatus,
        products,
        address,
        price
      });
  
      res.status(201).json(newOrder);
    } catch (error) {
      console.error('Błąd podczas tworzenia zamówienia:', error);
      res.status(500).json({ message: 'Błąd serwera' });
    }
  });
  
  router.get('/api/orders/user/:userId', async (req, res) => {
    const userId = req.params.userId;
  
    try {
      const userOrders = await Order.findAll({
        where: {
          user_id: userId,
        },
        attributes: ['id', 'user_id', 'status', 'products', 'address', 'created_at', 'updated_at', 'price'],
      });
  
      if (userOrders.length === 0) {
        return res.status(200).json({ message: 'Nie znaleziono zamówień dla tego użytkownika' });
      }
  
      const ordersWithProductNames = [];
  
      for (const order of userOrders) {
        const productIds = order.products; 
  
        const products = await Product.findAll({
          where: {
            id: productIds,
          },
          attributes: ['id', 'name'], 
        });
  
        const productNames = products.map((product) => product.name);
        console.log(productNames)
  
        ordersWithProductNames.push({
          ...order.toJSON(), 
          products: productNames,
        });
      }
  
      res.status(200).json(ordersWithProductNames);
    } catch (error) {
      console.error('Błąd podczas pobierania zamówień:', error);
      res.status(500).json({ message: 'Błąd serwera' });
    }
  });
  
  router.get('/api/orders', async (req, res) => {
    try {
      const allOrders = await Order.findAll({
        attributes: ['id', 'user_id', 'status', 'products', 'address', 'created_at', 'updated_at', 'price'],
      });
  
      const enrichedOrders = [];
  
      for (const order of allOrders) {
        let productIds = order.products;
  
        if (!Array.isArray(productIds)) {
          console.error(`Nieprawidłowe dane products dla zamówienia ${order.id}:`, productIds);
          productIds = []; 
        } else {
          productIds = productIds.filter((id) => typeof id === 'number');
        }
  
        const products = await Product.findAll({
          where: {
            id: productIds,
          },
          attributes: ['id', 'name'], 
        });
  
        const productNames = products.map((product) => product.name);
  
        enrichedOrders.push({
          ...order.toJSON(), 
          products: productNames,
        });
      }
  
      if (enrichedOrders.length === 0) {
        return res.status(404).json({ message: 'Brak zamówień w systemie' });
      }
  
      res.status(200).json(enrichedOrders);
    } catch (error) {
      console.error('Błąd podczas pobierania zamówień:', error);
      res.status(500).json({ message: 'Błąd serwera' });
    }
  });
  
  
  router.put('/api/orders/:id', async (req, res) => {
    const { id } = req.params; 
    const { status } = req.body; 
  
    try {
      const order = await Order.findByPk(id);
  
      if (!order) {
        return res.status(404).json({ message: 'Zamówienie nie zostało znalezione' });
      }
  
      order.status = status;
  
      await order.save();
  
      res.status(200).json({ message: 'Status zamówienia został zmieniony', order });
    } catch (error) {
      console.error('Błąd podczas zmiany statusu:', error);
      res.status(500).json({ message: 'Błąd serwera' });
    }
  });
  
  
  module.exports = router;