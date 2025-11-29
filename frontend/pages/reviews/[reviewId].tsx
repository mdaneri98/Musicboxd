import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/router';
import { Layout } from '@/components/layout';
import { ReviewCard, UserCard } from '@/components/cards';
import { CommentForm } from '@/components/forms';
import { ConfirmationModal } from '@/components/ui';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { 
  selectIsAuthenticated, 
  selectCurrentUser,
  fetchReviewByIdAsync,
  fetchReviewCommentsAsync,
  fetchReviewLikesAsync,
  postCommentAsync,
  deleteCommentAsync,
  selectCurrentReview,
  selectReviewComments,
  selectReviewLikes,
  selectLoadingReview,
  selectLoadingComments,
  selectLoadingLikes,
  clearCurrentReview
} from '@/store/slices';
import type { Comment, CommentFormData } from '@/types';
import { ReviewTab } from '@/types/enums';

const ReviewDetailPage = () => {
  const router = useRouter();
  const { reviewId, tab: queryTab, pageNum: queryPage } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const review = useAppSelector(selectCurrentReview);
  const comments = useAppSelector(selectReviewComments);
  const likedUsers = useAppSelector(selectReviewLikes);
  const loadingReview = useAppSelector(selectLoadingReview);
  const loadingComments = useAppSelector(selectLoadingComments);
  const loadingLikes = useAppSelector(selectLoadingLikes);
  
  const [activeTab, setActiveTab] = useState<ReviewTab>(ReviewTab.COMMENTS);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [commentToDelete, setCommentToDelete] = useState<number | null>(null);
  const [submitLoading, setSubmitLoading] = useState(false);

  const loading = loadingReview || (activeTab === ReviewTab.COMMENTS ? loadingComments : loadingLikes);

  useEffect(() => {
    if (queryTab) {
      setActiveTab(queryTab as ReviewTab);
    }
    if (queryPage) {
      setPage(parseInt(queryPage as string));
    }
  }, [queryTab, queryPage]);

  useEffect(() => {
    return () => {
      dispatch(clearCurrentReview());
    };
  }, [dispatch]);

  useEffect(() => {
    if (!reviewId) return;
    
    const reviewIdNum = parseInt(reviewId as string);
    dispatch(fetchReviewByIdAsync(reviewIdNum));
  }, [reviewId, dispatch]);

  useEffect(() => {
    if (!reviewId || !review) return;

    const reviewIdNum = parseInt(reviewId as string);

    if (activeTab === ReviewTab.COMMENTS) {
      dispatch(fetchReviewCommentsAsync({ reviewId: reviewIdNum, page, size: 20 }))
        .unwrap()
        .then((commentsData) => {
          setHasMore(commentsData.items.length === 20);
        })
        .catch((err) => console.error('Failed to fetch comments:', err));
    } else if (activeTab === ReviewTab.LIKES) {
      dispatch(fetchReviewLikesAsync({ reviewId: reviewIdNum, page, size: 20 }))
        .unwrap()
        .then((likesData) => {
          setHasMore(likesData.items.length === 20);
        })
        .catch((err) => console.error('Failed to fetch likes:', err));
    }
  }, [reviewId, review, activeTab, page, dispatch]);

  const handleTabChange = useCallback((tab: ReviewTab) => {
    setActiveTab(tab);
    setPage(1);
    setHasMore(true);
  }, []);

  const handlePageChange = useCallback((newPage: number) => {
    setPage(newPage);
  }, [reviewId, dispatch]);

  const handleCommentSubmit = async (data: CommentFormData) => {
    if (!review) return;

    try {
      console.log('Creating comment:', data);
      setSubmitLoading(true);
      await dispatch(postCommentAsync(data)).unwrap();
    } catch (error) {
      console.error('Failed to create comment:', error);
    } finally {
      setSubmitLoading(false);
    }
  };

  const handleDeleteComment = async () => {
    if (!commentToDelete) return;

    try {
      await dispatch(deleteCommentAsync(commentToDelete)).unwrap();
      setCommentToDelete(null);
    } catch (error) {
      console.error('Failed to delete comment:', error);
    }
  };

  const canDeleteComment = useCallback((comment: Comment) => {
    if (!currentUser) return false;
    return currentUser.id === comment.user_id || currentUser.moderator;
  }, [currentUser]);

  if (loadingReview || !review) {
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
        <div className="review-detail">
          <ReviewCard review={review} />
        </div>

        <div className="section-header-home">
          <div className="tabs">
            <span
              className={`tab ${activeTab === ReviewTab.COMMENTS ? 'active' : ''}`}
              onClick={() => handleTabChange(ReviewTab.COMMENTS)}
              style={{ cursor: 'pointer' }}
            >
              Comments
            </span>
            <span
              className={`tab ${activeTab === ReviewTab.LIKES ? 'active' : ''}`}
              onClick={() => handleTabChange(ReviewTab.LIKES)}
              style={{ cursor: 'pointer' }}
            >
              Likes
            </span>
          </div>
        </div>

        {activeTab === ReviewTab.LIKES ? (
          <>
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
            {isAuthenticated ? (
              <div className="comment-form-section">
                <h3>Add a Comment</h3>
                <CommentForm
                  onSubmit={handleCommentSubmit}
                  isLoading={submitLoading}
                  reviewId={review.id}
                />
              </div>
            ) : (
              <div className="auth-prompt">
                <p>Please log in to comment</p>
              </div>
            )}

            {loading ? (
              <div className="loading">Loading comments...</div>
            ) : comments.length === 0 ? (
              <p className="no-results">No comments yet</p>
            ) : (
              <div className="comments-list">
                {comments.map((comment) => (
                  <div key={comment.id} className="comment-card">
                    <div className="comment-header">
                      <div className="comment-user">
                        <span className="comment-username">{comment.username}</span>
                        <span className="comment-date">
                          {comment.time_ago}
                        </span>
                      </div>
                      {canDeleteComment(comment) && (
                        <button
                          onClick={() => setCommentToDelete(comment.id)}
                          className="btn btn-danger btn-sm"
                        >
                          Delete
                        </button>
                      )}
                    </div>
                    <div className="comment-content">{comment.content}</div>
                  </div>
                ))}
              </div>
            )}
          </>
        )}

        {!loading && (
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
        )}
      </div>

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
