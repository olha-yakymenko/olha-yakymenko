import React, { useState, useEffect, useContext, useMemo } from 'react';
import { ShopContext } from '../../Context/ShopContext';
import { UserContext } from '../../Context/UserContext';
import useMessageHandler from '../Admin/hooks/useMessageHandler';
import remove_icon from '../Assets/cart_cross_icon.png';
import Order from '../Order/Order';
import { Link } from 'react-router-dom';
import './CartItems.css';

const CartItems = () => {
    const { getTotalCartAmount, all_product, cartItems, removeFromCart, setCartItems } = useContext(ShopContext);
    const { user } = useContext(UserContext);
    const email = user?.email;
    const {message, setMessage} = useMessageHandler();
    const [userCart, setUserCart] = useState(cartItems);
    const [showOrder, setShowOrder] = useState(false);
    const [promoCode, setPromoCode] = useState("");
    const [discountedTotal, setDiscountedTotal] = useState(false);
    const [availablePromoCodes, setAvailablePromoCodes] = useState([]);
    const [finalTotal, setFinalTotal] = useState(0); 
    const [usedPromoCode, setUsedPromoCode] = useState(false); 
    const [promoCodeError, setPromoCodeError] = useState("");
    
    useEffect(() => {
        const fetchPromoCodes = async () => {
            try {
                const response = await fetch(`/api/get-sale-codes?email=${encodeURIComponent(email)}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                });

                const data = await response.json();
                if (response.ok) {
                    const saleCodes = Array.isArray(data.saleCodes) ? data.saleCodes : [];
                    setAvailablePromoCodes(saleCodes);
                } else {
                    setMessage("Failed to fetch promo codes", "error");
                    
                }
            } catch (error) {
                console.error("Error fetching promo codes:", error);
            }
        };

        if (user?.email) {
            fetchPromoCodes();
        }
    }, [user?.email]);

    const handlePromoCodeSubmit = async () => {
        if (promoCode === "") {
            setMessage("Please enter a promo code.","message");
            return;
        }

        if (availablePromoCodes.includes(promoCode)) {
            if (usedPromoCode) {
                setPromoCodeError("You can only use one promo code per order.");
                return;
            }

            if (!user || !user.email) {
                setMessage( "You must be logged in to apply a promo code.", "message");
                return;
            }

            const email = user.email;

            try {
                const response = await fetch('/api/remove-sale-code', {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ email, saleCode: promoCode })
                });

                const result = await response.json();

                if (response.ok) {
                    const discountedAmount = true
                    setDiscountedTotal(true)
                    setMessage('Promo code applied! You get a 50% discount.',  'message' );
                    setAvailablePromoCodes(availablePromoCodes.filter(code => code !== promoCode));
                    setUsedPromoCode(true);  
                    setPromoCodeError(""); 
                } else {
                    setMessage( result.message || "Error applying promo code",  'message' );
                }
            } catch (error) {
                console.error("Error applying promo code:", error);
            }
        } else {
            setPromoCodeError("Invalid promo code. The price will remain the same.");
        }
    };

    useEffect(() => {
        const fetchUserCart = async () => {
            if (!user) return;

            const email = user.email;

            try {
                const response = await fetch('/api/get-cart', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ email }),
                });

                const result = await response.json();

                if (response.ok) {
                    const fetchedCart = result.cart?.reduce((acc, item) => {
                        acc[item.product_id] = item.quantity;
                        return acc;
                    }, {}) || [];
                    setUserCart(fetchedCart);
                    setCartItems(fetchedCart);
                } 
            } catch (error) {
                console.error('Error fetching cart:', error);
            }
        };

        fetchUserCart();
    }, [user, cartItems]);

    const handleRemoveFromCart = async (productId) => {
        if (!user) {
            setMessage('You must be logged in to remove products from the cart',  'message' );
            return;
        }

        const email = user.email;

        try {
            const response = await fetch('/api/remove-from-cart', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email, productId }),
            });

            const result = await response.json();

            if (response.ok) {
                removeFromCart(productId);
                setMessage(result.message, 'message' );
            } else {
                setMessage(result.message,  'error' );
            }
        } catch (error) {
            console.error('Error removing product from cart:', error);
            setMessage('Error removing product from cart', 'message' );
        }
    };

    const handleProceedToCheckout = () => {
        setShowOrder((prevState) => !prevState);
    };

    const filteredCartItems = useMemo(() => {
        return all_product.filter((product) => cartItems[product.id] > 0);
    }, [all_product, cartItems]);

    const totalCartAmount = useMemo(() => getTotalCartAmount(), [cartItems, getTotalCartAmount]);

    useEffect(() => {
        const updatedTotal = discountedTotal  ? totalCartAmount*0.5 : totalCartAmount;
        setFinalTotal(updatedTotal);
    }, [discountedTotal, totalCartAmount]);

    const clearCart = async () => {
        if (!user) {
            setMessage( 'You must be logged in to clear your cart','message' );
            return;
        }
        
        const email = user.email; 
        try {
            const response = await fetch('/api/clear-cart', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email }), 
            });
  
            const result = await response.json();
  
            if (response.ok) {
                setCartItems({});  
                setMessage( result.message, 'message' ); 
            } else {
                setMessage(result.message, 'error' ); 
            }
        } catch (error) {
            console.error('Error clearing cart:', error);
            setMessage('Error clearing cart', 'error' );
        }
    };

const handleQuantityChange = (productId, newQuantity) => {
    if (typeof newQuantity !== 'number' || newQuantity < 1) {
        setMessage("Ilość musi być co najmniej 1.", 'error' );
        return;
    }

    const updatedCartItems = { ...cartItems, [productId]: newQuantity };
    setCartItems(updatedCartItems);

    if (user && user.email) {
        const email = user.email;
        fetch('/api/update-cart', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, productId, quantity: newQuantity }),
        })
            .then(response => response.json())
            .then(data => {
                if (data.message === "Cart updated successfully") {
                    setMessage("Koszyk zaktualizowano pomyślnie.",'message' );
                } else {
                    setCartItems((prev) => ({ ...prev, [productId]: cartItems[productId] }));
                    setMessage(data.message || "Błąd podczas aktualizacji koszyka.",'error' );
                }
            })
            .catch(error => {
                console.error("Błąd podczas aktualizacji koszyka:", error);
                setCartItems((prev) => ({ ...prev, [productId]: cartItems[productId] }));
                setMessage("Nie udało się zaktualizować koszyka. Spróbuj ponownie.",  'error' );
            });
    }
};

    

    return (
        <div className="cartitems">
            
            <div className="cartitems-format-main">
                <p>Products</p>
                <p>Title</p>
                <p>Price</p>
                <p>Quantity</p>
                <p>Total</p>
                <p>Remove</p>
            </div>
            <hr />
            {filteredCartItems.map((e) => {
                return (
                    <div key={e.id}>
                        <div className="cartitems-format cartitems-format-main">
                            <img src={e.image} alt="" className="carticon-product-icon" />
                            <Link to={`/product/${e.id}`}>
                                <p>{e.name}</p>
                            </Link>
                            <p>${e.new_price}</p>
    
                            <input
                                type="number"
                                min="1"
                                value={cartItems[e.id] || 1} 
                                onChange={(event) => handleQuantityChange(e.id, parseInt(event.target.value))}
                            />

                            <p>${e.new_price * (cartItems[e.id] || 1)}</p>
                            <img
                                className="cartitems-remove-icon"
                                src={remove_icon}
                                onClick={() => handleRemoveFromCart(e.id)}
                                alt="Remove"
                            />
                        </div>
                        <hr />
                    </div>
                );
            })}
    
            <div className="cartitems-down">
            
                <div className="cartitems-total">
                    <h1>Cart Totals</h1>
                    <div>
                        <div className="cartitems-total-item">
                            <p>Subtotal</p>
                            <p>${totalCartAmount}</p>
                        </div>
                        <hr />
                        <div className="cartitems-total-item">
                            <p>Shipping Fee</p>
                            <p>Free</p>
                        </div>
                        <hr />
                        <div className="cartitems-total-item">
                            <h3>Total</h3>
                            <h3>${finalTotal}</h3>
                        </div>
                    </div>
                    <div>
                        <button onClick={handleProceedToCheckout}>PROCEED TO CHECKOUT</button>
                        {showOrder && <Order id={user.id} totalAmount={finalTotal} clearCart={clearCart} cartItems={userCart} />}

                    </div>
                </div>
    
                <div className="cartitems-promocode">
                    {message.text && (
                        <div className={`message ${message.type}`}>
                            {message.text}
                        </div>
                    )}
                    <p>If you have a promo code, enter it here</p>
                    <div className="cartitems-promobox">
                        <input
                            type="text"
                            placeholder="Promo code"
                            value={promoCode}
                            onChange={(e) => setPromoCode(e.target.value)}
                            disabled={usedPromoCode}
                        />
                        <button onClick={handlePromoCodeSubmit} disabled={usedPromoCode}>
                            Apply
                        </button>
                    </div>
                    <div className="cartitems-promo-list">
                        {promoCodeError && <p style={{ color: 'red' }}>{promoCodeError}</p>}
                        <p>Available Promo Codes:</p>
                        <ul>
                            {availablePromoCodes?.length > 0 ? (
                                availablePromoCodes.map((code, index) => (
                                    <li key={index}>{code}</li>
                                ))
                            ) : (
                                <p>No promo codes available</p>
                            )}
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    );
    };

export default CartItems;
