/**
 * Artist Slice
 * Redux slice for artist state management
 */

import { createSlice, createAsyncThunk, PayloadAction, createSelector } from '@reduxjs/toolkit';
import { artistRepository, reviewRepository } from '@/repositories';
import { Artist, Album, Song, Review, Collection, HALResource, EditArtistFormData, CreateArtistFormData, ReviewFormData } from '@/types';
import type { RootState } from '../index';
import { blockReviewAsync, unblockReviewAsync, likeReviewAsync, unlikeReviewAsync } from './reviewSlice';

// ============================================================================
// State Interface
// ============================================================================

export interface ArtistState {
  // Artists by ID (normalized state)
  artists: Record<number, Artist>;
  // Ordered artists id list
  orderedArtistsIds: number[];
  // Current artist being viewed
  currentArtist: Artist | null;
  // Related data for current artist
  artistAlbums: Album[];
  artistSongs: Song[];
  artistReviews: Review[];
  // Current user's review for the artist
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
  loadingArtist: boolean;
  loadingAlbums: boolean;
  loadingSongs: boolean;
  loadingReviews: boolean;
  loadingMoreReviews: boolean;
  // Error state
  error: string | null;
}

// ============================================================================
// Initial State
// ============================================================================

const initialState: ArtistState = {
  artists: {},
  orderedArtistsIds: [],
  currentArtist: null,
  artistAlbums: [],
  artistSongs: [],
  artistReviews: [],
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
  loadingArtist: false,
  loadingAlbums: false,
  loadingSongs: false,
  loadingReviews: false,
  loadingMoreReviews: false,
  error: null,
};

// ============================================================================
// Async Thunks
// ============================================================================

/**
 * Fetch paginated artists list (initial load - replaces data)
 */
export const fetchArtistsAsync = createAsyncThunk<
  Collection<HALResource<Artist>>,
  { page?: number; size?: number; search?: string; filter?: string },
  { rejectValue: string }
>('artists/fetchArtists', async ({ page = 1, size = 10, search, filter }, { rejectWithValue }) => {
  try {
    const response = await artistRepository.getArtists(page, size, search, filter);
    return response as Collection<HALResource<Artist>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch artists');
  }
});

/**
 * Fetch more artists (infinite scroll - appends data)
 */
export const fetchMoreArtistsAsync = createAsyncThunk<
  Collection<HALResource<Artist>>,
  { page: number; size?: number; search?: string; filter?: string },
  { rejectValue: string }
