/**
 * Album Slice
 * Redux slice for album state management
 */

import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { albumRepository, reviewRepository } from '@/repositories';
import { Album, Song, Review, Collection, HALResource, EditAlbumFormData, CreateAlbumFormData, ReviewFormData } from '@/types';
import type { RootState } from '../index';

// ============================================================================
// State Interface
// ============================================================================

export interface AlbumState {
  // Albums by ID (normalized state)
  albums: Album[];
  // Ordered albums id list
  orderedAlbumsIds: number[];
  // Current album being viewed
  currentAlbum: Album | null;
  // Related data for current album
  albumSongs: Song[];
  albumReviews: Review[];
  // Pagination info
  pagination: {
    page: number;
    size: number;
    totalCount: number;
  };
  // Loading states
  loading: boolean;
  loadingAlbum: boolean;
  loadingSongs: boolean;
  loadingReviews: boolean;
  // Error state
  error: string | null;
}

// ============================================================================
// Initial State
// ============================================================================

const initialState: AlbumState = {
  albums: [],
  orderedAlbumsIds: [],
  currentAlbum: null,
  albumSongs: [],
  albumReviews: [],
  pagination: {
    page: 1,
    size: 20,
    totalCount: 0,
  },
  loading: false,
  loadingAlbum: false,
  loadingSongs: false,
  loadingReviews: false,
  error: null,
};

// ============================================================================
// Async Thunks
// ============================================================================

export const fetchAlbumsAsync = createAsyncThunk<
  Collection<HALResource<Album>>,
  { page?: number; size?: number; search?: string; filter?: string },
  { rejectValue: string }
>('albums/fetchAlbumsAsync', async ({ page = 1, size = 20, search, filter }, { rejectWithValue }) => {
  try {
    const response = await albumRepository.getAlbums(page, size, search, filter);
    return response as Collection<HALResource<Album>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch albums');
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
>('albums/fetchAlbumSongs', async ({ albumId, page = 1, size = 20 }, { rejectWithValue }) => {
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
>('albums/fetchAlbumReviews', async ({ albumId, page = 1, size = 20, filter }, { rejectWithValue }) => {
  try {
    const response = await albumRepository.getAlbumReviews(albumId, page, size, filter);
    return response as Collection<HALResource<Review>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch album reviews');
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
    },
    addAlbum: (state, action: PayloadAction<Album>) => {
      state.albums.push(action.payload);
    },
    removeAlbum: (state, action: PayloadAction<number>) => {
      state.albums = state.albums.filter((album) => album.id !== action.payload);
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
        action.payload.items.forEach((album) => {
          state.albums[album.data.id] = album.data as Album;
        });
        state.orderedAlbumsIds = action.payload.items.map((album) => album.data.id);
        state.pagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
        };
      })
      .addCase(fetchAlbumsAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to fetch albums';
      });

    builder
      .addCase(fetchAlbumByIdAsync.pending, (state) => {
        state.loadingAlbum = true;
        state.error = null;
      })
      .addCase(fetchAlbumByIdAsync.fulfilled, (state, action) => {
        state.loadingAlbum = false;
        state.currentAlbum = action.payload.data as Album;
        state.albums[action.payload.data.id] = action.payload.data as Album;
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
        state.albums[action.payload.data.id] = action.payload.data as Album;
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
        state.albums[action.payload.data.id] = action.payload.data as Album;
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
        // Sobrescribir el array completo en lugar de acumular
        state.albumReviews = action.payload.items.map((review) => review.data as Review);
      })
      .addCase(fetchAlbumReviewsAsync.rejected, (state, action) => {
        state.loadingReviews = false;
        state.error = action.payload || 'Failed to fetch album reviews';
      });

    builder
      .addCase(addAlbumFavoriteAsync.rejected, (state, action) => {
        state.error = action.payload || 'Failed to add album to favorites';
      })
      .addCase(removeAlbumFavoriteAsync.rejected, (state, action) => {
        state.error = action.payload || 'Failed to remove album from favorites';
      });
  },
});

// ============================================================================
// Actions & Selectors
// ============================================================================

export const { clearError, clearCurrentAlbum, addAlbum, removeAlbum } = albumSlice.actions;

export const selectAlbums = (state: RootState) => state.albums.albums;
export const selectOrderedAlbums = (state: RootState) => state.albums.orderedAlbumsIds.map((id) => state.albums.albums[id]);
export const selectAlbumById = (albumId: number) => (state: RootState) =>
  state.albums.albums.find((album) => album.id === albumId) || null;
export const selectCurrentAlbum = (state: RootState) => state.albums.currentAlbum;
export const selectAlbumSongs = (state: RootState) => state.albums.albumSongs;
export const selectAlbumReviews = (state: RootState) => state.albums.albumReviews;
export const selectAlbumPagination = (state: RootState) => state.albums.pagination;
export const selectAlbumLoading = (state: RootState) => state.albums.loading;
export const selectAlbumError = (state: RootState) => state.albums.error;
export const selectLoadingAlbum = (state: RootState) => state.albums.loadingAlbum;
export const selectLoadingAlbumSongs = (state: RootState) => state.albums.loadingSongs;
export const selectLoadingAlbumReviews = (state: RootState) => state.albums.loadingReviews;

export default albumSlice.reducer;

