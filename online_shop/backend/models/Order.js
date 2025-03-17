const { DataTypes, INTEGER } = require('sequelize');
const sequelize = require('../db');
const User = require('./User');

const Order = sequelize.define(
  'Order',
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
    },
    user_id: {
      type: DataTypes.INTEGER,
      allowNull: false,
      references: {
        model: 'users', 
        key: 'id',
      },
    },
    status: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    products: {
      type: DataTypes.ARRAY(DataTypes.INTEGER), 
      allowNull: false,
    },
    address: {
      type: DataTypes.STRING,
      allowNull: false, 
    },
    created_at: {
        type: DataTypes.DATE,
        defaultValue: DataTypes.NOW,
      },
      updated_at: {
        type: DataTypes.DATE,
        defaultValue: DataTypes.NOW,
      },
      price:{
        type: DataTypes.NUMERIC(10, 2),
        allowNull: false
      }
      
  },
  {
    tableName: 'orders',
    timestamps: false, 
  }
);

Order.belongsTo(User, { foreignKey: 'user_id' });
User.hasMany(Order, { foreignKey: 'user_id' });

module.exports = Order;
