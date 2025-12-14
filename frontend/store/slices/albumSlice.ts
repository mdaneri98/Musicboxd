/**
 * Album Slice
 * Redux slice for album state management
 */

import { createSlice, createAsyncThunk, PayloadAction, createSelector } from '@reduxjs/toolkit';
import { albumRepository, reviewRepository } from '@/repositories';
import { Album, Song, Review, Collection, HALResource, EditAlbumFormData, CreateAlbumFormData, ReviewFormData } from '@/types';
import type { RootState } from '../index';
import { blockReviewAsync, unblockReviewAsync, likeReviewAsync, unlikeReviewAsync } from './reviewSlice';

// ============================================================================
// State Interface
// ============================================================================

export interface AlbumState {
  // Albums by ID (normalized state)
  albums: Record<number, Album>;
  // Ordered albums id list
  orderedAlbumsIds: number[];
  // Current album being viewed
  currentAlbum: Album | null;
  // Related data for current album
  albumSongs: Song[];
  albumReviews: Review[];
  // Current user's review for the album
  currentUserReview: Review | null;
  // Pagination info
  pagination: {
    page: number;
    size: number;
    totalCount: number;
    hasMore: boolean;
  };
  reviewsPagination: {
    page: number;
    size: number;
    totalCount: number;
    hasMore: boolean;
  };
  // Loading states
  loading: boolean;
  loadingMore: boolean;
  loadingAlbum: boolean;
  loadingSongs: boolean;
  loadingReviews: boolean;
  loadingMoreReviews: boolean;
  // Error state
  error: string | null;
}

// ============================================================================
// Initial State
// ============================================================================

const initialState: AlbumState = {
  albums: {},
  orderedAlbumsIds: [],
  currentAlbum: null,
  albumSongs: [],
  albumReviews: [],
  currentUserReview: null,
  pagination: {
    page: 1,
    size: 10,
    totalCount: 0,
    hasMore: true,
  },
  reviewsPagination: {
    page: 1,
    size: 10,
    totalCount: 0,
    hasMore: true,
  },
  loading: false,
  loadingMore: false,
  loadingAlbum: false,
  loadingSongs: false,
  loadingReviews: false,
  loadingMoreReviews: false,
  error: null,
};

// ============================================================================
// Async Thunks
// ============================================================================

export const fetchAlbumsAsync = createAsyncThunk<
  Collection<HALResource<Album>>,
  { page?: number; size?: number; search?: string; filter?: string },
  { rejectValue: string }
>('albums/fetchAlbumsAsync', async ({ page = 1, size = 10, search, filter }, { rejectWithValue }) => {
  try {
    const response = await albumRepository.getAlbums(page, size, search, filter);
    return response as Collection<HALResource<Album>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch albums');
  }
});

export const fetchMoreAlbumsAsync = createAsyncThunk<
  Collection<HALResource<Album>>,
  { page: number; size?: number; search?: string; filter?: string },
  { rejectValue: string }
>('albums/fetchMoreAlbumsAsync', async ({ page, size = 10, search, filter }, { rejectWithValue }) => {
  try {
    const response = await albumRepository.getAlbums(page, size, search, filter);
    return response as Collection<HALResource<Album>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch more albums');
  }
});

export const fetchAlbumByIdAsync = createAsyncThunk<
  HALResource<Album>,
  number,
  { rejectValue: string }
>('albums/fetchAlbumById', async (albumId, { rejectWithValue }) => {
  try {
    const album = await albumRepository.getAlbumById(albumId);
    return album as HALResource<Album>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch album');
  }
});

export const createAlbumAsync = createAsyncThunk<
  HALResource<Album>,
  CreateAlbumFormData,
  { rejectValue: string }
>('albums/createAlbumAsync', async (albumData, { rejectWithValue }) => {
  try {
    const album = await albumRepository.createAlbum(albumData);
    return album as HALResource<Album>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to create album');
  }
});

export const updateAlbumAsync = createAsyncThunk<
  HALResource<Album>,
  { id: number; albumData: EditAlbumFormData },
  { rejectValue: string }
>('albums/updateAlbumAsync', async ({ id, albumData }, { rejectWithValue }) => {
  try {
    const album = await albumRepository.updateAlbum(id, albumData);
    return album as HALResource<Album>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to update album');
  }
});

export const deleteAlbumAsync = createAsyncThunk<
  number,
  number,
  { rejectValue: string }
