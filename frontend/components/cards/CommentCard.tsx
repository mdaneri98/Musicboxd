/**
 * CommentCard Component
 * Displays comment information with user details and actions
 */

import Link from 'next/link';
import { useState } from 'react';
import { Comment } from '@/types';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { selectCurrentUser, deleteCommentAsync } from '@/store/slices';
import { imageRepository } from '@/repositories';
import { formatTimeAgo } from '@/utils/timeUtils';
import { useTranslation } from 'react-i18next';
import { ASSETS } from '@/utils';

interface CommentCardProps {
  comment: Comment;
  onEdit?: (comment: Comment) => void;
  onDelete?: () => void;
}

const CommentCard = ({ comment, onEdit, onDelete }: CommentCardProps) => {
  const { t } = useTranslation();
  const dispatch = useAppDispatch();
  const currentUser = useAppSelector(selectCurrentUser);
  const [isDeleting, setIsDeleting] = useState(false);

  const isOwner = currentUser?.id === comment.user_id;
  const canDelete = isOwner || onDelete;

  const userImageUrl = comment.user_image_id
    ? imageRepository.getImageUrl(comment.user_image_id)
    : ASSETS.DEFAULT_AVATAR;

  const handleDelete = async () => {
    if (onDelete) {
      onDelete();
      return;
    }

    if (!window.confirm(t('common.confirmDeleteComment'))) {
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
      <div className="comment-layout">
        {/* User Info Section */}
        <Link href={`/users/${comment.user_id}`} className="comment-user-section">
          <img
            src={userImageUrl}
            alt={comment.username}
            className="comment-avatar"
          />
          <div className="comment-user-details">
            <div className="comment-user-name-row">
              <span className="comment-username">{comment.username}</span>
              <div className="comment-badges">
                {comment.user_verified && (
                  <span className="badge badge-verified">{t('label.verified')}</span>
                )}
                {comment.user_moderator && (
                  <span className="badge badge-moderator">{t('label.moderator')}</span>
                )}
              </div>
            </div>
            <span className="comment-timestamp">
              {formatTimeAgo(comment.created_at)}
            </span>
          </div>
        </Link>

        {/* Comment Content */}
        <div className="comment-body">
          <p className="comment-text">{comment.content}</p>
        </div>

        {/* Actions */}
        {canDelete && (
          <div className="comment-actions">
            {isOwner && onEdit && (
              <button
                onClick={handleEdit}
                className="action-btn"
                title={t('common.editComment')}
                aria-label={t('common.editComment')}
              >
                <i className="fas fa-edit"></i>
              </button>
            )}
            <button
              onClick={handleDelete}
              className="action-btn danger"
              title={t('common.deleteComment')}
              aria-label={t('common.deleteComment')}
              disabled={isDeleting}
            >
              <i className="fas fa-trash"></i>
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default CommentCard;
