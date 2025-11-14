/**
 * Comment Slice
 * Redux slice for comment state management
 */

import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { commentRepository } from '@/repositories';
import { Comment, Collection, HALResource, CommentFormData } from '@/types';
import type { RootState } from '../index';

// ============================================================================
// State Interface
// ============================================================================

export interface CommentState {
  // Comments by ID (normalized state)
  comments: Record<number, Comment>;
  // Current comment being viewed/edited
  currentComment: Comment | null;
  // Pagination info
  pagination: {
    page: number;
    size: number;
    totalCount: number;
  };
  // Loading states
  loading: boolean;
  loadingComment: boolean;
  // Error state
  error: string | null;
}

// ============================================================================
// Initial State
// ============================================================================

const initialState: CommentState = {
  comments: {},
  currentComment: null,
  pagination: {
    page: 1,
    size: 20,
    totalCount: 0,
  },
  loading: false,
  loadingComment: false,
  error: null,
};

// ============================================================================
// Async Thunks
// ============================================================================

/**
 * Fetch paginated comments list
 */
export const fetchCommentsAsync = createAsyncThunk<
  Collection<HALResource<Comment>>,
  { page?: number; size?: number; search?: string; filter?: string },
  { rejectValue: string }
>('comments/fetchCommentsAsync', async ({ page = 1, size = 20, search, filter }, { rejectWithValue }) => {
  try {
    const response = await commentRepository.getComments(page, size, search, filter);
    return response as Collection<HALResource<Comment>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch comments');
  }
});

/**
 * Fetch comment by ID
 */
export const fetchCommentByIdAsync = createAsyncThunk<
  HALResource<Comment>,
  number,
  { rejectValue: string }
>('comments/fetchCommentByIdAsync', async (commentId, { rejectWithValue }) => {
  try {
    const comment = await commentRepository.getCommentById(commentId);
    return comment as HALResource<Comment>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch comment');
  }
});

/**
 * Create new comment
 */
export const createCommentAsync = createAsyncThunk<
  HALResource<Comment>,
  CommentFormData,
  { rejectValue: string }
>('comments/createCommentAsync', async (commentData, { rejectWithValue }) => {
  try {
    const comment = await commentRepository.createComment(commentData);
    return comment as HALResource<Comment>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to create comment');
  }
});

/**
 * Update comment
 */
export const updateCommentAsync = createAsyncThunk<
  HALResource<Comment>,
  { id: number; commentData: CommentFormData },
  { rejectValue: string }
>('comments/updateCommentAsync', async ({ id, commentData }, { rejectWithValue }) => {
  try {
    const comment = await commentRepository.updateComment(id, commentData);
    return comment as HALResource<Comment>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to update comment');
  }
});

/**
 * Delete comment
 */
export const deleteCommentAsync = createAsyncThunk<
  void,
  number,
  { rejectValue: string }
>('comments/deleteCommentAsync', async (commentId, { rejectWithValue }) => {
  try {
    await commentRepository.deleteComment(commentId);
    return;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to delete comment');
  }
});

// ============================================================================
// Slice
// ============================================================================

const commentSlice = createSlice({
  name: 'comments',
  initialState,
  reducers: {
    /**
     * Clear error state
     */
    clearError: (state) => {
      state.error = null;
    },

    /**
     * Clear current comment
     */
    clearCurrentComment: (state) => {
      state.currentComment = null;
    },

    /**
     * Add comment to normalized state
     */
    addComment: (state, action: PayloadAction<Comment>) => {
      state.comments[action.payload.id] = action.payload;
    },

    /**
     * Remove comment from normalized state
     */
    removeComment: (state, action: PayloadAction<number>) => {
      delete state.comments[action.payload];
    },
  },
  extraReducers: (builder) => {
    // Fetch Comments
    builder
      .addCase(fetchCommentsAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCommentsAsync.fulfilled, (state, action) => {
        state.loading = false;
        action.payload.items.forEach((comment) => {
          state.comments[comment.data.id] = comment.data as Comment;
        });
        state.pagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
        };
      })
      .addCase(fetchCommentsAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to fetch comments';
      });

    // Fetch Comment By ID
    builder
      .addCase(fetchCommentByIdAsync.pending, (state) => {
        state.loadingComment = true;
        state.error = null;
      })
      .addCase(fetchCommentByIdAsync.fulfilled, (state, action) => {
        state.loadingComment = false;
        state.currentComment = action.payload.data as Comment;
        state.comments[action.payload.data.id] = action.payload.data as Comment;
      })
      .addCase(fetchCommentByIdAsync.rejected, (state, action) => {
        state.loadingComment = false;
        state.error = action.payload || 'Failed to fetch comment';
      });

    // Create Comment
    builder
      .addCase(createCommentAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createCommentAsync.fulfilled, (state, action) => {
        state.loading = false;
        state.comments[action.payload.data.id] = action.payload.data as Comment;
      })
      .addCase(createCommentAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to create comment';
      });

    // Update Comment
    builder
      .addCase(updateCommentAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateCommentAsync.fulfilled, (state, action) => {
        state.loading = false;
        state.comments[action.payload.data.id] = action.payload.data as Comment;
        if (state.currentComment?.id === action.payload.data?.id) {
          state.currentComment = action.payload.data as Comment;
        }
      })
      .addCase(updateCommentAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to update comment';
      });

    // Delete Comment
    builder
      .addCase(deleteCommentAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteCommentAsync.fulfilled, (state, action) => {
        state.loading = false;
        delete state.comments[action.meta.arg];
        if (state.currentComment?.id === action.meta.arg) {
          state.currentComment = null;
        }
      })
      .addCase(deleteCommentAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to delete comment';
      });
  },
});

// ============================================================================
// Actions & Selectors
// ============================================================================

export const { clearError, clearCurrentComment, addComment, removeComment } = commentSlice.actions;

export const selectComments = (state: RootState) => state.comments.comments;
export const selectCommentById = (commentId: number) => (state: RootState) =>
  state.comments.comments[commentId] || null;
export const selectCurrentComment = (state: RootState) => state.comments.currentComment;
export const selectCommentPagination = (state: RootState) => state.comments.pagination;
export const selectCommentLoading = (state: RootState) => state.comments.loading;
export const selectCommentError = (state: RootState) => state.comments.error;
export const selectLoadingComment = (state: RootState) => state.comments.loadingComment;

export default commentSlice.reducer;

