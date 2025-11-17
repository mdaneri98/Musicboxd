/**
 * CommentCard Component
 * Displays comment information with edit/delete actions
 */

import Link from 'next/link';
import { useState } from 'react';
import { Comment } from '@/types';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectCurrentUser, deleteCommentAsync } from '@/store/slices';
import { imageRepository } from '@/repositories';

interface CommentCardProps {
  comment: Comment;
  onEdit?: (comment: Comment) => void;
}

const CommentCard = ({ comment, onEdit }: CommentCardProps) => {
  const dispatch = useAppDispatch();
  const currentUser = useAppSelector(selectCurrentUser);
  const [isDeleting, setIsDeleting] = useState(false);

  const isOwner = currentUser?.id === comment.user_id;

  const userImageUrl = comment.user_image_id
    ? imageRepository.getImageUrl(comment.user_image_id)
    : '/assets/default-avatar.png';

  const handleDelete = async () => {
    if (!window.confirm('Are you sure you want to delete this comment?')) {
      return;
    }
    
    setIsDeleting(true);
    try {
      await dispatch(deleteCommentAsync(comment.id));
    } catch (error) {
      console.error('Failed to delete comment:', error);
    } finally {
      setIsDeleting(false);
    }
  };

  const handleEdit = () => {
    if (onEdit) {
      onEdit(comment);
    }
  };

  return (
    <div className="comment-card">
      <div className="comment-header">
        <Link href={`/users/${comment.user_id}`} className="comment-user">
          <img
            src={userImageUrl}
            alt={comment.username}
            className="img-avatar"
          />
          <div className="comment-user-info">
            <span className="comment-username">{comment.username}</span>
            <span className="comment-timestamp">
              {comment.time_ago}
            </span>
          </div>
        </Link>
        {isOwner && (
          <div className="comment-actions">
            <button
              onClick={handleEdit}
              className="btn-icon"
              title="Edit comment"
            >
              <i className="fas fa-edit"></i>
            </button>
            <button
              onClick={handleDelete}
              className="btn-icon danger"
              title="Delete comment"
              disabled={isDeleting}
            >
              <i className="fas fa-trash"></i>
            </button>
          </div>
        )}
      </div>
      <div className="comment-content">
        <p>{comment.content}</p>
      </div>
    </div>
  );
};

export default CommentCard;

