import React, { createContext, useState } from "react";

export const SearchContext = createContext(null);

const SearchContextProvider = (props) => {
    const [searchResults, setSearchResults] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");

    const contextValue = {
        searchResults,
        setSearchResults,
        searchTerm,
        setSearchTerm,
    };

    return (
        <SearchContext.Provider value={contextValue}>
            {props.children}
        </SearchContext.Provider>
    );
};

export default SearchContextProvider;


