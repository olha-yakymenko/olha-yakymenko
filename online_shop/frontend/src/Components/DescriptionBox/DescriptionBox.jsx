import React, { useState, useContext, useEffect } from 'react';
import './DescriptionBox.css';
import { UserContext } from '../../Context/UserContext';
import { ProductContext } from '../../Context/ProductContext';
import useMessageHandler from '../Admin/hooks/useMessageHandler';

const DescriptionBox = ({ product }) => {
  const { user } = useContext(UserContext);
  const [activeTab, setActiveTab] = useState('description');
  const [newComment, setNewComment] = useState('');
  const [newRating, setNewRating] = useState(1);  
  const [comments, setComments] = useState(product.comments || []);
  const { message, setMessage }= useMessageHandler();
  const { updateAverageRating } = useContext(ProductContext)
  const saveCommentsToLocalStorage = (comments) => {
    localStorage.setItem(
      `product_${product.id}_comments`,
      JSON.stringify(comments)
    );
  };

  useEffect(() => {
    const storedComments = localStorage.getItem(`product_${product.id}_comments`);
    if (storedComments) {
      setComments(JSON.parse(storedComments));
    }
  }, [product.id]);

  const handleAddComment = async () => {
    if (!user) {
      return;
    }

    if (newComment.trim() && newRating >= 1 && newRating <= 5) {
        try {
            const response = await fetch('/api/add-comment', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    product_id: product.id,
                    user_name: user.first_name || 'You',
                    comment: newComment.trim(),
                    rating: newRating,
                }),
            });
    
            if (response.ok) {
                const result = await response.json();
                const updatedComments = [
                    ...comments,
                    { user: user.first_name || 'You', comment: newComment.trim(), rating: newRating },
                ];
                setComments(updatedComments);
                saveCommentsToLocalStorage(updatedComments);
                setNewComment('');
                setNewRating(1);  
                setMessage( 'Comment added successfully', 'success');

                try {
                  const updateResponse = await fetch('/api/update-product', {
                      method: 'PUT',
                      headers: {
                          'Content-Type': 'application/json',
                      },
                      body: JSON.stringify({
                          id: product.id,
                          newLike: newRating,
                      }),
                  });
      
                  if (!updateResponse.ok) {
                      const errorData = await updateResponse.json();
                      setMessage(`Error updating product rating: ${errorData.message || 'Failed to update rating'}`, 'error' );
                  } else {
                      const updatedProduct = await updateResponse.json();
                      console.log('Product rating updated successfully:', updatedProduct);
                      updateAverageRating(updatedProduct.likes)
                  }
              } catch (error) {
                  console.error("Error updating product rating:", error);
                  setMessage( 'An error occurred while updating the product rating', 'error');
              }
            } else {
                const errorData = await response.json();
                setMessage( `Error: ${errorData.message || 'Failed to add comment'}`,  'error' );
            }
        } catch (error) {
            console.error("Error adding comment:", error);
            setMessage( 'An error occurred while adding the comment','error' );
        }
    }
    
  };

  return (
    <div className="descriptionbox">
      {message.text && (
        <div className={`message ${message.type}`}>
          {message.text}
        </div>
      )}
      <div className="descriptionbox-navigator">
        <div
          className={`descriptionbox-nav-box ${activeTab === 'description' ? 'active' : ''}`}
          onClick={() => setActiveTab('description')}
        >
          Description
        </div>
        <div
          className={`descriptionbox-nav-box ${activeTab === 'reviews' ? 'active' : ''}`}
          onClick={() => setActiveTab('reviews')}
        >
          Reviews ({comments.length})
        </div>
      </div>

      {activeTab === 'description' && (
        <div className="descriptionbox-description">
          <p>{product.description}</p>
        </div>
      )}

      {activeTab === 'reviews' && (
        <div className="descriptionbox-reviews">
          <div className="comments">
            {comments.map((comment, index) => (
              <div key={index} className="comment">
                <strong>{comment.user}:</strong> {comment.comment} <br />
                <em>Rating: {comment.rating} / 5</em>
              </div>
            ))}
          </div>

          {user ? (
            <div className="add-comment-form">
              <textarea
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)}
                placeholder="Add a comment..."
              />
              <select
                value={newRating}
                onChange={(e) => setNewRating(Number(e.target.value))}
              >
                <option value={1}>1</option>
                <option value={2}>2</option>
                <option value={3}>3</option>
                <option value={4}>4</option>
                <option value={5}>5</option>
              </select>
              <button
                onClick={handleAddComment}
                disabled={!newComment.trim()}
              >
                Add Comment
              </button>
            </div>
          ) : (
            <p>You must be logged in to add a comment.</p>
          )}
        </div>
      )}
    </div>
  );
};

export default DescriptionBox;
