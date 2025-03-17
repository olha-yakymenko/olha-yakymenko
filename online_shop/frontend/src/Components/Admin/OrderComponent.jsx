import React, { useState, useEffect } from 'react';
import useMessageHandler from './hooks/useMessageHandler'
const OrdersComponent = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const { message, setMessage } = useMessageHandler();

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const response = await fetch('/api/orders');
        if (!response.ok) {
          throw new Error('Błąd podczas pobierania zamówień');
        }
        const data = await response.json();
        console.log(data);
        const sortedOrders = data.sort((a, b) => a.id - b.id);
        setOrders(sortedOrders);
      } catch (error) {
        setMessage(error.message, 'error');
      } finally {
        setLoading(false);
      }
    };
    fetchOrders();
  }, []);

  const changeStatus = async (id, newStatus) => {
    try {
      const response = await fetch(`/api/orders/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ status: newStatus }),
      });

      if (!response.ok) {
        throw new Error('Błąd podczas zmiany statusu');
      }

      const updatedOrder = await response.json();
      setOrders((prevOrders) =>
        prevOrders.map((order) =>
          order.id === updatedOrder.order.id ? updatedOrder.order : order
        )
      );
    } catch (error) {
        setMessage(error.message, 'error');
    }
  };

  const getStatusText = (status) => {
    switch (status) {
      case 'W trakcie':
        return 'W trakcie';
      case 'Zrealizowane':
        return 'Zrealizowane';
      case 'Anulowane':
        return 'Anulowane';
      case 'Przyjęte':
        return 'Przyjęte';
      case 'Wysłany':
        return 'Wysłany';
      default:
        return 'Nieznany status';
    }
  };

  return (
    <div>
      <h1>Lista Zamówień</h1>
      {loading && <p>Ładowanie...</p>}
      {message.type==='error' && <p>Wystąpił błąd: {message.text}</p>}
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Produkty</th>
            <th>Status</th>
            <th>Zmiana Statusu</th>
          </tr>
        </thead>
        <tbody>
          {orders.map((order) => (
            <tr key={order.id}>
              <td>{order.id}</td>
              <td>{order.products}</td>
              <td>{getStatusText(order.status)}</td> 
              <td>
                <select
                  value={order.status}
                  onChange={(e) => changeStatus(order.id, e.target.value)}
                >
                  <option value="Przyjęty">Przyjęty</option>
                  <option value="W trakcie">W trakcie</option>
                  <option value="Wysłany">Wysłany</option>
                  <option value="Zrealizowane">Zrealizowane</option>
                  <option value="Anulowane">Anulowane</option>
                </select>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default OrdersComponent;
