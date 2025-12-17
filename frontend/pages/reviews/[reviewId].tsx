import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/router';
import { useTranslation } from 'react-i18next';
import { Layout } from '@/components/layout';
import { ReviewCard, UserCard, CommentCard } from '@/components/cards';
import { CommentForm } from '@/components/forms';
import { ConfirmationModal, LoadingSpinner } from '@/components/ui';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { useInfiniteScroll } from '@/hooks';
import {
  selectIsAuthenticated,
  selectCurrentUser,
  fetchReviewByIdAsync,
  fetchReviewCommentsAsync,
  fetchMoreReviewCommentsAsync,
  fetchReviewLikesAsync,
  fetchMoreReviewLikesAsync,
  postCommentAsync,
  deleteCommentAsync,
  selectReviewComments,
  selectReviewLikes,
  selectLoadingReview,
  selectLoadingComments,
  selectLoadingMoreComments,
  selectLoadingLikes,
  selectLoadingMoreLikes,
  selectCommentsPagination,
  selectLikesPagination,
  selectCommentsHasMore,
  selectLikesHasMore,
  clearCurrentReview,
  selectReviewById,
  selectReviewError
} from '@/store/slices';
import type { Comment, CommentFormData } from '@/types';
import { ReviewTab } from '@/types/enums';
import Link from 'next/link';

