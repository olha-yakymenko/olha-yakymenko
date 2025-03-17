import React, { useState, useEffect, useCallback, useContext } from 'react';
import './Admin.css';
import { ProductContext } from '../../Context/ProductContext';
import AddProductForm from './AddProdutForm';
import useMessageHandler from './hooks/useMessageHandler';

const ProductList = () => {
  const [products, setProducts] = useState([]);
  const [editProduct, setEditProduct] = useState(null); 
  const [showAddForm, setShowAddForm] = useState(false);
  const { all_product, fetchProducts } = useContext(ProductContext);
  const [formValues, setFormValues] = useState({
    image: null,
    sizes: [],
    colors: []  
  });
  const { message, setMessage } = useMessageHandler();

  const availableColors = ["red", "blue", "green", "yellow", "black", "white"];
  const availableSizes = ["XS", "S", "M", "L", "XL"];
  
  useEffect(() => {
    setProducts(
      all_product.sort((a, b) => {
        const idA = a.id ?? 0; 
        const idB = b.id ?? 0; 
        return idA - idB;
      })
    );
  }, [all_product]);
  

  const saveProductToServer = async (product, key) => {
    const updatedValue = product[key] ? true : false;
    const wys = { id: product.id, [key]: updatedValue };
    console.log('Wysyłam na serwer:', wys);

    try {
      const response = await fetch('/api/save-product', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(wys),
      });

      if (!response.ok) {
        console.error(`Błąd serwera: ${response.statusText}`);
      }

      const data = await response.json();
      console.log('Produkt został zapisany na serwerze:', data);
    } catch (error) {
      console.error('Błąd podczas zapisywania produktu:', error);
    }
  };

  const saveEditsToServer = async () => {
    console.log(formValues);
    try {
      const formData = new FormData();
      
      formData.append('id', formValues.id);
      formData.append('name', formValues.name);
      formData.append('category', formValues.category);
      formData.append('description', formValues.description);
      formData.append('new_price', formValues.new_price);
      formData.append('isavailable', formValues.isavailable);
      formData.append('popular', formValues.popular);
      formData.append('new', formValues.new);
      formData.append('type', formValues.type)
      
      if (formValues.sizes && Array.isArray(formValues.sizes)) {
        formValues.sizes.forEach((size, index) => {
          formData.append(`sizes[${index}]`, size);
        });
      }
  
      if (formValues.colors && Array.isArray(formValues.colors)) {
        formValues.colors.forEach((color, index) => {
          formData.append(`colors[${index}]`, color);
        });
      }

      const image = formValues.image;
      const imageSize = Math.ceil((image.length * 3) / 4 / 1024 / 1024);
      console.log('Rozmiar obrazu (MB):', imageSize);
      if (imageSize > 10) {
        setMessage('Obraz jest za duży! Maksymalny rozmiar to 10 MB.', 'error');
      return; 
      }
      const response = await fetch('/api/update-product', {
        method: 'PUT',
        body: formData, 
      });
  
      if (!response.ok) {
        console.error(`Błąd serwera: ${response.statusText}`);
      }
  
      const updatedProduct = await response.json();
      console.log('Produkt zaktualizowany na serwerze:', updatedProduct);
  
      setProducts((prevProducts) =>
        prevProducts.map((product) =>
          product.id === updatedProduct.id ? updatedProduct : product
        )
      );
  
      setEditProduct(null);
      fetchProducts();
    } catch (error) {
      console.error('Błąd podczas aktualizacji produktu:', error);
    }
  };
  
  
  const deleteProduct = async (id) => {
    try {
      const response = await fetch(`/api/delete-product/${id}`, {
        method: 'DELETE'
      });

      if (!response.ok) {
        console.error(`Błąd serwera: ${response.statusText}`);
      }

      const updatedProduct = await response.json();
      console.log('Produkt został usunięty:', updatedProduct);
      fetchProducts()
    } catch (error) {
      console.error('Błąd podczas usunięcia produktu:', error);
    }
  };

  const startEditing = (product) => {
    setEditProduct(product.id);
    setFormValues({
      ...product,
      sizes: product.sizes || [],
      colors: product.colors || [],
    });
  };


  const cancelEditing = () => {
    setEditProduct(null);
    setFormValues({});
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormValues((prevValues) => ({
      ...prevValues,
      [name]: value,
    }));
  };

  const toggleAvailability = useCallback((id) => {
    setProducts((prevProducts) => {
      const updatedProducts = prevProducts.map((product) =>
        product.id === id
          ? { ...product, isavailable: !product.isavailable }
          : product
      );

      const updatedProduct = updatedProducts.find((product) => product.id === id);
      saveProductToServer(updatedProduct, 'isavailable');

      return updatedProducts;
    });
  }, [fetchProducts]);

  const togglePopularity = useCallback((id) => {
    setProducts((prevProducts) => {
      const updatedProducts = prevProducts.map((product) =>
        product.id === id
          ? { ...product, popular: !product.popular }
          : product
      );

      const updatedProduct = updatedProducts.find((product) => product.id === id);
      saveProductToServer(updatedProduct, 'popular');

      return updatedProducts;
    });
  }, [fetchProducts]);

  const toggleNew = useCallback((id) => {
    setProducts((prevProducts) => {
      const updatedProducts = prevProducts.map((product) =>
        product.id === id
          ? { ...product, new: !product.new }
          : product
      );

      const updatedProduct = updatedProducts.find((product) => product.id === id);
      saveProductToServer(updatedProduct, 'new');

      return updatedProducts;
    });
  }, [fetchProducts]);

  const handleFileChange = (event) => {
    const file = event.target.files[0]; 
    if (file) {
      setFormValues({
        ...formValues,
        image: file, 
      });
    }
  };

  const handleCheckboxChange = (e, type) => {
    const { value, checked } = e.target;
    setFormValues((prevValues) => {
      const updatedValues = [...prevValues[type]];
      if (checked) {
        updatedValues.push(value);
      } else {
        const index = updatedValues.indexOf(value);
        if (index > -1) {
          updatedValues.splice(index, 1); 
        }
      }
      return {
        ...prevValues,
        [type]: updatedValues,
      };
    });
  };

  const handleAddProductSuccess = () => {
    fetchProducts();
    setShowAddForm(false);
  };
  

  return (
    <div className="container mt-5">
    <button 
        className="btn btn-primary mb-4" 
        onClick={() => setShowAddForm(prevState => !prevState)} 
      >
        {showAddForm ? 'Ukryj formularz' : 'Dodaj nowy produkt'}
      </button>

      {showAddForm && <AddProductForm onSuccess={handleAddProductSuccess} />}
      <h1 className="text-center mb-4 text-primary">Panel Administratora - Zarządzanie Produktami</h1>
      <table className="table table-striped">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nazwa</th>
            <th>Kategoria</th>
            <th>Cena</th>
            <th>Opis</th>
            <th>Zdjęcie</th>
            <th>Typ</th>
            <th>Status dostępności</th>
            <th>Popularność</th>
            <th>Nowość</th>
            <th>Akcja</th>
          </tr>
        </thead>
        <tbody>
          {products.map((product) => (
            <tr key={product.id}>
              {editProduct === product.id ? (
                <>
                  <td>{product.id}</td>
                  <td>
                    <input
                      type="text"
                      name="name"
                      className="form-control"
                      value={formValues.name || ''}
                      onChange={handleInputChange}
                      placeholder="Nazwa produktu"
                    />
                  </td>
                  <td>
                    <input
                      type="text"
                      name="category"
                      className="form-control"
                      value={formValues.category || ''}
                      onChange={handleInputChange}
                      placeholder="Kategoria"
                    />
                  </td>
                  <td>
                  <input
                    type="number"
                    name="new_price"
                    className="form-control"
                    value={formValues.new_price || ''}
                    onChange={handleInputChange}
                    placeholder="Cena"
                    style={{ width: '100%' }}
                  />
                  </td>
                  <td>
                    <textarea
                      name="description"
                      className="form-control"
                      value={formValues.description || ''}
                      onChange={handleInputChange}
                      placeholder="Opis produktu"
                    />
                  </td>
                  <td>
                    <input
                      type="file"
                      name="image"
                      accept="image/*"
                      className="form-control"
                      onChange={handleFileChange}
                    />
                  </td>
                  <td>
                  <textarea
                      name="type"
                      className="form-control"
                      value={formValues.type || ''}
                      onChange={handleInputChange}
                      placeholder="Typ produktu"
                    />
                  </td>
                  <td>
                    {availableSizes.map((size) => (
                      <div key={size} className="form-check form-check-inline">
                        <input
                          type="checkbox"
                          value={size}
                          className="form-check-input"
                          checked={formValues.sizes.includes(size)}
                          onChange={(e) => handleCheckboxChange(e, 'sizes')}
                        />
                        <label className="form-check-label">{size}</label>
                      </div>
                    ))}
                  </td>
                  <td>
                    {availableColors.map((color) => (
                      <div key={color} className="form-check form-check-inline">
                        <input
                          type="checkbox"
                          value={color}
                          className="form-check-input"
                          checked={formValues.colors.includes(color)}
                          onChange={(e) => handleCheckboxChange(e, 'colors')}
                        />
                        <label className="form-check-label">{color}</label>
                      </div>
                    ))}
                  </td>
                  <td colSpan="4" className="text-center">
                    <button className="btn btn-success" onClick={saveEditsToServer}>
                      Zapisz
                    </button>
                    <button className="btn btn-danger ml-3" onClick={cancelEditing}>
                      Anuluj
                    </button>
                  </td>
                </>
              ) : (
                <>
                  <td>{product.id}</td>
                  <td>{product.name}</td>
                  <td>{product.category}</td>
                  <td>{product.new_price}</td>
                  <td>{product.description}</td>
                  <td>
                    <img
                      src={product.image}
                      alt={product.name}
                      style={{ width: '50px', borderRadius: '5px' }}
                    />
                  </td>
                  <td>{product.type}</td>
                  <td>{product.isavailable ? 'Dostępny' : 'Niedostępny'}</td>
                  <td>{product.popular ? 'Popularny' : 'Niepopularny'}</td>
                  <td>{product.new ? 'Nowy' : 'Nie nowy'}</td>
                  <td>
                    <button
                      className="btn btn-info mr-2"
                      onClick={() => {
                        return toggleAvailability(product.id)
                      }}
                    >
                      {product.isavailable ? 'Zmień na niedostępny' : 'Zmień na dostępny'}
                    </button>
                    <button
                      className="btn btn-warning mr-2"
                      onClick={() => togglePopularity(product.id)}
                    >
                      {product.popular ? 'Usuń popularność' : 'Ustaw jako popularny'}
                    </button>
                    <button
                      className="btn btn-primary mr-2"
                      onClick={() => toggleNew(product.id)}
                    >
                      {product.new ? 'Zmień na nie nowy' : 'Oznacz jako nowy'}
                    </button>
                    <button
                      className="btn btn-info mr-2"
                      onClick={() => {
                        return deleteProduct(product.id)
                      }}
                    >
                      Usuń
                    </button>
                    <button
                      className="btn btn-secondary"
                      onClick={() => startEditing(product)}
                    >
                      Edytuj
                    </button>
                    
                  </td>
                </>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ProductList;
