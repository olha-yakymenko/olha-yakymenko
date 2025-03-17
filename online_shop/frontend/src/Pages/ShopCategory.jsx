import React, { useContext, useState, useEffect } from 'react';
import { ShopContext } from '../Context/ShopContext';
import Item from '../Components/Item/Item';
import './CSS/ShopCategory.css';

const ShopCategory = (props) => {
    const { all_product } = useContext(ShopContext);
    
    const categoryTypes = all_product.reduce((acc, product) => {
        const { category, type } = product;
            if (!acc[category]) {
            acc[category] = [];
        }
            if (!acc[category].includes(type)) {
            acc[category].push(type);
        }
    
        return acc;
    }, {}); 
    
    const [selectedType, setSelectedTypes] = useState([]);
    const [sortCriteria, setSortCriteria] = useState("price_asc");
    const [filters, setFilters] = useState({
        size: [],
        color: [],
        priceRange: [0, 1000],
        category: props.category || "all",
    });

    const cat_product = all_product
        .map((item) => {
            return {
                ...item,
                isavailable: item ? item.isavailable : false,
                new: item ? item.new : false,

            };
        })
        .filter((item) => props.category === item.category && item.isavailable);
    const sortProducts = (products, criteria) => {
        switch (criteria) {
            case "price_asc":
                return [...products].sort((a, b) => a.new_price - b.new_price);
            case "price_desc":
                return [...products].sort((a, b) => b.new_price - a.new_price);
            case "name_asc":
                return [...products].sort((a, b) => a.name.localeCompare(b.name));
            case "name_desc":
                return [...products].sort((a, b) => b.name.localeCompare(a.name));
            default:
                return products;
        }
    };

    const filterByCategory = (products) => {
        return products.filter((product) => {
            console.log(product)
            const matchesType =
            selectedType.length === 0 || selectedType.includes(product.type);


            const matchesSize =
                filters.size.length === 0 ||
                (product.sizes &&
                    filters.size.every((size) => product.sizes.includes(size)));

            const matchesColor =
                filters.color.length === 0 ||
                (product.colors &&
                    filters.color.every((color) => product.colors.includes(color)));

            const matchesPrice =
                product.new_price >= filters.priceRange[0] && product.new_price <= filters.priceRange[1];

            return matchesType && matchesSize && matchesColor && matchesPrice;
        });
    };

    const handleFilterChange = (filterType, value) => {
        setFilters((prevFilters) => {
            if (filterType === "size" || filterType === "color") {
                const updatedValues = prevFilters[filterType].includes(value)
                    ? prevFilters[filterType].filter((v) => v !== value)
                    : [...prevFilters[filterType], value];
                return { ...prevFilters, [filterType]: updatedValues };
            }
            if (filterType === "priceRange") {
                return { ...prevFilters, priceRange: value };
            }
            return prevFilters;
        });
    };

    const filteredProducts = filterByCategory(cat_product);
    const sortedProducts = sortProducts(filteredProducts, sortCriteria);

    const handleTypeClick = (type) => {
        setSelectedTypes(prev => prev.includes(type) ? prev.filter(t => t!==type) : [...prev, type]); 
    };

    return (
        <div className="shop-category">
            <div className="product-types">
                {(categoryTypes[props.category] || []).map((type) => (
                    <button
                        key={type}
                        className={`type-button ${selectedType.includes(type) ? "active" : ""}`}
                        onClick={() => handleTypeClick(type)}
                    >
                        {type}
                    </button>
                ))}
            </div>

            <div className="search-results-sort">
                <p>
                    <span>Showing {sortedProducts.length}</span> out of {cat_product.length} products
                </p>
                <div className="sort-options">
                    <label>Sort by:</label>
                    <select value={sortCriteria} onChange={(e) => setSortCriteria(e.target.value)}>
                        <option value="price_asc">Price: Low to High</option>
                        <option value="price_desc">Price: High to Low</option>
                        <option value="name_asc">Name: A to Z</option>
                        <option value="name_desc">Name: Z to A</option>
                    </select>
                </div>
            </div>

            <div className="shop-main">
                <div className="filters-section">
                    <h4>Filters</h4>
                    <div>
                        <h5>Size</h5>
                        {["XS", "S", "M", "L", "XL"].map((size) => (
                            <label key={size}>
                                <input
                                    type="checkbox"
                                    value={size}
                                    checked={filters.size.includes(size)}
                                    onChange={() => handleFilterChange("size", size)}
                                />
                                {size}
                            </label>
                        ))}
                    </div>
                    <div>
                        <h5>Color</h5>
                        {["red", "blue", "green", "yellow", "black", "white"].map((color) => (
                            <label key={color}>
                                <input
                                    type="checkbox"
                                    value={color}
                                    checked={filters.color.includes(color)}
                                    onChange={() => handleFilterChange("color", color)}
                                />
                                {color}
                            </label>
                        ))}
                    </div>
                    <div>
                        <h5>Price Range</h5>
                        <input
                            type="range"
                            min="0"
                            max="1000"
                            step="10"
                            value={filters.priceRange[1]}
                            onChange={(e) =>
                                handleFilterChange("priceRange", [filters.priceRange[0], Number(e.target.value)])
                            }
                        />
                        <span>${filters.priceRange[0]} - ${filters.priceRange[1]}</span>
                    </div>
                </div>

                <div className="shopcategory-products">
                    {sortedProducts.map((item, i) => (
                        <Item
                            key={i}
                            id={item.id}
                            name={item.name}
                            image={item.image}
                            new_price={item.new_price}
                            old_price={item.old_price}
                        />
                    ))}
                </div>
            </div>
        </div>
    );
};

export default ShopCategory;
