import { useState } from 'react';

const useMessageHandler = () => {
  const [message, setMessage] = useState({ text: '', type: '' });

  const clearMessages = () => {
    setTimeout(() => {
      setMessage({ text: '', type: '' });
    }, 3000);
  };

  const setMessageWithType = (msg, type = 'message') => {
    setMessage({ text: msg, type });
    clearMessages();
  };
  const getMessageStyle = () => {
    switch (message.type) {
      case 'message':
        return { color: 'green', fontWeight: 'bold' };
      case 'error':
        return { color: 'red', fontWeight: 'bold' };
      default:
        return {};
    }
  };

  return {
    message,
    setMessage: setMessageWithType,
    getMessageStyle,
  };
};

export default useMessageHandler;
