import React, { useEffect, useState, useContext } from 'react';
import { UserContext } from '../../Context/UserContext';
import './OrderList.css'

const OrderList = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null); 
  const { user } = useContext(UserContext);

  const fetchOrders = async () => {
    try {
      const response = await fetch(`/api/orders/user/${user.id}`);
      if (!response.ok) {
        throw new Error('Nie udało się pobrać zamówień');
      }
      const data = await response.json();
      
      if (data.message && data.message === 'Nie znaleziono zamówień dla tego użytkownika') {
        setOrders([]);
      } else {
        setOrders(data); 
        console.log(data)
      }
    } catch (error) {
      console.error('Błąd pobierania zamówień:', error);
      setError('Wystąpił problem z pobraniem zamówień. Spróbuj ponownie później.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, [user.id]); 

  if (loading) {
    return <div>Ładowanie...</div>;
  }

  return (
    <div>
      <h2>Your Orders</h2>
      {error && <div style={{ color: 'red' }}>{error}</div>}
      {orders.length === 0 ? (
        <p>You do not have any orders</p>
      ) : (
        <ul>
          <div className="order-list">
            {orders.map((order) => (
              <div key={order.id} className="order-item">
                <div>
                  <strong>Order No. {order.id}</strong> (Placed on: {new Date(order.created_at).toLocaleDateString()})
                </div>
                <div className="status">Status: {order.status}</div>
                <div className="address">Delivery Address: {order.address}</div>

                <h3>Products:</h3>
                <ul>
                  {order.products.map((product, index) => (
                    <li key={index}>{product}</li>
                  ))}
                </ul>

                <div className="price">Price: {order.price} PLN</div>
              </div>
            ))}
          </div>
        </ul>
      )}
    </div>
  );
};

export default OrderList;
