/**
 * Artist Slice
 * Redux slice for artist state management
 */

import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { artistRepository } from '@/repositories';
import { Artist, Album, Song, Review, Collection, HALResource, EditArtistFormData, CreateArtistFormData, ReviewFormData } from '@/types';
import type { RootState } from '../index';

// ============================================================================
// State Interface
// ============================================================================

export interface ArtistState {
  // Artists by ID (normalized state)
  artists: Record<number, Artist>;
  // Current artist being viewed
  currentArtist: Artist | null;
  // Related data for current artist
  artistAlbums: Album[];
  artistSongs: Song[];
  artistReviews: Review[];
  // Pagination info
  pagination: {
    page: number;
    size: number;
    totalCount: number;
  };
  // Loading states
  loading: boolean;
  loadingArtist: boolean;
  loadingAlbums: boolean;
  loadingSongs: boolean;
  loadingReviews: boolean;
  // Error state
  error: string | null;
}

// ============================================================================
// Initial State
// ============================================================================

const initialState: ArtistState = {
  artists: {},
  currentArtist: null,
  artistAlbums: [],
  artistSongs: [],
  artistReviews: [],
  pagination: {
    page: 1,
    size: 20,
    totalCount: 0,
  },
  loading: false,
  loadingArtist: false,
  loadingAlbums: false,
  loadingSongs: false,
  loadingReviews: false,
  error: null,
};

// ============================================================================
// Async Thunks
// ============================================================================

/**
 * Fetch paginated artists list
 */
export const fetchArtistsAsync = createAsyncThunk<
  Collection<HALResource<Artist>>,
  { page?: number; size?: number; search?: string; filter?: string },
  { rejectValue: string }
>('artists/fetchArtists', async ({ page = 1, size = 20, search, filter }, { rejectWithValue }) => {
  try {
    const response = await artistRepository.getArtists(page, size, search, filter);
    return response as Collection<HALResource<Artist>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch artists');
  }
});

/**
 * Fetch artist by ID
 */
export const fetchArtistByIdAsync = createAsyncThunk<
  HALResource<Artist>,
  number,
  { rejectValue: string }
>('artists/fetchArtistById', async (artistId, { rejectWithValue }) => {
  try {
    const artist = await artistRepository.getArtistById(artistId);
    return artist as HALResource<Artist>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch artist');
  }
});

/**
 * Create new artist (moderator only)
 */
export const createArtistAsync = createAsyncThunk<
  HALResource<Artist>,
  CreateArtistFormData,
  { rejectValue: string }
>('artists/createArtistAsync', async (artistData, { rejectWithValue }) => {
  try {
    const artist = await artistRepository.createArtist(artistData as CreateArtistFormData);
    return artist as HALResource<Artist>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to create artist');
  }
});

/**
 * Update artist (moderator only)
 */
export const updateArtistAsync = createAsyncThunk<
  HALResource<Artist>,
  { id: number; artistData: EditArtistFormData },
  { rejectValue: string }
>('artists/updateArtistAsync', async ({ id, artistData }, { rejectWithValue }) => {
  try {
    const artist = await artistRepository.updateArtist(id, artistData);
    return artist as HALResource<Artist>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to update artist');
  }
});

/**
 * Delete artist (moderator only)
 */
export const deleteArtistAsync = createAsyncThunk<
  void,
  number,
  { rejectValue: string }
>('artists/deleteArtistAsync', async (artistId, { rejectWithValue }) => {
  try {
    await artistRepository.deleteArtist(artistId);
    return;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to delete artist');
  }
});

/**
 * Fetch artist's albums
 */
export const fetchArtistAlbumsAsync = createAsyncThunk<
  Collection<HALResource<Album>>,
  { artistId: number; page?: number; size?: number },
  { rejectValue: string }
>('artists/fetchArtistAlbums', async ({ artistId, page = 1, size = 20 }, { rejectWithValue }) => {
  try {
    const response = await artistRepository.getArtistAlbums(artistId, page, size);
    return response as Collection<HALResource<Album>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch artist albums');
  }
});

/**
 * Fetch artist's songs
 */
export const fetchArtistSongsAsync = createAsyncThunk<
  Collection<HALResource<Song>>,
  { artistId: number; page?: number; size?: number },
  { rejectValue: string }
