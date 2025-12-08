import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
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
  selectReviewComments,
  selectReviewLikes,
  selectLoadingReview,
  selectLoadingComments,
  selectLoadingLikes,
  selectCommentsPagination,
  selectLikesPagination,
  clearCurrentReview,
  selectReviewById
} from '@/store/slices';
import type { Comment, CommentFormData } from '@/types';
import { ReviewTab } from '@/types/enums';

const ReviewDetailPage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const { reviewId, tab: queryTab, pageNum: queryPage } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const review = useAppSelector(selectReviewById(parseInt(reviewId as string)));
  const comments = useAppSelector(selectReviewComments);
  const likedUsers = useAppSelector(selectReviewLikes);
  const loadingReview = useAppSelector(selectLoadingReview);
  const loadingComments = useAppSelector(selectLoadingComments);
  const loadingLikes = useAppSelector(selectLoadingLikes);
  const commentsPagination = useAppSelector(selectCommentsPagination);
  const likesPagination = useAppSelector(selectLikesPagination);

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
    if (!review) dispatch(fetchReviewByIdAsync(reviewIdNum));
    dispatch(fetchReviewCommentsAsync({ reviewId: reviewIdNum, page, size: 20 })).unwrap()
    dispatch(fetchReviewLikesAsync({ reviewId: reviewIdNum, page, size: 20 })).unwrap()
    if (activeTab === ReviewTab.COMMENTS) {
      setHasMore(commentsPagination.page * commentsPagination.size < commentsPagination.totalCount);
    } else if (activeTab === ReviewTab.LIKES) {
      setHasMore(likesPagination.page * likesPagination.size < likesPagination.totalCount);
    }
  }, [reviewId, dispatch]);

  useEffect(() => {
    if (!reviewId || !review) return;
    const reviewIdNum = parseInt(reviewId as string);
    if (activeTab === ReviewTab.COMMENTS) {
      dispatch(fetchReviewCommentsAsync({ reviewId: reviewIdNum, page, size: 20 })).unwrap()
      setHasMore(commentsPagination.page * commentsPagination.size < commentsPagination.totalCount);
    } else if (activeTab === ReviewTab.LIKES) {
      dispatch(fetchReviewLikesAsync({ reviewId: reviewIdNum, page, size: 20 })).unwrap()
      setHasMore(likesPagination.page * likesPagination.size < likesPagination.totalCount);
    }
  }, [reviewId, page, dispatch]);

  useEffect(() => {
    if (!reviewId || !review) return;
    const reviewIdNum = parseInt(reviewId as string);
    if (activeTab === ReviewTab.LIKES) {
      dispatch(fetchReviewLikesAsync({ reviewId: reviewIdNum, page, size: 20 })).unwrap()
      setHasMore(likesPagination.page * likesPagination.size < likesPagination.totalCount);
    }
  }, [review?.likes]);


  const handleTabChange = useCallback((tab: ReviewTab) => {
    setActiveTab(tab);
    setPage(1);
  }, []);

  const handlePageChange = useCallback((newPage: number) => {
    setPage(newPage);
  }, []);

  const handleCommentSubmit = async (data: CommentFormData) => {
    if (!review) return;

    try {
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
      <Layout title={t('common.loading')}>
        <div className="content-wrapper">
          <div className="loading">{t('reviewDetail.loadingReview')}</div>
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
              {t('review.tabs.comments')}
            </span>
            <span
              className={`tab ${activeTab === ReviewTab.LIKES ? 'active' : ''}`}
              onClick={() => handleTabChange(ReviewTab.LIKES)}
              style={{ cursor: 'pointer' }}
            >
              {t('review.tabs.likes')}
            </span>
          </div>
        </div>

        {activeTab === ReviewTab.LIKES ? (
          <>
            {likedUsers.length === 0 ? (
              <p className="no-results">{t('reviewDetail.noLikes')}</p>
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
                <h3>{t('reviewDetail.addComment')}</h3>
                <CommentForm
                  onSubmit={handleCommentSubmit}
                  isLoading={submitLoading}
                  reviewId={review.id}
                />
              </div>
            ) : (
              <div className="auth-prompt">
                <p>{t('reviewDetail.loginToComment')}</p>
              </div>
            )}

            {loading ? (
              <div className="loading">{t('reviewDetail.loadingComments')}</div>
            ) : comments.length === 0 ? (
              <p className="no-results">{t('reviewDetail.noComments')}</p>
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
                          {t('common.delete')}
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
                {t('reviewDetail.previousPage')}
              </button>
            )}
            {hasMore && (
              <button
                onClick={() => handlePageChange(page + 1)}
                className="btn btn-secondary"
              >
                {t('reviewDetail.nextPage')}
              </button>
            )}
          </div>
        )}
      </div>

      <ConfirmationModal
        isOpen={commentToDelete !== null}
        message={t('reviewDetail.confirmDeleteComment')}
        onConfirm={handleDeleteComment}
        onCancel={() => setCommentToDelete(null)}
        confirmText={t('reviewDetail.yes')}
        cancelText={t('reviewDetail.no')}
      />
    </Layout>
  );
};

export default ReviewDetailPage;
