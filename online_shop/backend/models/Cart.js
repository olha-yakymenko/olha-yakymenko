const { DataTypes } = require('sequelize');
const sequelize = require('../db');
const User = require('./User');

const Cart = sequelize.define('Cart', {
  id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    autoIncrement: true,
  },
  user_id: {
    type: DataTypes.INTEGER,
    allowNull: true, 
    references: {
      model: User, 
      key: 'id',  
    },
    onDelete: 'CASCADE', 
  },
  product_id: {
    type: DataTypes.INTEGER,
    allowNull: true, 
  },
  quantity: {
    type: DataTypes.INTEGER,
    allowNull: true,
    defaultValue: 1,  
  },
  added_at: {
    type: DataTypes.DATE,
    allowNull: true,
    defaultValue: DataTypes.NOW, 
  },
}, {
  tableName: 'carts',
  timestamps: false,  
});

Cart.belongsTo(User, { foreignKey: 'user_id' });
User.hasMany(Cart, { foreignKey: 'user_id' });

module.exports = Cart;
