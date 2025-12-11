/**
 * Review Slice
 * Redux slice for review state management
 */

import { createSlice, createAsyncThunk, PayloadAction, createSelector } from '@reduxjs/toolkit';
import { commentRepository, reviewRepository } from '@/repositories';
import { Review, User, Comment, Collection, HALResource, ReviewFormData, CommentFormData } from '@/types';
import type { RootState } from '../index';

// ============================================================================
// State Interface
// ============================================================================

export interface ReviewState {
  // Reviews by ID (normalized state)
  reviews: Record<number, Review>;
  // Ordered reviews id list
  orderedReviewsIds: number[];
  // Current review being viewed
  currentReview: Review | null;
  // Related data for current review
  reviewComments: Comment[];
  reviewLikes: User[];
  // Pagination info
  pagination: {
    page: number;
    size: number;
    totalCount: number;
    hasMore: boolean;
  };
  commentsPagination: {
    page: number;
    size: number;
    totalCount: number;
    hasMore: boolean;
  };
  likesPagination: {
    page: number;
    size: number;
    totalCount: number;
    hasMore: boolean;
  };
  // Loading states
  loading: boolean;
  loadingMore: boolean;
  loadingReview: boolean;
  loadingComments: boolean;
  loadingMoreComments: boolean;
  loadingLikes: boolean;
  loadingMoreLikes: boolean;
  // Error state
  error: string | null;
}

// ============================================================================
// Initial State
// ============================================================================

const initialState: ReviewState = {
  reviews: {},
  orderedReviewsIds: [],
  currentReview: null,
  reviewComments: [],
  reviewLikes: [],
  pagination: {
    page: 1,
    size: 20,
    totalCount: 0,
    hasMore: true,
  },
  commentsPagination: {
    page: 1,
    size: 20,
    totalCount: 0,
    hasMore: true,
  },
  likesPagination: {
    page: 1,
    size: 20,
    totalCount: 0,
    hasMore: true,
  },
  loading: false,
  loadingMore: false,
  loadingReview: false,
  loadingComments: false,
  loadingMoreComments: false,
  loadingLikes: false,
  loadingMoreLikes: false,
  error: null,
};

// ============================================================================
// Async Thunks
// ============================================================================

/**
 * Fetch paginated reviews list (replaces existing data - for initial load or filter change)
 */
export const fetchReviewsAsync = createAsyncThunk<
  Collection<HALResource<Review>>,
  { page?: number; size?: number; search?: string; filter?: string },
  { rejectValue: string }
>('reviews/fetchReviews', async ({ page = 1, size = 20, search, filter }, { rejectWithValue }) => {
  try {
    const response = await reviewRepository.getReviews(page, size, search, filter);
    return response as Collection<HALResource<Review>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch reviews');
  }
});

/**
 * Fetch more reviews (appends to existing data - for infinite scroll)
 */
export const fetchMoreReviewsAsync = createAsyncThunk<
  Collection<HALResource<Review>>,
  { page: number; size?: number; search?: string; filter?: string },
  { rejectValue: string }
>('reviews/fetchMoreReviews', async ({ page, size = 20, search, filter }, { rejectWithValue }) => {
  try {
    const response = await reviewRepository.getReviews(page, size, search, filter);
    return response as Collection<HALResource<Review>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch more reviews');
  }
});

/**
 * Fetch review by ID
 */
export const fetchReviewByIdAsync = createAsyncThunk<
  HALResource<Review>,
  number,
  { rejectValue: string }
>('reviews/fetchReviewById', async (reviewId, { rejectWithValue }) => {
  try {
    const review = await reviewRepository.getReviewById(reviewId);
    return review as HALResource<Review>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch review');
  }
});

/**
 * Create new review
 */
export const createReviewAsync = createAsyncThunk<
  HALResource<Review>,
  ReviewFormData,
  { rejectValue: string }
>('reviews/createReview', async (reviewData, { rejectWithValue }) => {
  try {
    const review = await reviewRepository.createReview(reviewData as ReviewFormData);
    return review as HALResource<Review>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to create review');
  }
});

/**
 * Update review
 */
export const updateReviewAsync = createAsyncThunk<
  HALResource<Review>,
  { id: number; reviewData: ReviewFormData },
  { rejectValue: string }
>('reviews/updateReview', async ({ id, reviewData }, { rejectWithValue }) => {
  try {
    const review = await reviewRepository.updateReview(id, reviewData);
    return review as HALResource<Review>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to update review');
  }
});

/**
 * Delete review
 */
