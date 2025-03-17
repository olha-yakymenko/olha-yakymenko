import React, { useState, useContext } from 'react';
import { UserContext } from '../Context/UserContext'; 
import { useNavigate } from 'react-router-dom';
import { useMutation, useQueryClient } from 'react-query';
import './CSS/LoginSignup.css'
import useMessageHandler from '../Components/Admin/hooks/useMessageHandler';

const Login = ({ switchToRegister }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const { message, setMessage } = useMessageHandler();
  const { loginUser } = useContext(UserContext);  
  const navigate = useNavigate();  

  const mutation = useMutation(
    async (userData) => {
      const response = await fetch('/api/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
      });
      const result = await response.json();
      if (!response.ok) {
        throw new Error(result.message || 'Invalid credentials');
      }
      return result;
    },
    {
      onSuccess: (result) => {
        loginUser(result.user);
  
        if (result.user.role === 'admin') {
          setMessage('Admin login successful!','message' );
          navigate('/admin');
        } else {
          setMessage( 'Login successful!', 'message' );
          navigate('/');
        }
      },
      onError: (error) => {
        setError(error.message);
      },
    }
  );
  

  const handleLogin = async (e) => {
    e.preventDefault();
    const userData = { email, password };

    if (!email || !password) {
      setError("Email and password are required");
      return;
    }

    mutation.mutate(userData);  
  };

  return (
    <div className="loginsignup-container">
      <h1>Login</h1>
      {message.text && (
            <div className={`message ${message.type}`}>
                {message.text}
            </div>
        )}
      <form onSubmit={handleLogin}>
        <div className="loginsignup-fields">
          <input
            type="email"
            placeholder="Email Address"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        {error && <p style={{ color: 'red' }}>{error}</p>}  
        <button type="submit" disabled={mutation.isLoading}>
          {mutation.isLoading ? 'Logging in...' : 'Login'}
        </button>
      </form>
      <p className="loginsignup-register">
        Don't have an account? <span onClick={switchToRegister}>Register</span>
      </p>
    </div>
  );
};

export default Login;
