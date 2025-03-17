const { Sequelize } = require('sequelize');

const sequelize = new Sequelize('online_shop', 'postgres', '1', {
  host: 'localhost', 
  dialect: 'postgres',
  logging: false 
});

module.exports = sequelize;
