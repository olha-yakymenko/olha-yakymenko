const express = require('express');
const cors = require('cors'); 
const app = express();
const bodyParser = require('body-parser');
const authRoutes = require('./routes/authRoutes'); 
const cartRoutes = require('./routes/cartRoutes'); 
const dataRoutes = require('./routes/dataRoutes');
app.use(bodyParser.json({limit: '600mb'}));
app.use(bodyParser.urlencoded({ limit: '600mb', extended: true }));
app.use(cors({
  origin: ['https://localhost:1111'], 
  methods: 'GET, POST, PUT, DELETE', 
  credentials: true 
}));

app.use(express.json()); 

app.use('', authRoutes); 
app.use('', cartRoutes); 
app.use('', dataRoutes);




const port = 5055;
app.listen(port, () => {
  console.log(`Server running on http://localhost:${port}`);
});
