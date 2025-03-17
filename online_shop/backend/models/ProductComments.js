const { DataTypes } = require('sequelize');
const sequelize = require('../db');

const ProductComments = sequelize.define(
  'ProductComments',
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
    },
    product_id: {
      type: DataTypes.INTEGER,
      references: {
        model: 'products',  
        key: 'id',
      },
      onDelete: 'CASCADE', 
      allowNull: false,
    },
    user_name: {
      type: DataTypes.STRING, 
      allowNull: false,
    },
    comment: {
      type: DataTypes.TEXT,
      allowNull: false,
    },
    rating: {
      type: DataTypes.INTEGER,
      allowNull: false,
      validate: {
        min: 1,
        max: 5,  
      },
    },
  },
  {
    tableName: 'product_comments',
    timestamps: false, 
  }
);

module.exports = ProductComments;