>('artists/fetchArtistSongs', async ({ artistId, page = 1, size = 20 }, { rejectWithValue }) => {
  try {
    const response = await artistRepository.getArtistSongs(artistId, page, size);
    return response as Collection<HALResource<Song>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch artist songs');
  }
});

/**
 * Fetch artist's reviews
 */
export const fetchArtistReviewsAsync = createAsyncThunk<
  Collection<HALResource<Review>>,
  { artistId: number; page?: number; size?: number; filter?: string },
  { rejectValue: string }
>('artists/fetchArtistReviews', async ({ artistId, page = 1, size = 20, filter }, { rejectWithValue }) => {
  try {
    const response = await artistRepository.getArtistReviews(artistId, page, size, filter);
    return response as Collection<HALResource<Review>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch artist reviews');
  }
});

/**
 * Create artist review
 */
export const createArtistReviewAsync = createAsyncThunk<
  HALResource<Review>,
  { artistId: number; reviewData: Omit<ReviewFormData, 'itemId' | 'itemType'> },
  { rejectValue: string }
>('artists/createArtistReview', async ({ artistId, reviewData }, { rejectWithValue }) => {
  try {
    const response = await artistRepository.createArtistReview(artistId, reviewData as any);
    return response as HALResource<Review>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to create artist review');
  }
});

/**
 * Add artist to favorites
 */
export const addArtistFavoriteAsync = createAsyncThunk<
  HALResource<Artist>,
  number,
  { rejectValue: string }
>('artists/addFavorite', async (artistId, { rejectWithValue }) => {
  try {
    await artistRepository.addArtistFavorite(artistId);
    return await artistRepository.getArtistById(artistId) as HALResource<Artist>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to add artist to favorites');
  }
});

/**
 * Remove artist from favorites
 */
export const removeArtistFavoriteAsync = createAsyncThunk<
    HALResource<Artist>,
  number,
  { rejectValue: string }
>('artists/removeFavoriteAsync', async (artistId, { rejectWithValue }) => {
  try {
    await artistRepository.removeArtistFavorite(artistId);
    return await artistRepository.getArtistById(artistId) as HALResource<Artist>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to remove artist from favorites');
  }
});

// ============================================================================
// Slice
// ============================================================================

