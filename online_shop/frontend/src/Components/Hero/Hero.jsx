import React from 'react';
import './Hero.css'
import banner from '../Assets/banner.png'
const Hero = () => {
  return (
    <div className='hero'>
      <img className='banner' src={banner} alt="" />
    </div>
    
  );
};

export default Hero;