export const deleteReviewAsync = createAsyncThunk<
  number,
  number,
  { rejectValue: string }
>('reviews/deleteReview', async (reviewId, { rejectWithValue }) => {
  try {
    await reviewRepository.deleteReview(reviewId);
    return reviewId;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to delete review');
  }
});

/**
 * Fetch users who liked a review (replaces existing - for initial load)
 */
export const fetchReviewLikesAsync = createAsyncThunk<
  Collection<HALResource<User>>,
  { reviewId: number; page?: number; size?: number },
  { rejectValue: string }
>('reviews/fetchReviewLikes', async ({ reviewId, page = 1, size = 20 }, { rejectWithValue }) => {
  try {
    const response = await reviewRepository.getReviewLikes(reviewId, page, size);
    return response as Collection<HALResource<User>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch review likes');
  }
});

/**
 * Fetch more users who liked a review (appends - for infinite scroll)
 */
export const fetchMoreReviewLikesAsync = createAsyncThunk<
  Collection<HALResource<User>>,
  { reviewId: number; page: number; size?: number },
  { rejectValue: string }
>('reviews/fetchMoreReviewLikes', async ({ reviewId, page, size = 20 }, { rejectWithValue }) => {
  try {
    const response = await reviewRepository.getReviewLikes(reviewId, page, size);
    return response as Collection<HALResource<User>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch more likes');
  }
});

/**
 * Like a review
 */
export const likeReviewAsync = createAsyncThunk<
  number,
  number,
  { rejectValue: string }
>('reviews/likeReview', async (reviewId, { rejectWithValue }) => {
  try {
    await reviewRepository.likeReview(reviewId);
    return reviewId;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to like review');
  }
});

/**
 * Unlike a review
 */
export const unlikeReviewAsync = createAsyncThunk<
  number,
  number,
  { rejectValue: string }
>('reviews/unlikeReview', async (reviewId, { rejectWithValue }) => {
  try {
    await reviewRepository.unlikeReview(reviewId);
    return reviewId;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to unlike review');
  }
});

/**
 * Fetch comments for a review (replaces existing - for initial load)
 */
export const fetchReviewCommentsAsync = createAsyncThunk<
  Collection<HALResource<Comment>>,
  { reviewId: number; page?: number; size?: number },
  { rejectValue: string }
>('reviews/fetchReviewComments', async ({ reviewId, page = 1, size = 20 }, { rejectWithValue }) => {
  try {
    const response = await reviewRepository.getReviewComments(reviewId, page, size);
    return response as Collection<HALResource<Comment>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch review comments');
  }
});

/**
 * Fetch more comments for a review (appends - for infinite scroll)
 */
export const fetchMoreReviewCommentsAsync = createAsyncThunk<
  Collection<HALResource<Comment>>,
  { reviewId: number; page: number; size?: number },
  { rejectValue: string }
>('reviews/fetchMoreReviewComments', async ({ reviewId, page, size = 20 }, { rejectWithValue }) => {
  try {
    const response = await reviewRepository.getReviewComments(reviewId, page, size);
    return response as Collection<HALResource<Comment>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch more comments');
  }
});

/**
 * Post a comment for a review
 */
export const postCommentAsync = createAsyncThunk<
  HALResource<Comment>,
  CommentFormData,
  { rejectValue: string }
>('reviews/postComment', async (commentData, { rejectWithValue }) => {
  try {
    const response = await commentRepository.createComment(commentData);
    return response as HALResource<Comment>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to post comment');
  }
});

/**
 * Delete a comment
 */
export const deleteCommentAsync = createAsyncThunk<
  number,
  number,
  { rejectValue: string }
>('reviews/deleteComment', async (commentId, { rejectWithValue }) => {
  try {
    await commentRepository.deleteComment(commentId);
    return commentId;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to delete comment');
  }
});

/**
 * Block a review (moderator only)
 */
export const blockReviewAsync = createAsyncThunk<
  HALResource<Review>,
  number,
  { rejectValue: string }
>('reviews/blockReview', async (reviewId, { rejectWithValue }) => {
  try {
    const review = await reviewRepository.updateReview(reviewId, { blocked: true });
    return review as HALResource<Review>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to block review');
  }
});

/**
 * Unblock a review (moderator only)
 */
export const unblockReviewAsync = createAsyncThunk<
  HALResource<Review>,
  number,
  { rejectValue: string }
>('reviews/unblockReview', async (reviewId, { rejectWithValue }) => {
  try {
    const review = await reviewRepository.updateReview(reviewId, { blocked: false });
    return review as HALResource<Review>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to unblock review');
  }
});

