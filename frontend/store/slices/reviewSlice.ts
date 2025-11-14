/**
 * Review Slice
 * Redux slice for review state management
 */

import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { reviewRepository } from '@/repositories';
import { Review, User, Comment, Collection, HALResource, ReviewFormData } from '@/types';
import type { RootState } from '../index';

// ============================================================================
// State Interface
// ============================================================================

export interface ReviewState {
  // Reviews by ID (normalized state)
  reviews: Record<number, Review>;
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
  };
  // Loading states
  loading: boolean;
  loadingReview: boolean;
  loadingComments: boolean;
  loadingLikes: boolean;
  // Error state
  error: string | null;
}

// ============================================================================
// Initial State
// ============================================================================

const initialState: ReviewState = {
  reviews: {},
  currentReview: null,
  reviewComments: [],
  reviewLikes: [],
  pagination: {
    page: 1,
    size: 20,
    totalCount: 0,
  },
  loading: false,
  loadingReview: false,
  loadingComments: false,
  loadingLikes: false,
  error: null,
};

// ============================================================================
// Async Thunks
// ============================================================================

/**
 * Fetch paginated reviews list
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
    HALResource<Review> ,
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
 * Fetch users who liked a review
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
 * Fetch comments for a review
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
 * Block a review (moderator only)
 */
export const blockReviewAsync = createAsyncThunk<
  number,
  number,
  { rejectValue: string }
>('reviews/blockReview', async (reviewId, { rejectWithValue }) => {
  try {
    await reviewRepository.blockReview(reviewId);
    return reviewId;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to block review');
  }
});

/**
 * Unblock a review (moderator only)
 */
export const unblockReviewAsync = createAsyncThunk<
  number,
  number,
  { rejectValue: string }
>('reviews/unblockReview', async (reviewId, { rejectWithValue }) => {
  try {
    await reviewRepository.unblockReview(reviewId);
    return reviewId;
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
    // Fetch Reviews
    builder
      .addCase(fetchReviewsAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchReviewsAsync.fulfilled, (state, action) => {
        state.loading = false;
        action.payload.items.forEach((review) => {
          state.reviews[review.data.id] = review.data as Review;
        });
        state.pagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
        };
      })
      .addCase(fetchReviewsAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to fetch reviews';
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

    // Fetch Review Likes
    builder
      .addCase(fetchReviewLikesAsync.pending, (state) => {
        state.loadingLikes = true;
        state.error = null;
      })
      .addCase(fetchReviewLikesAsync.fulfilled, (state, action) => {
        state.loadingLikes = false;
        action.payload.items.forEach((like) => {
          state.reviewLikes.push(like.data as User);
        });
      })
      .addCase(fetchReviewLikesAsync.rejected, (state, action) => {
        state.loadingLikes = false;
        state.error = action.payload || 'Failed to fetch review likes';
      });

    // Like Review
    builder
      .addCase(likeReviewAsync.fulfilled, (state, action) => {
        // Update like count in current review
        if (state.currentReview?.id === action.meta.arg) {
          state.currentReview.likes += 1;
        }
        // Update in normalized state
        if (state.reviews[action.meta.arg]) {
          state.reviews[action.meta.arg].likes += 1;
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
        }
        // Update in normalized state
        if (state.reviews[action.meta.arg]) {
          state.reviews[action.meta.arg].likes -= 1;
        }
      })
      .addCase(unlikeReviewAsync.rejected, (state, action) => {
        state.error = action.payload || 'Failed to unlike review';
      });

    // Fetch Review Comments
    builder
      .addCase(fetchReviewCommentsAsync.pending, (state) => {
        state.loadingComments = true;
        state.error = null;
      })
      .addCase(fetchReviewCommentsAsync.fulfilled, (state, action) => {
        state.loadingComments = false;
        action.payload.items.forEach((comment) => {
          state.reviewComments.push(comment.data as Comment);
        });
      })
      .addCase(fetchReviewCommentsAsync.rejected, (state, action) => {
        state.loadingComments = false;
        state.error = action.payload || 'Failed to fetch review comments';
      });

    // Block Review
    builder
      .addCase(blockReviewAsync.fulfilled, (state, action) => {
        // Update blocked status
        if (state.currentReview?.id === action.meta.arg) {
          state.currentReview.is_blocked = true;
        }
        if (state.reviews[action.meta.arg]) {
          state.reviews[action.meta.arg].is_blocked = true;
        }
      })
      .addCase(blockReviewAsync.rejected, (state, action) => {
        state.error = action.payload || 'Failed to block review';
      });

    // Unblock Review
    builder
      .addCase(unblockReviewAsync.fulfilled, (state, action) => {
        // Update blocked status
        if (state.currentReview?.id === action.payload) {
          state.currentReview.is_blocked = false;
        }
        if (state.reviews[action.meta.arg]) {
          state.reviews[action.meta.arg].is_blocked = false;
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

export const { clearError, clearCurrentReview, addReview, removeReview } = reviewSlice.actions;

export const selectReviews = (state: RootState) => state.reviews.reviews;
export const selectReviewById = (reviewId: number) => (state: RootState) =>
  state.reviews.reviews[reviewId] || null;
export const selectCurrentReview = (state: RootState) => state.reviews.currentReview;
export const selectReviewComments = (state: RootState) => state.reviews.reviewComments;
export const selectReviewLikes = (state: RootState) => state.reviews.reviewLikes;
export const selectReviewPagination = (state: RootState) => state.reviews.pagination;
export const selectReviewLoading = (state: RootState) => state.reviews.loading;
export const selectReviewError = (state: RootState) => state.reviews.error;
export const selectLoadingReview = (state: RootState) => state.reviews.loadingReview;
export const selectLoadingComments = (state: RootState) => state.reviews.loadingComments;
export const selectLoadingLikes = (state: RootState) => state.reviews.loadingLikes;

export default reviewSlice.reducer;

