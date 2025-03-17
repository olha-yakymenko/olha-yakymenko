import React, { createContext, useState, useEffect } from 'react';

export const ProductContext = createContext(null);

const ProductContextProvider = ({ children }) => {
  const [all_product, setAllProduct] = useState([]);
  const [rating, setAverageRating] = useState(0);

const fetchProducts = async () => {
      try {
        const response = await fetch('/api/products');
        const data = await response.json();
        console.log(data)
        setAllProduct(data);
      } catch (error) {
        console.error('Error fetching products:', error);
      }
    };
  useEffect(() => {

    fetchProducts();
  }, []); 

  const updateAverageRating = (newRating) => {
    setAverageRating(newRating);
};

  return (
    <ProductContext.Provider value={{ all_product, fetchProducts, updateAverageRating, rating }}>
      {children}
    </ProductContext.Provider>
  );
};

export default ProductContextProvider;
