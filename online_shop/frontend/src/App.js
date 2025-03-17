import React, { useContext } from 'react';
import './App.css';
import Navbar from './Components/Navbar/Navbar';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Shop from './Pages/Shop';
import ShopCategory from './Pages/ShopCategory';
import Product from './Pages/Product';
import Cart from './Pages/Cart';
import LoginSignup from './Pages/LoginSignup';
import SearchResults from './Components/Search/SearchResults';
import SearchContextProvider from './Context/SearchContext';
import banner from './Components/Assets/banner.png';
import { UserProvider, UserContext } from './Context/UserContext';
import ProductList from './Components/Admin/Admin';
import SalesReport from './Components/Admin/SalesReport';
import { QueryClient, QueryClientProvider } from 'react-query';
import  ShopContext  from './Context/ShopContext';
import AdminNavbar from './Components/Navbar/AdminNavbar';
import AdminUserPanel from './Components/Admin/AdminUserPanel';
import  ProductContext from './Context/ProductContext'
import OrderList from './Components/Order/OrderList'
import OrderComponent from './Components/Admin/OrderComponent'
const queryClient = new QueryClient();

const AppContent = () => {
  const { user } = useContext(UserContext);
      console.log('Current user:', user);
  return (
    <BrowserRouter>
      {user?.role === 'admin' ? <AdminNavbar /> : <Navbar />}

      <Routes>
        <Route path="/" element={<Shop />} />
        <Route path="/mens" element={<ShopCategory banner={banner} category="men" />} />
        <Route path="/womens" element={<ShopCategory banner={banner} category="women" />} />
        <Route path="/kids" element={<ShopCategory banner={banner} category="kid" />} />
        <Route path="/product" element={<Product />}>
          <Route path=":productId" element={<Product />} />
        </Route>
        <Route path="/cart" element={<Cart />} />
        <Route path="/login" element={<LoginSignup />} />
        <Route path="/search-results" element={<SearchResults />} />
        {user?.role === 'admin' && (
          <>
            <Route path="/admin" element={<ProductList />} />
            <Route path="/admin/report" element={<SalesReport />} />
            <Route path="/admin/user-panel" element={<AdminUserPanel/>} />
            <Route path="/admin/order-panel" element={<OrderComponent/>} />
          </>
        )}
        <Route path="/order" element={<OrderList/>} />
      </Routes>
    </BrowserRouter>
  );
}

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ProductContext>
      <ShopContext>
      <UserProvider>
        <SearchContextProvider>
          <AppContent />
        </SearchContextProvider>
      </UserProvider>
      </ShopContext>
      </ProductContext>
    </QueryClientProvider>
  );
}

export default App;
