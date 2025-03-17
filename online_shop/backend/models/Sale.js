const { DataTypes } = require('sequelize');
const sequelize = require('../db'); 
const User = require('./User');
const Sale = sequelize.define(
  'Sale',
  {
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
    sale_code: {
      type: DataTypes.STRING(255),
      allowNull: true,
    },
    created_at: {
      type: DataTypes.DATE,
      defaultValue: DataTypes.NOW,
    },
  },
  {
    tableName: 'sales',
    timestamps: false, 
  }
);

Sale.belongsTo(User, {
    foreignKey: 'user_id',
    onDelete: 'CASCADE',
  });

module.exports = Sale;
