import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { Layout } from '@/components/layout';
import { ReviewCard, UserCard } from '@/components/cards';
import { CommentForm } from '@/components/forms';
import { ConfirmationModal } from '@/components/ui';
import { useAppSelector } from '@/store/hooks';
import { selectIsAuthenticated, selectCurrentUser } from '@/store/slices';
import { reviewRepository, commentRepository, imageRepository } from '@/repositories';
import type { Review, Comment, User, CommentFormData, HALResource } from '@/types';

type TabType = 'comments' | 'likes';

const ReviewDetailPage = () => {
  const router = useRouter();
  const { id, tab: queryTab, pageNum: queryPage } = router.query;
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const [review, setReview] = useState<Review | null>(null);
  const [comments, setComments] = useState<Comment[]>([]);
  const [likedUsers, setLikedUsers] = useState<User[]>([]);
  const [activeTab, setActiveTab] = useState<TabType>('comments');
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(true);
  const [commentToDelete, setCommentToDelete] = useState<number | null>(null);
  const [submitLoading, setSubmitLoading] = useState(false);

  // Update state from query params
  useEffect(() => {
    if (queryTab) {
      setActiveTab(queryTab as TabType);
    }
    if (queryPage) {
      setPage(parseInt(queryPage as string));
    }
  }, [queryTab, queryPage]);

  // Fetch review data
  useEffect(() => {
    const fetchReview = async () => {
      if (!id) return;

      try {
        setLoading(true);
        const reviewId = parseInt(id as string);
        const reviewData = await reviewRepository.getReviewById(reviewId);
        setReview(reviewData.data as Review);
      } catch (error) {
        console.error('Failed to fetch review:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchReview();
  }, [id]);

  // Fetch tab content
  useEffect(() => {
    const fetchTabContent = async () => {
      if (!id || !review) return;

      try {
        const reviewId = parseInt(id as string);

        if (activeTab === 'comments') {
          const commentsData = await reviewRepository.getReviewComments(reviewId, page, 20);
          setComments(commentsData.items.map((item: HALResource<Comment>) => item.data as Comment));
          setHasMore(commentsData.items.length === 20);
        } else if (activeTab === 'likes') {
          const likesData = await reviewRepository.getReviewLikes(reviewId, page, 20);
          setLikedUsers(likesData.items.map((item: HALResource<User>) => item.data as User));
          setHasMore(likesData.items.length === 20);
        }
      } catch (error) {
        console.error('Failed to fetch tab content:', error);
      }
    };

    if (review) {
      fetchTabContent();
    }
  }, [id, review, activeTab, page]);

  const handleTabChange = useCallback((tab: TabType) => {
    setActiveTab(tab);
    setPage(1);
    router.push(`/reviews/${id}?tab=${tab}&pageNum=1`, undefined, { shallow: true });
  }, [id, router]);

  const handlePageChange = useCallback((newPage: number) => {
    setPage(newPage);
    router.push(`/reviews/${id}?tab=${activeTab}&pageNum=${newPage}`, undefined, { shallow: true });
  }, [id, activeTab, router]);

  const handleCommentSubmit = async (data: CommentFormData) => {
    if (!review) return;

    try {
      setSubmitLoading(true);
      await commentRepository.createComment(data);

      // Refresh comments
      const reviewId = parseInt(id as string);
      const commentsData = await reviewRepository.getReviewComments(reviewId, page, 20);
      setComments(commentsData.items.map((item: HALResource<Comment>) => item.data as Comment));

      // Update review comment count
      setReview({ ...review, comment_amount: (review.comment_amount || 0) + 1 });
    } catch (error) {
      console.error('Failed to create comment:', error);
    } finally {
      setSubmitLoading(false);
    }
  };

  const handleDeleteComment = async () => {
    if (!commentToDelete) return;

    try {
      await commentRepository.deleteComment(commentToDelete);

      // Refresh comments
      const reviewId = parseInt(id as string);
      const commentsData = await reviewRepository.getReviewComments(reviewId, page, 20);
      setComments(commentsData.items.map((item: HALResource<Comment>) => item.data as Comment));

      // Update review comment count
      if (review) {
        setReview({ ...review, comment_amount: Math.max(0, (review.comment_amount || 0) - 1) });
      }

      setCommentToDelete(null);
    } catch (error) {
      console.error('Failed to delete comment:', error);
    }
  };

  const canDeleteComment = useCallback((comment: Comment) => {
    if (!currentUser) return false;
    return currentUser.id === comment.user_id || currentUser.moderator;
  }, [currentUser]);

  if (loading || !review) {
    return (
      <Layout title="Loading...">
        <div className="content-wrapper">
          <div className="loading">Loading review...</div>
        </div>
      </Layout>
    );
  }

  return (
    <Layout title={`Musicboxd - ${review.title}`}>
      <div className="content-wrapper">
        {/* Review Card */}
        <div className="review-detail">
          <ReviewCard review={review} />
        </div>

        {/* Tabs Navigation */}
        <div className="section-header-home">
          <div className="tabs">
            <span
              className={`tab ${activeTab === 'comments' ? 'active' : ''}`}
              onClick={() => handleTabChange('comments')}
              style={{ cursor: 'pointer' }}
            >
              Comments
            </span>
            <span
              className={`tab ${activeTab === 'likes' ? 'active' : ''}`}
              onClick={() => handleTabChange('likes')}
              style={{ cursor: 'pointer' }}
            >
              Likes
            </span>
          </div>
        </div>

        {/* Tab Content */}
        {activeTab === 'likes' ? (
          <>
            {/* Likes Tab Content */}
            {likedUsers.length === 0 ? (
              <p className="no-results">No likes yet</p>
            ) : (
              <div className="users-grid">
                {likedUsers.map((user) => (
                  <UserCard key={user.id} user={user} />
                ))}
              </div>
            )}
          </>
        ) : (
          <>
            {/* Comments Tab Content */}
            <section className="comments-section">
              <h3>Comments</h3>

              {/* Comment Form */}
              {isAuthenticated ? (
                <div className="comment-form">
                  <CommentForm
                    onSubmit={handleCommentSubmit}
                    isLoading={submitLoading}
                    placeholder="Write a comment..."
                  />
                </div>
              ) : (
                <div className="auth-prompt">
                  <Link href="/login" className="btn btn-primary">
                    Login to Comment
                  </Link>
                </div>
              )}

              {/* Comments List */}
              {comments.length === 0 ? (
                <p className="no-results">No comments yet. Be the first to comment!</p>
              ) : (
                <div className="comments-list">
                  {comments.map((comment) => (
                    <div key={comment.id} className="comment-card">
                      <div className="comment-header">
                        <Link href={`/users/${comment.user_id}`} className="comment-user">
                          <img
                            src={comment.user_image_id ? imageRepository.getImageUrl(comment.user_image_id) : '/assets/default-user.png'}
                            alt={comment.username}
                            className="comment-user-img"
                          />
                          <div className="user-details">
                            <span className="comment-username">@{comment.username}</span>
                            {/* <div className="user-badges">
                              {comment.user_is_verified && (
                                <span className="badge badge-verified">Verified</span>
                              )}
                              {comment.user_is_moderator && (
                                <span className="badge badge-moderator">Moderator</span>
                              )}
                            </div> */}
                          </div>
                        </Link>

                        {canDeleteComment(comment) && (
                          <button
                            type="button"
                            onClick={() => setCommentToDelete(comment.id)}
                            className="btn btn-danger btn-sm"
                          >
                            Delete
                          </button>
                        )}
                      </div>
                      <span className="comment-date">{comment.created_at ? new Date(comment.created_at).toLocaleDateString() : ''}</span>
                      <p className="comment-content">{comment.content}</p>
                    </div>
                  ))}
                </div>
              )}
            </section>
          </>
        )}

        {/* Pagination */}
        <div className="pagination">
          {page > 1 && (
            <button
              onClick={() => handlePageChange(page - 1)}
              className="btn btn-secondary"
            >
              Previous Page
            </button>
          )}
          {hasMore && (
            <button
              onClick={() => handlePageChange(page + 1)}
              className="btn btn-secondary"
            >
              Next Page
            </button>
          )}
        </div>
      </div>

      {/* Confirmation Modal */}
      <ConfirmationModal
        isOpen={commentToDelete !== null}
        message="Are you sure you want to delete this comment?"
        onConfirm={handleDeleteComment}
        onCancel={() => setCommentToDelete(null)}
        confirmText="Yes"
        cancelText="No"
      />
    </Layout>
  );
};

export default ReviewDetailPage;