const artistSlice = createSlice({
  name: 'artists',
  initialState,
  reducers: {
    /**
     * Clear error state
     */
    clearError: (state) => {
      state.error = null;
    },

    /**
     * Clear current artist
     */
    clearCurrentArtist: (state) => {
      state.currentArtist = null;
      state.artistAlbums = [];
      state.artistSongs = [];
      state.artistReviews = [];
    },

    /**
     * Add artist to normalized state
     */
    addArtist: (state, action: PayloadAction<Artist>) => {
      state.artists[action.payload.id] = action.payload;
    },

    /**
     * Remove artist from normalized state
     */
    removeArtist: (state, action: PayloadAction<number>) => {
      delete state.artists[action.payload];
    },
  },
  extraReducers: (builder) => {
    // Fetch Artists
    builder
      .addCase(fetchArtistsAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchArtistsAsync.fulfilled, (state, action) => {
        state.loading = false;
        // Add artists to normalized state
        action.payload.items.forEach((artist) => {
          state.artists[artist.data.id] = artist.data as Artist;
        });
        state.pagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
        };
      })
      .addCase(fetchArtistsAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to fetch artists';
      });

    // Fetch Artist By ID
    builder
      .addCase(fetchArtistByIdAsync.pending, (state) => {
        state.loadingArtist = true;
        state.error = null;
      })
      .addCase(fetchArtistByIdAsync.fulfilled, (state, action) => {
        state.loadingArtist = false;
        state.currentArtist = action.payload.data as Artist;
        state.artists[action.payload.data.id] = action.payload.data as Artist;
      })
      .addCase(fetchArtistByIdAsync.rejected, (state, action) => {
        state.loadingArtist = false;
        state.error = action.payload || 'Failed to fetch artist';
      });

    // Create Artist
    builder
      .addCase(createArtistAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createArtistAsync.fulfilled, (state, action) => {
        state.loading = false;
        state.artists[action.payload.data.id] = action.payload.data as Artist;
      })
      .addCase(createArtistAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to create artist';
      });

    // Update Artist
    builder
      .addCase(updateArtistAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateArtistAsync.fulfilled, (state, action) => {
        state.loading = false;
        state.artists[action.payload.data.id] = action.payload.data as Artist;
        if (state.currentArtist?.id === action.payload.data?.id) {
          state.currentArtist = action.payload.data as Artist;
        }
      })
      .addCase(updateArtistAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to update artist';
      });

    // Delete Artist
    builder
      .addCase(deleteArtistAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteArtistAsync.fulfilled, (state, action) => {
        state.loading = false;
        delete state.artists[action.meta.arg];
        if (state.currentArtist?.id === action.meta.arg) {
          state.currentArtist = null;
        }
      })
      .addCase(deleteArtistAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string || 'Failed to delete artist';
      });

    // Fetch Artist Albums
    builder
      .addCase(fetchArtistAlbumsAsync.pending, (state) => {
        state.loadingAlbums = true;
        state.error = null;
      })
      .addCase(fetchArtistAlbumsAsync.fulfilled, (state, action) => {
        state.loadingAlbums = false;
        // Sobrescribir el array completo en lugar de acumular
        state.artistAlbums = action.payload.items.map((album) => album.data as Album);
      })
      .addCase(fetchArtistAlbumsAsync.rejected, (state, action) => {
        state.loadingAlbums = false;
        state.error = action.payload || 'Failed to fetch artist albums';
      });

    // Fetch Artist Songs
    builder
      .addCase(fetchArtistSongsAsync.pending, (state) => {
        state.loadingSongs = true;
        state.error = null;
      })
      .addCase(fetchArtistSongsAsync.fulfilled, (state, action) => {
        state.loadingSongs = false;
        // Sobrescribir el array completo en lugar de acumular
        state.artistSongs = action.payload.items.map((song) => song.data as Song);
      })
      .addCase(fetchArtistSongsAsync.rejected, (state, action) => {
        state.loadingSongs = false;
        state.error = action.payload || 'Failed to fetch artist songs';
      });

    // Fetch Artist Reviews
    builder
      .addCase(fetchArtistReviewsAsync.pending, (state) => {
        state.loadingReviews = true;
        state.error = null;
      })
      .addCase(fetchArtistReviewsAsync.fulfilled, (state, action) => {
        state.loadingReviews = false;
        // Sobrescribir el array completo en lugar de acumular
        state.artistReviews = action.payload.items.map((review) => review.data as Review);
      })
      .addCase(fetchArtistReviewsAsync.rejected, (state, action) => {
        state.loadingReviews = false;
        state.error = action.payload || 'Failed to fetch artist reviews';
      });

    // Add/Remove Favorite
    builder
      .addCase(addArtistFavoriteAsync.fulfilled, () => {
        // Success - UI will update based on user favorites list
      })
      .addCase(addArtistFavoriteAsync.rejected, (state, action) => {
        state.error = action.payload || 'Failed to add artist to favorites';
      })
      .addCase(removeArtistFavoriteAsync.fulfilled, () => {
        // Success - UI will update based on user favorites list
      })
      .addCase(removeArtistFavoriteAsync.rejected, (state, action) => {
        state.error = action.payload || 'Failed to remove artist from favorites';
      });
  },
});

// ============================================================================
// Actions
// ============================================================================

export const { clearError, clearCurrentArtist, addArtist, removeArtist } = artistSlice.actions;

// ============================================================================
// Selectors
// ============================================================================

export const selectArtists = (state: RootState) => state.artists.artists;
export const selectArtistById = (artistId: number) => (state: RootState) =>
  state.artists.artists[artistId] || null;
export const selectCurrentArtist = (state: RootState) => state.artists.currentArtist;
export const selectArtistAlbums = (state: RootState) => state.artists.artistAlbums;
export const selectArtistSongs = (state: RootState) => state.artists.artistSongs;
export const selectArtistReviews = (state: RootState) => state.artists.artistReviews;
export const selectArtistPagination = (state: RootState) => state.artists.pagination;
export const selectArtistLoading = (state: RootState) => state.artists.loading;
export const selectArtistError = (state: RootState) => state.artists.error;
export const selectLoadingArtist = (state: RootState) => state.artists.loadingArtist;
export const selectLoadingAlbums = (state: RootState) => state.artists.loadingAlbums;
export const selectLoadingSongs = (state: RootState) => state.artists.loadingSongs;
export const selectLoadingReviews = (state: RootState) => state.artists.loadingReviews;

// ============================================================================
// Reducer Export
// ============================================================================

export default artistSlice.reducer;