// ============================================================================
// Slice
// ============================================================================

const reviewSlice = createSlice({
  name: 'reviews',
  initialState,
  reducers: {
    /**
     * Clear error state
     */
    clearError: (state) => {
      state.error = null;
    },

    /**
     * Clear current review
     */
    clearCurrentReview: (state) => {
      state.currentReview = null;
      state.reviewComments = [];
      state.reviewLikes = [];
      state.commentsPagination = { page: 1, size: 20, totalCount: 0, hasMore: true };
      state.likesPagination = { page: 1, size: 20, totalCount: 0, hasMore: true };
    },

    /**
     * Clear reviews list (for filter/tab change)
     */
    clearReviews: (state) => {
      state.reviews = {};
      state.orderedReviewsIds = [];
      state.pagination = { page: 1, size: 20, totalCount: 0, hasMore: true };
    },

    /**
     * Add review to normalized state
     */
    addReview: (state, action: PayloadAction<Review>) => {
      state.reviews[action.payload.id] = action.payload;
    },

    /**
     * Remove review from normalized state
     */
    removeReview: (state, action: PayloadAction<number>) => {
      delete state.reviews[action.payload];
    },
  },
  extraReducers: (builder) => {
    // Fetch Reviews (initial load - replaces data)
    builder
      .addCase(fetchReviewsAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchReviewsAsync.fulfilled, (state, action) => {
        state.loading = false;
        // Clear and replace data
        state.reviews = {};
        state.orderedReviewsIds = [];
        action.payload.items.forEach((review) => { 
          state.reviews[review.data.id] = review.data as Review;
          state.orderedReviewsIds.push(review.data.id);
        });
        const hasMore = action.payload.currentPage * action.payload.pageSize < action.payload.totalCount;
        state.pagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
          hasMore,
        };
      })
      .addCase(fetchReviewsAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to fetch reviews';
      });

    // Fetch More Reviews (infinite scroll - appends data)
    builder
      .addCase(fetchMoreReviewsAsync.pending, (state) => {
        state.loadingMore = true;
        state.error = null;
      })
      .addCase(fetchMoreReviewsAsync.fulfilled, (state, action) => {
        state.loadingMore = false;
        // Append data without duplicates
        action.payload.items.forEach((review) => {
          if (!state.reviews[review.data.id]) {
            state.reviews[review.data.id] = review.data as Review;
            state.orderedReviewsIds.push(review.data.id);
          }
        });
        const hasMore = action.payload.currentPage * action.payload.pageSize < action.payload.totalCount;
        state.pagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
          hasMore,
        };
      })
      .addCase(fetchMoreReviewsAsync.rejected, (state, action) => {
        state.loadingMore = false;
        state.error = action.payload || 'Failed to fetch more reviews';
      });

    // Fetch Review By ID
    builder
      .addCase(fetchReviewByIdAsync.pending, (state) => {
        state.loadingReview = true;
        state.error = null;
      })
      .addCase(fetchReviewByIdAsync.fulfilled, (state, action) => {
        state.loadingReview = false;
        state.currentReview = action.payload.data as Review;
        state.reviews[action.payload.data.id] = action.payload.data as Review;
      })
      .addCase(fetchReviewByIdAsync.rejected, (state, action) => {
        state.loadingReview = false;
        state.error = action.payload || 'Failed to fetch review';
      });

    // Create Review
    builder
      .addCase(createReviewAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createReviewAsync.fulfilled, (state, action) => {
        state.loading = false;
        state.reviews[action.payload.data.id] = action.payload.data as Review;
      })
      .addCase(createReviewAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to create review';
      });

    // Update Review
    builder
      .addCase(updateReviewAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateReviewAsync.fulfilled, (state, action) => {
        state.loading = false;
        state.reviews[action.payload.data.id] = action.payload.data as Review;
        if (state.currentReview?.id === action.payload.data.id) {
          state.currentReview = action.payload.data as Review;
        }
      })
      .addCase(updateReviewAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to update review';
      });

    // Delete Review
    builder
      .addCase(deleteReviewAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteReviewAsync.fulfilled, (state, action) => {
        state.loading = false;
        delete state.reviews[action.meta.arg];
        if (state.currentReview?.id === action.meta.arg) {
          state.currentReview = null;
        }
      })
      .addCase(deleteReviewAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to delete review';
      });

    // Fetch Review Likes (initial load - replaces data)
    builder
      .addCase(fetchReviewLikesAsync.pending, (state) => {
        state.loadingLikes = true;
        state.error = null;
      })
      .addCase(fetchReviewLikesAsync.fulfilled, (state, action) => {
        state.loadingLikes = false;
        state.reviewLikes = action.payload.items.map((like) => like.data as User);
        const loadedCount = action.payload.currentPage * action.payload.pageSize;
        const hasMore = loadedCount < action.payload.totalCount && action.payload.items.length === action.payload.pageSize;
        state.likesPagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
          hasMore,
        };
      })
      .addCase(fetchReviewLikesAsync.rejected, (state, action) => {
        state.loadingLikes = false;
        state.error = action.payload || 'Failed to fetch review likes';
      });

    // Fetch More Review Likes (infinite scroll - appends data)
    builder
      .addCase(fetchMoreReviewLikesAsync.pending, (state) => {
        state.loadingMoreLikes = true;
        state.error = null;
      })
      .addCase(fetchMoreReviewLikesAsync.fulfilled, (state, action) => {
        state.loadingMoreLikes = false;
        // Append without duplicates
        const existingIds = new Set(state.reviewLikes.map(u => u.id));
        const newLikes = action.payload.items
          .map((like) => like.data as User)
          .filter(u => !existingIds.has(u.id));
        state.reviewLikes = [...state.reviewLikes, ...newLikes];
        const loadedCount = action.payload.currentPage * action.payload.pageSize;
        const hasMore = loadedCount < action.payload.totalCount && action.payload.items.length === action.payload.pageSize;
        state.likesPagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
          hasMore,
        };
      })
      .addCase(fetchMoreReviewLikesAsync.rejected, (state, action) => {
        state.loadingMoreLikes = false;
        state.error = action.payload || 'Failed to fetch more likes';
      });

    // Like Review
    builder
      .addCase(likeReviewAsync.fulfilled, (state, action) => {
        // Update like count in current review
        if (state.currentReview?.id === action.meta.arg) {
          state.currentReview.likes += 1;
          state.currentReview.liked = true;
        }
        // Update in normalized state
        if (state.reviews[action.meta.arg]) {
          state.reviews[action.meta.arg].likes += 1;
          state.reviews[action.meta.arg].liked = true;
        }
      })
      .addCase(likeReviewAsync.rejected, (state, action) => {
        state.error = action.payload || 'Failed to like review';
      });

    // Unlike Review
    builder
      .addCase(unlikeReviewAsync.fulfilled, (state, action) => {
        // Update like count in current review
        if (state.currentReview?.id === action.meta.arg) {
          state.currentReview.likes -= 1;
          state.currentReview.liked = false;
        }
        // Update in normalized state
        if (state.reviews[action.meta.arg]) {
          state.reviews[action.meta.arg].likes -= 1;
          state.reviews[action.meta.arg].liked = false;
        }
      })
      .addCase(unlikeReviewAsync.rejected, (state, action) => {
        state.error = action.payload || 'Failed to unlike review';
      });

    // Fetch Review Comments (initial load - replaces data)
    builder
      .addCase(fetchReviewCommentsAsync.pending, (state) => {
        state.loadingComments = true;
        state.error = null;
      })
      .addCase(fetchReviewCommentsAsync.fulfilled, (state, action) => {
        state.loadingComments = false;
        state.reviewComments = action.payload.items.map((comment) => comment.data as Comment);
        // hasMore is false if we got fewer items than page size OR if we've loaded all items
        const loadedCount = action.payload.currentPage * action.payload.pageSize;
        const hasMore = loadedCount < action.payload.totalCount && action.payload.items.length === action.payload.pageSize;
        state.commentsPagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
          hasMore,
        };
      })
      .addCase(fetchReviewCommentsAsync.rejected, (state, action) => {
        state.loadingComments = false;
        state.error = action.payload || 'Failed to fetch review comments';
      });

    // Fetch More Review Comments (infinite scroll - appends data)
    builder
      .addCase(fetchMoreReviewCommentsAsync.pending, (state) => {
        state.loadingMoreComments = true;
        state.error = null;
      })
      .addCase(fetchMoreReviewCommentsAsync.fulfilled, (state, action) => {
        state.loadingMoreComments = false;
        // Append without duplicates
        const existingIds = new Set(state.reviewComments.map(c => c.id));
        const newComments = action.payload.items
          .map((comment) => comment.data as Comment)
          .filter(c => !existingIds.has(c.id));
        state.reviewComments = [...state.reviewComments, ...newComments];
        // hasMore is false if we got fewer items than page size OR if we've loaded all items
        const loadedCount = action.payload.currentPage * action.payload.pageSize;
        const hasMore = loadedCount < action.payload.totalCount && action.payload.items.length === action.payload.pageSize;
        state.commentsPagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
          hasMore,
        };
      })
      .addCase(fetchMoreReviewCommentsAsync.rejected, (state, action) => {
        state.loadingMoreComments = false;
        state.error = action.payload || 'Failed to fetch more comments';
      });

    // Post Comment
    builder
      .addCase(postCommentAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(postCommentAsync.fulfilled, (state, action) => {
        state.loading = false;
        state.reviewComments.unshift(action.payload.data as Comment);
        const reviewId = action.payload.data.review_id;
        if (state.reviews[reviewId]) {
          state.reviews[reviewId].comment_amount += 1;
        }
        if (state.currentReview?.id === reviewId) {
          state.currentReview.comment_amount += 1;
        }
      })
      .addCase(postCommentAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to post comment';
      });

    // Delete Comment
    builder
      .addCase(deleteCommentAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteCommentAsync.fulfilled, (state, action) => {
        state.loading = false;
        const deletedComment = state.reviewComments.find((comment) => comment.id === action.meta.arg);
        state.reviewComments = state.reviewComments.filter((comment) => comment.id !== action.meta.arg);
        if (deletedComment) {
          const reviewId = deletedComment.review_id;
          if (state.reviews[reviewId]) {
            state.reviews[reviewId].comment_amount -= 1;
          }
          if (state.currentReview?.id === reviewId) {
            state.currentReview.comment_amount -= 1;
          }
        }
      })
      .addCase(deleteCommentAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to delete comment';
      });

    // Block Review
    builder
      .addCase(blockReviewAsync.fulfilled, (state, action) => {
        const reviewData = action.payload.data as Review;
        state.reviews[reviewData.id] = reviewData;
        if (state.currentReview?.id === reviewData.id) {
          state.currentReview = reviewData;
        }
      })
      .addCase(blockReviewAsync.rejected, (state, action) => {
        state.error = action.payload || 'Failed to block review';
      });

    // Unblock Review
    builder
      .addCase(unblockReviewAsync.fulfilled, (state, action) => {
        const reviewData = action.payload.data as Review;
        state.reviews[reviewData.id] = reviewData;
        if (state.currentReview?.id === reviewData.id) {
          state.currentReview = reviewData;
        }
      })
      .addCase(unblockReviewAsync.rejected, (state, action) => {
        state.error = action.payload || 'Failed to unblock review';
      });
  },
});

