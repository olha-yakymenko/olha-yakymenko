const { DataTypes } = require('sequelize');
const sequelize = require('../db');
const ProductComments = require('./ProductComments');

const Product = sequelize.define(
  'Product',
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
    },
    name: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    category: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    type: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    image: {
      type: DataTypes.BLOB('long'),
      allowNull: false,
    },
    new_price: {
      type: DataTypes.DECIMAL(10, 2),
      allowNull: false,
    },
    old_price: {
      type: DataTypes.DECIMAL(10, 2),
      allowNull: true,
    },
    description: {
      type: DataTypes.TEXT,
      allowNull: false,
    },
    isavailable: {
      type: DataTypes.BOOLEAN,
      allowNull: false,
      defaultValue: true,  
    },
    popular: {
        type: DataTypes.BOOLEAN,
        allowNull: false,
        defaultValue: true,  
      },
    new: {
        type: DataTypes.BOOLEAN,
        allowNull: false,
        defaultValue: true,  
      },
    sizes: {
        type: DataTypes.ARRAY(DataTypes.STRING),  
        allowNull: true,
      },
    colors: {
        type: DataTypes.ARRAY(DataTypes.STRING), 
        allowNull: true,
      },
    likes: {
        type: DataTypes.ARRAY(DataTypes.INTEGER), 
        allowNull: true,
    }
  },
  {
    tableName: 'products',
    timestamps: false,
  }
);

Product.hasMany(ProductComments, { foreignKey: 'product_id', as: 'comments' });
ProductComments.belongsTo(Product, { foreignKey: 'product_id' });

module.exports = Product;
