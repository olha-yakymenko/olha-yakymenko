import React from 'react';
import { Link } from 'react-router-dom';  
import './Breadcrum.css';   
import arrow_icon from '../Assets/breadcrum_arrow.png';

const Breadcrum = (props) => {
    const { product } = props;

    return (
        <div className='breadcrum'>
            <Link to="/" className="breadcrum-link">
                SHOP
            </Link>
            <img src={arrow_icon} alt="arrow" />
            <Link to={`/${product.category}s`} className="breadcrum-link">
                {product.category}
            </Link>
            <img src={arrow_icon} alt="arrow" />
            <span>{product.name}</span>
        </div>
    );
};

export default Breadcrum;
