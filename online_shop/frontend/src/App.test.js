
import React from 'react';
import { render, screen } from '@testing-library/react';
import AppContent from './App';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from 'react-query';
import { UserContext } from './Context/UserContext';
import LoginSignup from './Pages/LoginSignup';
import { ProductContext } from './Context/ProductContext';
import { BrowserRouter } from 'react-router-dom';
test('renders Shop link', () => {
  render(<AppContent />);
  const linkElement = screen.getByText(/Shop/i);
  expect(linkElement).toBeInTheDocument();
});
