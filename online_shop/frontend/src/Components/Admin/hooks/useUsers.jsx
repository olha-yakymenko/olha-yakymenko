import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';

const useUsers = () => {
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const queryClient = useQueryClient();

  const fetchUsers = async () => {
    const response = await fetch('/api/get-users');
    if (!response.ok) {
      throw new Error('Failed to fetch users');
    }
    return response.json();
  };

  const { data: users } = useQuery('users', fetchUsers, {
    onError: (err) => setError(err.message),
    refetchOnWindowFocus: false,
  });
  
  const addSaleCodeMutation = useMutation(
    async (email) => {
      const response = await fetch('/api/add-sale-code', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email }),
      });
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to add sale code');
      }
      return response.json();
    },
    {
      onSuccess: (data, email) => {
        setMessage(`Sale code added for ${email}: ${data.saleCode}`);
        queryClient.invalidateQueries('users'); 
      },
      onError: (err) => {
        setMessage(err.message, 'error');
      },
    }
  );

  const removeSaleCodeMutation = useMutation(
    async ({ email, saleCode }) => {
      const response = await fetch('/api/remove-sale-code', {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, saleCode }),
      });
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to remove sale code');
      }
      return response.json();
    },
    {
      onSuccess: (data, { email, saleCode }) => {
        setMessage(`Sale code ${saleCode} removed for ${email}`);
        queryClient.invalidateQueries('users'); 
      },
      onError: (err) => setError(err.message),
    }
  );

  const removeUserMutation = useMutation(
    async (email) => {
      const response = await fetch('/api/delete-user', {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email }),
      });
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to remove user');
      }
      return response.json();
    },
    {
      onSuccess: (data, email) => {
        setMessage(`User ${email} removed`);
        queryClient.invalidateQueries('users'); 
      },
      onError: (err) => setError(err.message),
    }
  );

  const fetchSaleCodes = async (email) => {
    const response = await fetch(`/api/get-sale-codes?email=${email}`);
    if (!response.ok) {
      const errorData = await response.json();
      setError(errorData.message || 'Failed to fetch sale codes');
      return [];
    }
    const data = await response.json();
    return data.saleCodes || [];
  };

  return {
    users: users || [],
    message,
    error,
    addSaleCode: addSaleCodeMutation,
    removeSaleCode: removeSaleCodeMutation.mutate,
    removeUser: removeUserMutation.mutate,
    fetchSaleCodes,
    setError,
    setMessage,
  };
};

export default useUsers;
