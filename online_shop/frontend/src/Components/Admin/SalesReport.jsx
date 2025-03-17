import React, { useState } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import useMessageHandler from './hooks/useMessageHandler'

const SalesReport = () => {
  const [reportData, setReportData] = useState([]);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const { message, setMessage } = useMessageHandler();

  const handleGenerateReport = () => {
    if (!startDate || !endDate) {
      setMessage('Please select both start and end date.', 'error');
      return;
    }

    fetch(`/api/sales-report?startDate=${startDate}&endDate=${endDate}`)
      .then(response => {
        if (!response.ok) {
          throw new Error('Failed to fetch sales report');
        }
        return response.json();
      })
      .then(data => {
        console.log("Fetched data:", data); 
        setReportData(data);
      })
      .catch(err => {
        console.error('Error fetching sales report:', err);
        setMessage('Error fetching report. Please try again.', 'error');
      });
  };

  const filteredProducts = reportData.filter(item =>
    item.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="container my-5">
      <h1 className="text-center mb-4">Sales Report</h1>

      <div className="d-flex justify-content-center mb-4">
        <input
          type="date"
          value={startDate}
          onChange={(e) => setStartDate(e.target.value)}
          className="form-control me-2"
        />
        <input
          type="date"
          value={endDate}
          onChange={(e) => setEndDate(e.target.value)}
          className="form-control me-2"
        />
        <button
          onClick={handleGenerateReport}
          className="btn btn-success"
        >
          Generate Report
        </button>
      </div>

      {message.type='error' && <p className="text-center text-danger">{message.text}</p>}

      <h3 className="text-center my-4">Overall Sales</h3>
      <ResponsiveContainer width="100%" height={400}>
        <LineChart data={reportData}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Line type="monotone" dataKey="totalQuantity" stroke="#8884d8" activeDot={{ r: 8 }} />
          <Line type="monotone" dataKey="totalRevenue" stroke="#82ca9d" />
        </LineChart>
      </ResponsiveContainer>

      <div className="mb-4 text-center">
        <input
          type="text"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="Search for a product"
          className="form-control w-50 mx-auto"
        />
      </div>

      <h3 className="text-center my-4">Sales Data Table</h3>
      <table className="table table-striped">
        <thead className="thead-dark">
          <tr>
            <th>Product Name</th>
            <th>Total Quantity Sold</th>
            <th>Total Revenue</th>
            <th>Date</th>
          </tr>
        </thead>
        <tbody>
          {reportData.length === 0 ? (
            <tr><td colSpan="4" className="text-center">No data available</td></tr>
          ) : (
            reportData.map((row, index) => (
              <tr key={index}>
                <td>{row.name}</td>
                <td>{row.totalQuantity}</td>
                <td>{row.totalRevenue}</td>
                <td>{row.sales.map(sale => sale.date).join(', ')}</td>
              </tr>
            ))
          )}
        </tbody>
      </table>

      {filteredProducts.length > 0 ? (
        filteredProducts.map((product, index) => {
          return (
            <div key={index} className="mb-5">
              <h3 className="text-center">{product.name} Sales</h3>
              <ResponsiveContainer width="100%" height={400}>
                <LineChart data={product.sales}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="date" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Line type="monotone" dataKey="quantity" stroke="#8884d8" activeDot={{ r: 8 }} />
                  <Line type="monotone" dataKey="revenue" stroke="#82ca9d" />
                </LineChart>
              </ResponsiveContainer>
            </div>
          );
        })
      ) : (
        <p className="text-center text-danger">No products found</p>
      )}
    </div>
  );
};

export default SalesReport;
