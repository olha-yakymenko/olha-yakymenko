import React, { useState, useEffect } from 'react';
import useUsers from './hooks/useUsers';
import useMessageHandler from './hooks/useMessageHandler';

const AdminUserPanel = () => {
  const { users, addSaleCode, removeSaleCode, removeUser, fetchSaleCodes } = useUsers();
  const { message, setMessage, getMessageStyle } = useMessageHandler();

  const [codes, setCodes] = useState({});

  const loadSaleCodes = async (email) => {
    try {
      const saleCodes = await fetchSaleCodes(email); 
      setCodes((prevCodes) => ({ ...prevCodes, [email]: saleCodes }));
    } catch (err) {
      setMessage('Failed to load sale codes', 'error');
    }
  };

  useEffect(() => {
    users.forEach((user) => {
      loadSaleCodes(user.email);
    });
  }, [users]);

  const handleAddSaleCode = async (email) => {
    try {
      const response = await addSaleCode.mutateAsync(email); 
      console.log("Response from backend:", response); 

      if (response && response.saleCode) {
        const saleCode = response.saleCode; 

        setCodes((prevCodes) => {
          const updatedCodes = { ...prevCodes };
          updatedCodes[email] = updatedCodes[email] ? [...updatedCodes[email], saleCode] : [saleCode];
          return updatedCodes;
        });

        setMessage(`Sale code ${saleCode} added for ${email}`);
      } else {
        setMessage('Sale code was not returned from the backend', 'error');
      }
    } catch (error) {
      console.error("Error during addSaleCode:", error);
      setMessage(error.message, 'error');
    }
  };

  const handleRemoveSaleCode = async (email, saleCode) => {
    try {
      await removeSaleCode({ email, saleCode });  

      setCodes((prevCodes) => {
        const updatedCodes = { ...prevCodes };
        updatedCodes[email] = updatedCodes[email]?.filter((code) => code !== saleCode) || [];
        return updatedCodes;
      });

      setMessage(`Sale code ${saleCode} removed for ${email}`);
    } catch (error) {
      setMessage(error.message, 'error');
    }
  };

  return (
    <div className="admin-panel">
      <h1>Administrator Panel</h1>

      {message.text && (
        <p style={getMessageStyle()}>{message.text}</p>
      )}

      <table>
        <thead>
          <tr>
            <th>Email</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Sale Codes</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.length > 0 ? (
            users.map((user) => (
              <tr key={user.email}>
                <td>{user.email}</td>
                <td>{user.firstName}</td>
                <td>{user.lastName}</td>
                <td>
                  <ul>
                    {codes[user.email] && codes[user.email].length > 0 ? (
                      codes[user.email].map((code, index) => (
                        <li key={index}>
                          {code}{" "}
                          <button onClick={() => handleRemoveSaleCode(user.email, code)}>
                            Remove
                          </button>
                        </li>
                      ))
                    ) : (
                      <p>No sale codes</p>
                    )}
                  </ul>
                </td>
                <td>
                  <button onClick={() => handleAddSaleCode(user.email)}>Add Sale Code</button>
                  <button onClick={() => removeUser(user.email)} style={{ color: 'white' }}>
                    Remove User
                  </button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="5">No users found.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default AdminUserPanel;
