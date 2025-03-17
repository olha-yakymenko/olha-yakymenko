import React, { useContext, useState, useMemo, useCallback } from 'react';
import './ProductDisplay.css';
import star_icon from '../Assets/star_icon.png';
import star_dull_icon from '../Assets/star_dull_icon.png';
import { ShopContext } from '../../Context/ShopContext';
import { UserContext } from '../../Context/UserContext';
import { ProductContext } from '../../Context/ProductContext';
import useMessageHandler from '../Admin/hooks/useMessageHandler';


const ProductDisplay = (props) => {
    const { product } = props;
    const { addToCart } = useContext(ShopContext);
    const { user } = useContext(UserContext);
    const { rating } = useContext(ProductContext);
    const { message, setMessage } = useMessageHandler();
    const [userRating, setUserRating] = useState(0);
    const [likes, setLikes] = useState(product?.likes || []);
    const [sizeSelected, setSize] = useState(''); 

    const averageRating = useMemo(() => {
        if (rating.length > likes.length) {
            setLikes(rating);
        }
        if (Array.isArray(likes) && likes.length > 0) {
            const avgRating = (likes.reduce((acc, curr) => acc + curr, 0) / likes.length).toFixed(1);
            return avgRating;
        }
        return 0;
    }, [likes, rating]);

    async function handleRatingChange(rating) {
        if (!user) {
            setSize('You must be logged in to rate products', 'error');
            return;
        }

        try {
            const response = await fetch('/api/update-product', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    id: product.id,
                    newLike: rating,
                }),
            });

            if (!response.ok) {
                throw new Error('Wystąpił błąd podczas aktualizacji oceny produktu');
            }

            const updatedProduct = await response.json();
            setUserRating(rating);
            setLikes(updatedProduct.likes);
        } catch (error) {
            console.error('Błąd:', error);
        }
    }

    const handleSizeSelect = (sizeOption) => {
        setSize(prevSize => prevSize === sizeOption ? null : sizeOption);
    };

    const handleAddToCart = useCallback(async () => {
        if (!user) {
            setMessage('You must be logged in to add products to the cart', 'message');
            return;
        }
        if (!sizeSelected) {
            setMessage('Please choose a size', 'message');
            return;
        }

        try {
            const response = await fetch('/api/add-to-cart', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email: user.email, productId: product.id }),
            });

            const result = await response.json();

            if (response.ok) {
                addToCart(product);
                setMessage(result.message,'message');
            }
        } catch (error) {
            console.error('Error adding product to cart:', error);
            setMessage( 'Error adding product to cart','error');
        }
    }, [user, product, addToCart, sizeSelected]);

    return (
        <div className="productdisplay">
            <div className="productdisplay-left">
                <div className="productdisplay-img-list">
                    {Array(4).fill(null).map((_, index) => (
                        <img key={index} src={product.image} alt={`img-${index}`} />
                    ))}
                </div>
            </div>
            <div className="productdisplay-image">
                <img className="productdisplay-main-img" src={product.image} alt="main product" />
            </div>
            <div className="productdisplay-right">
                <h1>{product.name}</h1>
                {message.text && <div className={`message ${message.type}`}>{message.text}</div>}

                <div className="productdisplay-right-star">
                    {[1, 2, 3, 4, 5].map((star) => (
                        <img
                            key={star}
                            src={star <= averageRating ? star_icon : star_dull_icon}
                            alt="star"
                        />
                    ))}
                    <p>Average rating: {averageRating}</p>
                </div>

                <div className="productdisplay-rate">
                    <h3>Your Rating</h3>
                    {[1, 2, 3, 4, 5].map((star) => (
                        <img
                            key={star}
                            src={star <= userRating ? star_icon : star_dull_icon}
                            alt="star"
                            onClick={() => handleRatingChange(star)}
                        />
                    ))}
                </div>

                <div className="productdisplay-right-prices">
                    <div className="productdisplay-right-price-old">${product.old_price}</div>
                    <div className="productdisplay-right-price-new">${product.new_price}</div>
                </div>

                <div className="productdisplay-right-sizes">
                    {product?.sizes?.length > 0 ? (
                        product.sizes.map((sizeOption) => (
                            <button
                                key={sizeOption}
                                onClick={() => handleSizeSelect(sizeOption)}
                                className={sizeSelected === sizeOption ? 'selected' : 'notselected'}
                            >
                                {sizeOption}
                            </button>
                        ))
                    ) : (
                        <p>Wait for updates</p>
                    )}
                </div>

                <button className="add" onClick={handleAddToCart}>ADD TO CART</button>
                <p className="productdisplay-right-category">
                    <span>Category:</span> {product.category}
                </p>
                <p className="productdisplay-right-category">
                    <span>Type:</span> {product.type}
                </p>
            </div>
        </div>
    );
};

export default ProductDisplay;