>('albums/deleteAlbumAsync', async (albumId, { rejectWithValue }) => {
  try {
    await albumRepository.deleteAlbum(albumId);
    return albumId;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to delete album');
  }
});

export const fetchAlbumSongsAsync = createAsyncThunk<
  Collection<HALResource<Song>>,
  { albumId: number; page?: number; size?: number },
  { rejectValue: string }
>('albums/fetchAlbumSongs', async ({ albumId, page = 1, size = 10 }, { rejectWithValue }) => {
  try {
    const response = await albumRepository.getAlbumSongs(albumId, page, size);
    return response as Collection<HALResource<Song>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch album songs');
  }
});

export const fetchAlbumReviewsAsync = createAsyncThunk<
  Collection<HALResource<Review>>,
  { albumId: number; page?: number; size?: number; filter?: string },
  { rejectValue: string }
>('albums/fetchAlbumReviews', async ({ albumId, page = 1, size = 10, filter }, { rejectWithValue }) => {
  try {
    const response = await albumRepository.getAlbumReviews(albumId, page, size, filter);
    return response as Collection<HALResource<Review>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch album reviews');
  }
});

export const fetchMoreAlbumReviewsAsync = createAsyncThunk<
  Collection<HALResource<Review>>,
  { albumId: number; page: number; size?: number; filter?: string },
  { rejectValue: string }
>('albums/fetchMoreAlbumReviews', async ({ albumId, page, size = 10, filter }, { rejectWithValue }) => {
  try {
    const response = await albumRepository.getAlbumReviews(albumId, page, size, filter);
    return response as Collection<HALResource<Review>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch more album reviews');
  }
});

/**
 * Fetch the current user's review for an album
 */
export const fetchUserAlbumReviewAsync = createAsyncThunk<
  Review | null,
  { albumId: number; userId: number },
  { rejectValue: string }
>('albums/fetchUserAlbumReview', async ({ albumId, userId }, { rejectWithValue }) => {
  try {
    const review = await albumRepository.getUserReviewForAlbum(albumId, userId);
    return review;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch user album review');
  }
});

export const createAlbumReviewAsync = createAsyncThunk<
  HALResource<Review>,
  ReviewFormData,
  { rejectValue: string }
>('albums/createAlbumReview', async (reviewData, { rejectWithValue }) => {
  try {
    const reviewResponse = await reviewRepository.createReview(reviewData);
    return reviewResponse as HALResource<Review>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to create album review');
  }
});

export const addAlbumFavoriteAsync = createAsyncThunk<
  HALResource<Album>,
  number,
  { rejectValue: string }
>('albums/addFavorite', async (albumId, { rejectWithValue }) => {
  try {
    await albumRepository.addAlbumFavorite(albumId);
    return await albumRepository.getAlbumById(albumId) as HALResource<Album>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to add album to favorites');
  }
});

export const removeAlbumFavoriteAsync = createAsyncThunk<
  HALResource<Album>,
  number,
  { rejectValue: string }
>('albums/removeFavorite', async (albumId, { rejectWithValue }) => {
  try {
    await albumRepository.removeAlbumFavorite(albumId);
    return await albumRepository.getAlbumById(albumId) as HALResource<Album>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to remove album from favorites');
  }
});

// ============================================================================
// Slice
// ============================================================================

