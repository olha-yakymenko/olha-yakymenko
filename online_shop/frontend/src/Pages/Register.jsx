import React, { useState } from 'react';
import { useMutation } from 'react-query';  
import useMessageHandler from '../Components/Admin/hooks/useMessageHandler';
import './CSS/LoginSignup.css'

const registerUserApi = async (userData) => {
  const response = await fetch('/api/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(userData),
  });

  const result = await response.json();

  if (!response.ok) {
    throw new Error(result.message || 'Error during registration');
  }

  return result;
};

const Register = ({ switchToLogin }) => {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState(null);
  const {message, setMessage} = useMessageHandler();
  const { mutate, isLoading, isError, error: queryError } = useMutation(registerUserApi, {
    onSuccess: () => {
      setMessage('Registration successful!', 'message');
      setFirstName('');
      setLastName('');
      setEmail('');
      setPassword('');
      setConfirmPassword('');
      setError(null);
    },
    onError: (error) => {
      setError(error.message || 'Error during registration');
    },
  });

  const handleRegister = (e) => {
    e.preventDefault();

    if (password !== confirmPassword) {
      setMessage( "Passwords don't match!", 'error' );
      return;
    }

    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*?&]{8,}$/;
    if (!passwordRegex.test(password)) {
      setMessage("Password must be at least 8 characters long and contain a letter and a number.", 'error' );
      return;
    }

    const userData = { firstName, lastName, email, password };

    mutate(userData);
  };

  return (
    <div className="loginsignup-container">
      <h1>Register</h1>
      {message.text && (
          <div className={`message ${message.type}`}>
              {message.text}
          </div>
      )}

      <form onSubmit={handleRegister}>
        <div className="loginsignup-fields">
          <input
            type="text"
            placeholder="First Name"
            value={firstName}
            onChange={(e) => setFirstName(e.target.value)}
            required
          />
          <input
            type="text"
            placeholder="Last Name"
            value={lastName}
            onChange={(e) => setLastName(e.target.value)}
            required
          />
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
          <input
            type="password"
            placeholder="Confirm Password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required
          />
        </div>
        {error && <p style={{ color: 'red' }}>{error}</p>}
        {isLoading && <p>Loading...</p>} 
        {isError && <p style={{ color: 'red' }}>{queryError.message}</p>}  
        <button type="submit" disabled={isLoading}>Register</button>
      </form>
      <p className="loginsignup-login">
        Already have an account? <span onClick={switchToLogin}>Login</span>
      </p>
    </div>
  );
};

export default Register;
