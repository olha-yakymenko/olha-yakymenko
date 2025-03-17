import React, { useContext, useRef, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { SearchContext } from "../../Context/SearchContext";
import { ProductContext } from '../../Context/ProductContext'; 
import './Search.css';

const Search = () => {
    const { setSearchResults, setSearchTerm, searchTerm } = useContext(SearchContext);  
    const searchRef = useRef(null);
    const navigate = useNavigate();
    const {all_product}=useContext(ProductContext)


    const getAvailableProducts = () => {
        return all_product
            .map((item) => {
                return {
                    ...item,
                    isavailable: item ? item.isavailable : false,
                };
            })
            .filter(item => item.isavailable === true);
    };

    const filterProducts = (products, term) => {
        return products.filter((product) => {
            const nameMatch = product.name?.toLowerCase().includes(term);
            const descriptionMatch = product.description?.toLowerCase().includes(term);
            const priceMatch = product.new_price?.toString().includes(term);
            const categoryMatch = product.category?.toLowerCase().includes(term);
            const typeMatch = product.type?.toLowerCase().includes(term);
            return nameMatch || descriptionMatch || priceMatch || categoryMatch || typeMatch;
        });
    };

    const handleSearchChange = (e) => {
        const term = e.target.value.toLowerCase();
        setSearchTerm(term);

        const availableProducts = getAvailableProducts();
        const filteredResults = filterProducts(availableProducts, term);
        
        setSearchResults(filteredResults);
    };

    const handleKeyDown = (e) => {
        if (e.key === "Enter") {
            e.preventDefault();

            const availableProducts = getAvailableProducts();
            const filteredResults = filterProducts(availableProducts, searchTerm);
            
            setSearchResults(filteredResults);
            setSearchTerm(""); 
            navigate("/search-results");
        }
    };

    const handleClickOutside = (event) => {
        if (searchRef.current && !searchRef.current.contains(event.target)) {
            setSearchTerm(""); 
        }
    };

    useEffect(() => {
        document.addEventListener("click", handleClickOutside, true);
        return () => {
            document.removeEventListener("click", handleClickOutside, true);
        };
    }, []);

    const filteredSuggestions = searchTerm
        ? filterProducts(getAvailableProducts(), searchTerm)
        : [];

    return (
        <div className="nav-search" ref={searchRef}>
            <input
                type="text"
                id="search"
                value={searchTerm}
                placeholder="Szukaj produktów..."
                onChange={handleSearchChange}
                onKeyDown={handleKeyDown} 
            />
            {searchTerm && filteredSuggestions.length > 0 && (
                <div className="search-results-list">
                    {filteredSuggestions.map((product) => (
                        <div key={product.id} className="search-result-item">
                            <Link to={`/product/${product.id}`}>{product.name}</Link>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default Search;