const albumSlice = createSlice({
  name: 'albums',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearCurrentAlbum: (state) => {
      state.currentAlbum = null;
      state.albumSongs = [];
      state.albumReviews = [];
      state.currentUserReview = null;
      state.reviewsPagination = { page: 1, size: 10, totalCount: 0, hasMore: true };
    },
    clearAlbums: (state) => {
      state.albums = {};
      state.orderedAlbumsIds = [];
      state.pagination = { page: 1, size: 10, totalCount: 0, hasMore: true };
    },
    addAlbum: (state, action: PayloadAction<Album>) => {
      state.albums[action.payload.id] = action.payload;
    },
    removeAlbum: (state, action: PayloadAction<number>) => {
      delete state.albums[action.payload];
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchAlbumsAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchAlbumsAsync.fulfilled, (state, action) => {
        state.loading = false;
        state.albums = {};
        state.orderedAlbumsIds = [];
        action.payload.items.forEach((album) => {
          state.albums[album.data.id] = album.data as Album;
          state.orderedAlbumsIds.push(album.data.id);
        });
        const hasMore = action.payload.currentPage * action.payload.pageSize < action.payload.totalCount;
        state.pagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
          hasMore,
        };
      })
      .addCase(fetchAlbumsAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to fetch albums';
      });

    builder
      .addCase(fetchMoreAlbumsAsync.pending, (state) => {
        state.loadingMore = true;
        state.error = null;
      })
      .addCase(fetchMoreAlbumsAsync.fulfilled, (state, action) => {
        state.loadingMore = false;
        action.payload.items.forEach((album) => {
          if (!state.albums[album.data.id]) {
            state.albums[album.data.id] = album.data as Album;
            state.orderedAlbumsIds.push(album.data.id);
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
      .addCase(fetchMoreAlbumsAsync.rejected, (state, action) => {
        state.loadingMore = false;
        state.error = action.payload || 'Failed to fetch more albums';
      });

    builder
      .addCase(fetchAlbumByIdAsync.pending, (state) => {
        state.loadingAlbum = true;
        state.error = null;
      })
      .addCase(fetchAlbumByIdAsync.fulfilled, (state, action) => {
        state.loadingAlbum = false;
        state.currentAlbum = action.payload.data as Album;
        if (!state.albums[action.payload.data.id]) {
          state.albums[action.payload.data.id] = action.payload.data as Album;
        }
      })
      .addCase(fetchAlbumByIdAsync.rejected, (state, action) => {
        state.loadingAlbum = false;
        state.error = action.payload || 'Failed to fetch album';
      });

    builder
      .addCase(createAlbumAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createAlbumAsync.fulfilled, (state, action) => {
        state.loading = false;
        if (!state.albums[action.payload.data.id]) {
          state.albums[action.payload.data.id] = action.payload.data as Album;
        }
      })
      .addCase(createAlbumAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to create album';
      });

    builder
      .addCase(updateAlbumAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateAlbumAsync.fulfilled, (state, action) => {
        state.loading = false;
        if (!state.albums[action.payload.data.id]) {
          state.albums[action.payload.data.id] = action.payload.data as Album;
        }
        if (state.currentAlbum?.id === action.payload.data?.id) {
          state.currentAlbum = action.payload.data as Album;
        }
      })
      .addCase(updateAlbumAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to update album';
      });

    builder
      .addCase(deleteAlbumAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteAlbumAsync.fulfilled, (state, action) => {
        state.loading = false;
        delete state.albums[action.meta.arg];
        if (state.currentAlbum?.id === action.meta.arg) {
          state.currentAlbum = null;
        }
      })
      .addCase(deleteAlbumAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to delete album';
      });

    builder
      .addCase(fetchAlbumSongsAsync.pending, (state) => {
        state.loadingSongs = true;
        state.error = null;
      })
      .addCase(fetchAlbumSongsAsync.fulfilled, (state, action) => {
        state.loadingSongs = false;
        // Sobrescribir el array completo en lugar de acumular
        state.albumSongs = action.payload.items.map((song) => song.data as Song);
      })
      .addCase(fetchAlbumSongsAsync.rejected, (state, action) => {
        state.loadingSongs = false;
        state.error = action.payload || 'Failed to fetch album songs';
      });

    builder
      .addCase(fetchAlbumReviewsAsync.pending, (state) => {
        state.loadingReviews = true;
        state.error = null;
      })
      .addCase(fetchAlbumReviewsAsync.fulfilled, (state, action) => {
        state.loadingReviews = false;
        state.albumReviews = action.payload.items.map((review) => review.data as Review);
        const hasMore = action.payload.currentPage * action.payload.pageSize < action.payload.totalCount;
        state.reviewsPagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
          hasMore,
        };
      })
      .addCase(fetchAlbumReviewsAsync.rejected, (state, action) => {
        state.loadingReviews = false;
        state.error = action.payload || 'Failed to fetch album reviews';
      });

    builder
      .addCase(fetchMoreAlbumReviewsAsync.pending, (state) => {
        state.loadingMoreReviews = true;
        state.error = null;
      })
      .addCase(fetchMoreAlbumReviewsAsync.fulfilled, (state, action) => {
        state.loadingMoreReviews = false;
        const existingIds = new Set(state.albumReviews.map(r => r.id));
        const newReviews = action.payload.items
          .map((review) => review.data as Review)
          .filter(r => !existingIds.has(r.id));
        state.albumReviews = [...state.albumReviews, ...newReviews];
        const hasMore = action.payload.currentPage * action.payload.pageSize < action.payload.totalCount;
        state.reviewsPagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
          hasMore,
        };
      })
      .addCase(fetchMoreAlbumReviewsAsync.rejected, (state, action) => {
        state.loadingMoreReviews = false;
        state.error = action.payload || 'Failed to fetch more album reviews';
      });

    // Fetch User's Album Review
    builder
      .addCase(fetchUserAlbumReviewAsync.fulfilled, (state, action) => {
        state.currentUserReview = action.payload;
      })
      .addCase(fetchUserAlbumReviewAsync.rejected, (state) => {
        state.currentUserReview = null;
      });

    builder
      .addCase(addAlbumFavoriteAsync.fulfilled, (state, action) => {
        const updatedAlbum = action.payload.data as Album;
        state.currentAlbum = updatedAlbum;
        state.albums[updatedAlbum.id] = updatedAlbum;
      })
      .addCase(removeAlbumFavoriteAsync.fulfilled, (state, action) => {
        const updatedAlbum = action.payload.data as Album;
        state.currentAlbum = updatedAlbum;
        state.albums[updatedAlbum.id] = updatedAlbum;
      });

    builder
      .addCase(blockReviewAsync.fulfilled, (state, action) => {
        const reviewData = action.payload.data as Review;
        const index = state.albumReviews.findIndex((r) => r.id === reviewData.id);
        if (index !== -1) {
          state.albumReviews[index] = reviewData;
        }
      })
      .addCase(unblockReviewAsync.fulfilled, (state, action) => {
        const reviewData = action.payload.data as Review;
        const index = state.albumReviews.findIndex((r) => r.id === reviewData.id);
        if (index !== -1) {
          state.albumReviews[index] = reviewData;
        }
      });

    // Like/Unlike Review - Update albumReviews list
    builder
      .addCase(likeReviewAsync.fulfilled, (state, action) => {
        const reviewId = action.meta.arg;
        const index = state.albumReviews.findIndex((r) => r.id === reviewId);
        if (index !== -1) {
          state.albumReviews[index].likes = (state.albumReviews[index].likes || 0) + 1;
          state.albumReviews[index].liked = true;
        }
      })
      .addCase(unlikeReviewAsync.fulfilled, (state, action) => {
        const reviewId = action.meta.arg;
        const index = state.albumReviews.findIndex((r) => r.id === reviewId);
        if (index !== -1) {
          state.albumReviews[index].likes = Math.max(0, (state.albumReviews[index].likes || 0) - 1);
          state.albumReviews[index].liked = false;
        }
      });
  },
});