const ReviewDetailPage = () => {
  const { t } = useTranslation();
  const router = useRouter();
  const { reviewId, tab: queryTab } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const review = useAppSelector(selectReviewById(parseInt(reviewId as string)));
  const comments = useAppSelector(selectReviewComments);
  const likedUsers = useAppSelector(selectReviewLikes);
  const loadingReview = useAppSelector(selectLoadingReview);
  const loadingComments = useAppSelector(selectLoadingComments);
  const loadingMoreComments = useAppSelector(selectLoadingMoreComments);
  const loadingLikes = useAppSelector(selectLoadingLikes);
  const loadingMoreLikes = useAppSelector(selectLoadingMoreLikes);
  const commentsPagination = useAppSelector(selectCommentsPagination);
  const likesPagination = useAppSelector(selectLikesPagination);
  const commentsHasMore = useAppSelector(selectCommentsHasMore);
  const likesHasMore = useAppSelector(selectLikesHasMore);
  const error = useAppSelector(selectReviewError);

  const [activeTab, setActiveTab] = useState<ReviewTab>(queryTab as ReviewTab || ReviewTab.COMMENTS);
  const [commentToDelete, setCommentToDelete] = useState<number | null>(null);
  const [submitLoading, setSubmitLoading] = useState(false);

  useEffect(() => {
    if (queryTab) {
      setActiveTab(queryTab as ReviewTab);
    }
  }, [queryTab]);

  useEffect(() => {
    return () => {
      dispatch(clearCurrentReview());
    };
  }, [dispatch]);

  useEffect(() => {
    if (!reviewId) return;
    const reviewIdNum = parseInt(reviewId as string);
    if (!review) dispatch(fetchReviewByIdAsync(reviewIdNum));
    dispatch(fetchReviewCommentsAsync({ reviewId: reviewIdNum, page: 1, size: 10 }));
    dispatch(fetchReviewLikesAsync({ reviewId: reviewIdNum, page: 1, size: 10 }));
  }, [reviewId, dispatch, review]);

  // Load more comments callback
  const handleLoadMoreComments = useCallback(async () => {
    if (!reviewId || !commentsHasMore || loadingMoreComments) return;

    const reviewIdNum = parseInt(reviewId as string);
    const nextPage = commentsPagination.page + 1;
    await dispatch(fetchMoreReviewCommentsAsync({
      reviewId: reviewIdNum,
      page: nextPage,
      size: commentsPagination.size
    }));
  }, [dispatch, reviewId, commentsPagination.page, commentsPagination.size, commentsHasMore, loadingMoreComments]);

  // Load more likes callback
  const handleLoadMoreLikes = useCallback(async () => {
    if (!reviewId || !likesHasMore || loadingMoreLikes) return;

    const reviewIdNum = parseInt(reviewId as string);
    const nextPage = likesPagination.page + 1;
    await dispatch(fetchMoreReviewLikesAsync({
      reviewId: reviewIdNum,
      page: nextPage,
      size: likesPagination.size
    }));
  }, [dispatch, reviewId, likesPagination.page, likesPagination.size, likesHasMore, loadingMoreLikes]);

  // Infinite scroll hook for comments
  const { sentinelRef: commentsSentinelRef, isFetchingMore: isFetchingMoreComments } = useInfiniteScroll({
    onLoadMore: handleLoadMoreComments,
    hasMore: commentsHasMore,
    isLoading: loadingComments || loadingMoreComments,
    enabled: activeTab === ReviewTab.COMMENTS && !loadingComments && comments.length > 0,
  });

  // Infinite scroll hook for likes
  const { sentinelRef: likesSentinelRef, isFetchingMore: isFetchingMoreLikes } = useInfiniteScroll({
    onLoadMore: handleLoadMoreLikes,
    hasMore: likesHasMore,
    isLoading: loadingLikes || loadingMoreLikes,
    enabled: activeTab === ReviewTab.LIKES && !loadingLikes && likedUsers.length > 0,
  });

  const handleTabChange = useCallback((tab: ReviewTab) => {
    setActiveTab(tab);
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

  if (error) {
    return (
      <Layout title={t('errors.review.title')}>
        <div className="content-wrapper">
          <div className="not-found-container">
            <h1>{t('errors.review.title')}</h1>
            <p>{t('errors.review.message')}</p>
            <button className="btn btn-primary" onClick={() => router.push('/')}>
              {t('errors.review.backToHome')}
            </button>
          </div>
        </div>
      </Layout>
    );
  }

  if (loadingReview || !review) {
    return (
      <Layout title={t('common.loading')}>
        <div className="content-wrapper">
          <LoadingSpinner size="large" />
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
            {loadingLikes && likedUsers.length === 0 ? (
              <LoadingSpinner size="medium" />
            ) : likedUsers.length === 0 ? (
              <p className="no-results">{t('reviewDetail.noLikes')}</p>
            ) : (
              <>
                <div className="users-grid">
                  {likedUsers.map((user) => (
                    <UserCard key={user.id} user={user} />
                  ))}
                </div>

                {/* Sentinel for infinite scroll */}
                <div ref={likesSentinelRef} className="infinite-scroll-sentinel" />

                {(loadingMoreLikes || isFetchingMoreLikes) && (
                  <div className="loading-more">
                    <LoadingSpinner size="small" />
                  </div>
                )}

                {!likesHasMore && likedUsers.length > 0 && (
                  <div className="end-of-content">
                    <p>{t('common.noMoreContent')}</p>
                  </div>
                )}
              </>
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
              <div className="form-actions">
                <Link href="/login">
                  <button className="btn btn-primary">
                    {t('reviewDetail.loginToComment')}
                  </button>
                </Link>
              </div>
            )}

            {loadingComments && comments.length === 0 ? (
              <LoadingSpinner size="medium" message={t('reviewDetail.loadingComments')} />
            ) : comments.length === 0 ? (
              <p className="no-results">{t('reviewDetail.noComments')}</p>
            ) : (
              <>
                <div className="comments-list">
                  {comments.map((comment) => (
                    <CommentCard
                      key={comment.id}
                      comment={comment}
                      onDelete={canDeleteComment(comment) ? () => setCommentToDelete(comment.id) : undefined}
                    />
                  ))}
                </div>

                {/* Sentinel for infinite scroll */}
                <div ref={commentsSentinelRef} className="infinite-scroll-sentinel" />

                {(loadingMoreComments || isFetchingMoreComments) && (
                  <div className="loading-more">
                    <LoadingSpinner size="small" />
                  </div>
                )}

                {!commentsHasMore && comments.length > 0 && (
                  <div className="end-of-content">
                    <p>{t('common.noMoreContent')}</p>
                  </div>
                )}
              </>
            )}
          </>
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