>('artists/fetchMoreArtists', async ({ page, size = 10, search, filter }, { rejectWithValue }) => {
  try {
    const response = await artistRepository.getArtists(page, size, search, filter);
    return response as Collection<HALResource<Artist>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch more artists');
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
    const artist = await artistRepository.createArtist(artistData);
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
>('artists/fetchArtistAlbums', async ({ artistId, page = 1, size = 10 }, { rejectWithValue }) => {
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
>('artists/fetchArtistSongs', async ({ artistId, page = 1, size = 10 }, { rejectWithValue }) => {
  try {
    const response = await artistRepository.getArtistSongs(artistId, page, size);
    return response as Collection<HALResource<Song>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch artist songs');
  }
});

/**
 * Fetch artist's reviews (initial load - replaces data)
 */
export const fetchArtistReviewsAsync = createAsyncThunk<
  Collection<HALResource<Review>>,
  { artistId: number; page?: number; size?: number; filter?: string },
  { rejectValue: string }
>('artists/fetchArtistReviews', async ({ artistId, page = 1, size = 10, filter }, { rejectWithValue }) => {
  try {
    const response = await artistRepository.getArtistReviews(artistId, page, size, filter);
    return response as Collection<HALResource<Review>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch artist reviews');
  }
});

/**
 * Fetch more artist reviews (infinite scroll - appends data)
 */
export const fetchMoreArtistReviewsAsync = createAsyncThunk<
  Collection<HALResource<Review>>,
  { artistId: number; page: number; size?: number; filter?: string },
  { rejectValue: string }
>('artists/fetchMoreArtistReviews', async ({ artistId, page, size = 10, filter }, { rejectWithValue }) => {
  try {
    const response = await artistRepository.getArtistReviews(artistId, page, size, filter);
    return response as Collection<HALResource<Review>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch more artist reviews');
  }
});

/**
 * Fetch the current user's review for an artist
 */
export const fetchUserArtistReviewAsync = createAsyncThunk<
  Review | null,
  { artistId: number; userId: number },
  { rejectValue: string }
>('artists/fetchUserArtistReview', async ({ artistId, userId }, { rejectWithValue }) => {
  try {
    const review = await artistRepository.getUserReviewForArtist(artistId, userId);
    return review;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch user artist review');
  }
});

export const createArtistReviewAsync = createAsyncThunk<
  HALResource<Review>,
  ReviewFormData,
  { rejectValue: string }
>('artists/createArtistReview', async (reviewData, { rejectWithValue }) => {
  try {
    const review = await reviewRepository.createReview(reviewData);
    return review as HALResource<Review>;
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
      state.currentUserReview = null;
      state.reviewsPagination = { page: 1, size: 10, totalCount: 0, hasMore: true };
    },

    /**
     * Clear artists list (for filter change)
     */
    clearArtists: (state) => {
      state.artists = {};
      state.orderedArtistsIds = [];
      state.pagination = { page: 1, size: 10, totalCount: 0, hasMore: true };
    },

    /**
     * Add artist to normalized state
     */
    addArtist: (state, action: PayloadAction<Artist>) => {
      state.artists[action.payload.id] = action.payload;
      if (!state.orderedArtistsIds.includes(action.payload.id)) {
        state.orderedArtistsIds.push(action.payload.id);
      }
    },

    /**
     * Remove artist from normalized state
     */
    removeArtist: (state, action: PayloadAction<number>) => {
      delete state.artists[action.payload];
      state.orderedArtistsIds = state.orderedArtistsIds.filter((id) => id !== action.payload);
      },
  },
  extraReducers: (builder) => {
    // Fetch Artists (initial load - replaces data)
    builder
      .addCase(fetchArtistsAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchArtistsAsync.fulfilled, (state, action) => {
        state.loading = false;
        // Clear and replace data
        state.artists = {};
        state.orderedArtistsIds = [];
        action.payload.items.forEach((artist) => {
          state.artists[artist.data.id] = artist.data as Artist;
          state.orderedArtistsIds.push(artist.data.id);
        });
        const hasMore = action.payload.currentPage * action.payload.pageSize < action.payload.totalCount;
        state.pagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
          hasMore,
        };
      })
      .addCase(fetchArtistsAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to fetch artists';
      });

    // Fetch More Artists (infinite scroll - appends data)
    builder
      .addCase(fetchMoreArtistsAsync.pending, (state) => {
        state.loadingMore = true;
        state.error = null;
      })
      .addCase(fetchMoreArtistsAsync.fulfilled, (state, action) => {
        state.loadingMore = false;
        action.payload.items.forEach((artist) => {
          if (!state.artists[artist.data.id]) {
            state.artists[artist.data.id] = artist.data as Artist;
            state.orderedArtistsIds.push(artist.data.id);
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
      .addCase(fetchMoreArtistsAsync.rejected, (state, action) => {
        state.loadingMore = false;
        state.error = action.payload || 'Failed to fetch more artists';
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
        if (!state.artists[action.payload.data.id]) {
          state.artists[action.payload.data.id] = action.payload.data as Artist;
        }
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

    // Fetch Artist Reviews (initial load - replaces data)
    builder
      .addCase(fetchArtistReviewsAsync.pending, (state) => {
        state.loadingReviews = true;
        state.error = null;
      })
      .addCase(fetchArtistReviewsAsync.fulfilled, (state, action) => {
        state.loadingReviews = false;
        state.artistReviews = action.payload.items.map((review) => review.data as Review);
        const hasMore = action.payload.currentPage * action.payload.pageSize < action.payload.totalCount;
        state.reviewsPagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
          hasMore,
        };
      })
      .addCase(fetchArtistReviewsAsync.rejected, (state, action) => {
        state.loadingReviews = false;
        state.error = action.payload || 'Failed to fetch artist reviews';
      });

    // Fetch More Artist Reviews (infinite scroll - appends data)
    builder
      .addCase(fetchMoreArtistReviewsAsync.pending, (state) => {
        state.loadingMoreReviews = true;
        state.error = null;
      })
      .addCase(fetchMoreArtistReviewsAsync.fulfilled, (state, action) => {
        state.loadingMoreReviews = false;
        const existingIds = new Set(state.artistReviews.map(r => r.id));
        const newReviews = action.payload.items
          .map((review) => review.data as Review)
          .filter(r => !existingIds.has(r.id));
        state.artistReviews = [...state.artistReviews, ...newReviews];
        const hasMore = action.payload.currentPage * action.payload.pageSize < action.payload.totalCount;
        state.reviewsPagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
          hasMore,
        };
      })
      .addCase(fetchMoreArtistReviewsAsync.rejected, (state, action) => {
        state.loadingMoreReviews = false;
        state.error = action.payload || 'Failed to fetch more artist reviews';
      });

    // Fetch User's Artist Review
    builder
      .addCase(fetchUserArtistReviewAsync.fulfilled, (state, action) => {
        state.currentUserReview = action.payload;
      })
      .addCase(fetchUserArtistReviewAsync.rejected, (state) => {
        state.currentUserReview = null;
      });

    // Add/Remove Favorite
    builder
      .addCase(addArtistFavoriteAsync.fulfilled, (state, action) => {
        const updatedArtist = action.payload.data as Artist;
        state.currentArtist = updatedArtist;
        state.artists[updatedArtist.id] = updatedArtist;
      })
      .addCase(removeArtistFavoriteAsync.fulfilled, (state, action) => {
        const updatedArtist = action.payload.data as Artist;
        state.currentArtist = updatedArtist;
        state.artists[updatedArtist.id] = updatedArtist;
      });

    builder
      .addCase(blockReviewAsync.fulfilled, (state, action) => {
        const reviewData = action.payload.data as Review;
        const index = state.artistReviews.findIndex((r) => r.id === reviewData.id);
        if (index !== -1) {
          state.artistReviews[index] = reviewData;
        }
      })
      .addCase(unblockReviewAsync.fulfilled, (state, action) => {
        const reviewData = action.payload.data as Review;
        const index = state.artistReviews.findIndex((r) => r.id === reviewData.id);
        if (index !== -1) {
          state.artistReviews[index] = reviewData;
        }
      });

    // Like/Unlike Review - Update artistReviews list
    builder
      .addCase(likeReviewAsync.fulfilled, (state, action) => {
        const reviewId = action.meta.arg;
        const index = state.artistReviews.findIndex((r) => r.id === reviewId);
        if (index !== -1) {
          state.artistReviews[index].likes = (state.artistReviews[index].likes || 0) + 1;
          state.artistReviews[index].liked = true;
        }
      })
      .addCase(unlikeReviewAsync.fulfilled, (state, action) => {
        const reviewId = action.meta.arg;
        const index = state.artistReviews.findIndex((r) => r.id === reviewId);
        if (index !== -1) {
          state.artistReviews[index].likes = Math.max(0, (state.artistReviews[index].likes || 0) - 1);
          state.artistReviews[index].liked = false;
        }
      });
  },
});

// ============================================================================
// Actions
// ============================================================================

export const { clearError, clearCurrentArtist, clearArtists, addArtist, removeArtist } = artistSlice.actions;

// ============================================================================
// Selectors
// ============================================================================

export const selectArtists = (state: RootState) => state.artists.artists;
export const selectArtistIds = (state: RootState) => state.artists.orderedArtistsIds;
export const selectOrderedArtists = createSelector(
  [selectArtists, selectArtistIds],
  (artists, ids) => ids.map((id) => artists[id]).filter(Boolean)
);
export const selectArtistById = (artistId: number) => (state: RootState) =>
  state.artists.artists[artistId] || null;
export const selectCurrentArtist = (state: RootState) => state.artists.currentArtist;
export const selectArtistAlbums = (state: RootState) => state.artists.artistAlbums;
export const selectArtistSongs = (state: RootState) => state.artists.artistSongs;
export const selectArtistReviews = (state: RootState) => state.artists.artistReviews;
export const selectArtistPagination = (state: RootState) => state.artists.pagination;
export const selectArtistReviewsPagination = (state: RootState) => state.artists.reviewsPagination;
export const selectArtistLoading = (state: RootState) => state.artists.loading;
export const selectArtistLoadingMore = (state: RootState) => state.artists.loadingMore;
export const selectArtistError = (state: RootState) => state.artists.error;
export const selectLoadingArtist = (state: RootState) => state.artists.loadingArtist;
export const selectLoadingAlbums = (state: RootState) => state.artists.loadingAlbums;
export const selectLoadingSongs = (state: RootState) => state.artists.loadingSongs;
export const selectLoadingReviews = (state: RootState) => state.artists.loadingReviews;
export const selectLoadingMoreArtistReviews = (state: RootState) => state.artists.loadingMoreReviews;
export const selectArtistsHasMore = (state: RootState) => state.artists.pagination.hasMore;
export const selectArtistReviewsHasMore = (state: RootState) => state.artists.reviewsPagination.hasMore;
export const selectCurrentUserArtistReview = (state: RootState) => state.artists.currentUserReview;

// ============================================================================
// Reducer Export
// ============================================================================

export default artistSlice.reducer;

