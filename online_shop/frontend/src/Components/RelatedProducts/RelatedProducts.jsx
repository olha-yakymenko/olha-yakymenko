import { React, useContext } from 'react';
import './RelatedProducts.css';
import { ProductContext } from '../../Context/ProductContext';   
import Item from '../Item/Item';

const RelatedProducts = (props) => {
    const { product } = props;
    const {all_product} = useContext(ProductContext)

    const cat_product = all_product
        .map((item) => {
            return {
                ...item,
                isAvailable: item ? item.isavailable : false,
            };
        })
        .filter((item) => item.isavailable);
    const currentProduct = cat_product.find(item => item.id === product.id);
    if (!currentProduct) {
        return <div>Produkt nie znaleziony</div>;
    }

    const relatedProducts = all_product.filter(item => {
        return item.id !== currentProduct.id && 
               item.category === currentProduct.category && 
               item.name.toLowerCase().includes(currentProduct.name.toLowerCase().split(' ')[0]);
    }).slice(0, 4); 

    return (
        <div className='relatedproducts'>
            <h1>Related Products</h1>
            <hr />
            <div className="relatedproducts-item">
                {relatedProducts.length > 0 ? (
                    relatedProducts.map((item) => {
                        return <Item key={item.id} id={item.id} name={item.name} image={item.image} new_price={item.new_price} old_price={item.old_price} />
                    })
                ) : (
                    <p>Brak powiązanych produktów</p>
                )}
            </div>
        </div>
    );
};

export default RelatedProducts;
