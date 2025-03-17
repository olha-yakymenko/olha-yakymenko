import React from 'react';
import './NewCollections.css'
import {useContext} from 'react'
import { ProductContext } from '../../Context/ProductContext';
import Item from '../Item/Item'
const NewCollections = () => {
    const {all_product}=useContext(ProductContext)
    const cat_product = all_product
        .map((item) => {
            return {
                ...item,
                isavailable: item ? item.isavailable : false,
                new: item ? item.new : false,

            };
        })
        .filter((item) =>  item.isavailable && item.new);
        console.log(cat_product)
    return (
        <div className='new-collections'>
            <h1>NEW COLLECTIONS</h1>
            <hr />
            <div className="collections">
                {cat_product.map((item, i) =>{
                    return <Item key={i} id={item.id} name={item.name} image={item.image} new_price={item.new_price} old_price={item.old_price}/>
                })}
            </div>
        </div>
    );
};

export default NewCollections;