// ============================================================================
// Actions & Selectors
// ============================================================================

export const { clearError, clearCurrentReview, clearReviews, addReview, removeReview } = reviewSlice.actions;

export const selectReviews = (state: RootState) => state.reviews.reviews;
export const selectReviewIds = (state: RootState) => state.reviews.orderedReviewsIds;
export const selectOrderedReviews = createSelector(
  [selectReviews, selectReviewIds],
  (reviews, ids) => ids.map((id) => reviews[id]).filter(Boolean)
);
export const selectReviewById = (reviewId: number) => (state: RootState) => {
  return state.reviews.reviews[reviewId] || null;
}
export const selectCurrentReview = (state: RootState) => state.reviews.currentReview;
export const selectReviewComments = (state: RootState) => state.reviews.reviewComments;
export const selectReviewLikes = (state: RootState) => state.reviews.reviewLikes;
export const selectReviewPagination = (state: RootState) => state.reviews.pagination;
export const selectReviewLoading = (state: RootState) => state.reviews.loading;
export const selectReviewLoadingMore = (state: RootState) => state.reviews.loadingMore;
export const selectReviewError = (state: RootState) => state.reviews.error;
export const selectLoadingReview = (state: RootState) => state.reviews.loadingReview;
export const selectLoadingComments = (state: RootState) => state.reviews.loadingComments;
export const selectLoadingMoreComments = (state: RootState) => state.reviews.loadingMoreComments;
export const selectLoadingLikes = (state: RootState) => state.reviews.loadingLikes;
export const selectLoadingMoreLikes = (state: RootState) => state.reviews.loadingMoreLikes;
export const selectCommentsPagination = (state: RootState) => state.reviews.commentsPagination;
export const selectLikesPagination = (state: RootState) => state.reviews.likesPagination;
export const selectReviewsHasMore = (state: RootState) => state.reviews.pagination.hasMore;
export const selectCommentsHasMore = (state: RootState) => state.reviews.commentsPagination.hasMore;
export const selectLikesHasMore = (state: RootState) => state.reviews.likesPagination.hasMore;

export default reviewSlice.reducer;