// ============================================================================
// Actions & Selectors
// ============================================================================

export const { clearError, clearCurrentAlbum, clearAlbums, addAlbum, removeAlbum } = albumSlice.actions;

export const selectAlbums = (state: RootState) => state.albums.albums;
export const selectAlbumIds = (state: RootState) => state.albums.orderedAlbumsIds;
export const selectOrderedAlbums = createSelector(
  [selectAlbums, selectAlbumIds],
  (albums, ids) => ids.map((id) => albums[id]).filter(Boolean)
);
export const selectAlbumById = (albumId: number) => (state: RootState) =>
  state.albums.albums[albumId] || null;
export const selectCurrentAlbum = (state: RootState) => state.albums.currentAlbum;
export const selectAlbumSongs = (state: RootState) => state.albums.albumSongs;
export const selectAlbumReviews = (state: RootState) => state.albums.albumReviews;
export const selectAlbumPagination = (state: RootState) => state.albums.pagination;
export const selectAlbumReviewsPagination = (state: RootState) => state.albums.reviewsPagination;
export const selectAlbumLoading = (state: RootState) => state.albums.loading;
export const selectAlbumLoadingMore = (state: RootState) => state.albums.loadingMore;
export const selectAlbumError = (state: RootState) => state.albums.error;
export const selectLoadingAlbum = (state: RootState) => state.albums.loadingAlbum;
export const selectLoadingMoreAlbumReviews = (state: RootState) => state.albums.loadingMoreReviews;
export const selectAlbumsHasMore = (state: RootState) => state.albums.pagination.hasMore;
export const selectAlbumReviewsHasMore = (state: RootState) => state.albums.reviewsPagination.hasMore;
export const selectLoadingAlbumSongs = (state: RootState) => state.albums.loadingSongs;
export const selectLoadingAlbumReviews = (state: RootState) => state.albums.loadingReviews;
export const selectCurrentUserAlbumReview = (state: RootState) => state.albums.currentUserReview;

export default albumSlice.reducer;

