/**
 * Song Slice
 * Redux slice for song state management
 */

import { createSlice, createAsyncThunk, PayloadAction, createSelector } from '@reduxjs/toolkit';
import { songRepository, reviewRepository } from '@/repositories';
import { Song, Review, Collection, HALResource, CreateSongFormData, ReviewFormData } from '@/types';
import type { RootState } from '../index';
import { EditSongFormData } from '@/types/forms';
import { blockReviewAsync, unblockReviewAsync, likeReviewAsync, unlikeReviewAsync } from './reviewSlice';

// ============================================================================
// State Interface
// ============================================================================

export interface SongState {
  // Songs by ID (normalized state)
  songs: Record<number, Song>;
  // Ordered songs id list
  orderedSongsIds: number[];
  // Current song being viewed
  currentSong: Song | null;
  // Related data for current song
  songReviews: Review[];
  // Current user's review for the song
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
  loadingSong: boolean;
  loadingReviews: boolean;
  loadingMoreReviews: boolean;
  // Error state
  error: string | null;
}

// ============================================================================
// Initial State
// ============================================================================

const initialState: SongState = {
  songs: {},
  orderedSongsIds: [],
  currentSong: null,
  songReviews: [],
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
  loadingSong: false,
  loadingReviews: false,
  loadingMoreReviews: false,
  error: null,
};

// ============================================================================
// Async Thunks
// ============================================================================

export const fetchSongsAsync = createAsyncThunk<
  Collection<HALResource<Song>>,
  { page?: number; size?: number; search?: string; filter?: string },
  { rejectValue: string }
>('songs/fetchSongs', async ({ page = 1, size = 10, search, filter }, { rejectWithValue }) => {
  try {
    const response = await songRepository.getSongs(page, size, search, filter);
    return response as Collection<HALResource<Song>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch songs');
  }
});

export const fetchMoreSongsAsync = createAsyncThunk<
  Collection<HALResource<Song>>,
  { page: number; size?: number; search?: string; filter?: string },
  { rejectValue: string }
>('songs/fetchMoreSongs', async ({ page, size = 10, search, filter }, { rejectWithValue }) => {
  try {
    const response = await songRepository.getSongs(page, size, search, filter);
    return response as Collection<HALResource<Song>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch songs');
  }
});

export const fetchSongByIdAsync = createAsyncThunk<
  HALResource<Song>,
  number,
  { rejectValue: string }
>('songs/fetchSongById', async (songId, { rejectWithValue }) => {
  try {
    const song = await songRepository.getSongById(songId);
    return song as HALResource<Song>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch song');
  }
});

export const createSongAsync = createAsyncThunk<
  HALResource<Song>,
  CreateSongFormData,
  { rejectValue: string }
>('songs/createSong', async (songData, { rejectWithValue }) => {
  try {
    const song = await songRepository.createSong(songData as CreateSongFormData);
    return song as HALResource<Song>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to create song');
  }
});

export const updateSongAsync = createAsyncThunk<
  HALResource<Song>,
  { id: number; songData: EditSongFormData },
  { rejectValue: string }
>('songs/updateSong', async ({ id, songData }, { rejectWithValue }) => {
  try {
    const song = await songRepository.updateSong(id, songData);
    return song as HALResource<Song>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to update song');
  }
});

export const deleteSongAsync = createAsyncThunk<
  number,
  number,
  { rejectValue: string }
>('songs/deleteSong', async (songId, { rejectWithValue }) => {
  try {
    await songRepository.deleteSong(songId);
    return songId;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to delete song');
  }
});

export const fetchSongReviewsAsync = createAsyncThunk<
  Collection<HALResource<Review>>,
  { songId: number; page?: number; size?: number; filter?: string },
  { rejectValue: string }
>('songs/fetchSongReviews', async ({ songId, page = 1, size = 10, filter }, { rejectWithValue }) => {
  try {
    const response = await songRepository.getSongReviews(songId, page, size, filter);
    return response as Collection<HALResource<Review>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch song reviews');
  }
});

export const fetchMoreSongReviewsAsync = createAsyncThunk<
  Collection<HALResource<Review>>,
  { songId: number; page: number; size?: number; filter?: string },
  { rejectValue: string }
>('songs/fetchMoreSongReviews', async ({ songId, page, size = 10, filter }, { rejectWithValue }) => {
  try {
    const response = await songRepository.getSongReviews(songId, page, size, filter);
    return response as Collection<HALResource<Review>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch more song reviews');
  }
});

/**
 * Fetch the current user's review for a song
 */
export const fetchUserSongReviewAsync = createAsyncThunk<
  Review | null,
  { songId: number; userId: number },
  { rejectValue: string }
>('songs/fetchUserSongReview', async ({ songId, userId }, { rejectWithValue }) => {
  try {
    const review = await songRepository.getUserReviewForSong(songId, userId);
    return review;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch user song review');
  }
});

export const createSongReviewAsync = createAsyncThunk<
  HALResource<Review>,
  ReviewFormData,
  { rejectValue: string }
>('songs/createSongReview', async (reviewData, { rejectWithValue }) => {
  try {
    const reviewResponse = await reviewRepository.createReview(reviewData);
    return reviewResponse as HALResource<Review>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to create song review');
  }
});

export const addSongFavoriteAsync = createAsyncThunk<
  HALResource<Song>,
  number,
  { rejectValue: string }
>('songs/addFavorite', async (songId, { rejectWithValue }) => {
  try {
    await songRepository.addSongFavorite(songId);
    return await songRepository.getSongById(songId) as HALResource<Song>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to add song to favorites');
  }
});

export const removeSongFavoriteAsync = createAsyncThunk<
  HALResource<Song>,
  number,
  { rejectValue: string }
>('songs/removeFavorite', async (songId, { rejectWithValue }) => {
  try {
    await songRepository.removeSongFavorite(songId);
    return await songRepository.getSongById(songId) as HALResource<Song>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to remove song from favorites');
  }
});

// ============================================================================
// Slice
// ============================================================================

const songSlice = createSlice({
  name: 'songs',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearCurrentSong: (state) => {
      state.currentSong = null;
      state.songReviews = [];
      state.currentUserReview = null;
      state.reviewsPagination = { page: 1, size: 10, totalCount: 0, hasMore: true };
    },
    clearSongs: (state) => {
      state.songs = {};
      state.orderedSongsIds = [];
      state.pagination = { page: 1, size: 10, totalCount: 0, hasMore: true };
    },
    addSong: (state, action: PayloadAction<Song>) => {
      state.songs[action.payload.id] = action.payload;
      if (!state.orderedSongsIds.includes(action.payload.id)) {
        state.orderedSongsIds.push(action.payload.id);
      }
    },
    removeSong: (state, action: PayloadAction<number>) => {
      delete state.songs[action.payload];
      state.orderedSongsIds = state.orderedSongsIds.filter((id) => id !== action.payload);
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchSongsAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchSongsAsync.fulfilled, (state, action) => {
        state.loading = false;
        state.songs = {};
        state.orderedSongsIds = [];
        action.payload.items.forEach((song) => {
          state.songs[song.data.id] = song.data as Song;
          state.orderedSongsIds.push(song.data.id);
        });
        const hasMore = action.payload.currentPage * action.payload.pageSize < action.payload.totalCount;
        state.pagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
          hasMore,
        };
      })
      .addCase(fetchSongsAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to fetch songs';
      });

    builder
      .addCase(fetchMoreSongsAsync.pending, (state) => {
        state.loadingMore = true;
        state.error = null;
      })
      .addCase(fetchMoreSongsAsync.fulfilled, (state, action) => {
        state.loadingMore = false;
        action.payload.items.forEach((song) => {
          if (!state.songs[song.data.id]) {
            state.songs[song.data.id] = song.data as Song;
            state.orderedSongsIds.push(song.data.id);
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
      .addCase(fetchMoreSongsAsync.rejected, (state, action) => {
        state.loadingMore = false;
        state.error = action.payload || 'Failed to fetch more songs';
      });

    builder
      .addCase(fetchSongByIdAsync.pending, (state) => {
        state.loadingSong = true;
        state.error = null;
      })
      .addCase(fetchSongByIdAsync.fulfilled, (state, action) => {
        state.loadingSong = false;
        state.currentSong = action.payload.data as Song;
        if (!state.songs[action.payload.data.id]) {
          state.songs[action.payload.data.id] = action.payload.data as Song;
        }
      })
      .addCase(fetchSongByIdAsync.rejected, (state, action) => {
        state.loadingSong = false;
        state.error = action.payload || 'Failed to fetch song';
      });

    builder
      .addCase(createSongAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createSongAsync.fulfilled, (state, action) => {
        state.loading = false;
        if (!state.songs[action.payload.data.id]) {
          state.songs[action.payload.data.id] = action.payload.data as Song;
        }
      })
      .addCase(createSongAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to create song';
      });

    builder
      .addCase(updateSongAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateSongAsync.fulfilled, (state, action) => {
        state.loading = false;
        if (state.songs[action.payload.data.id]) {
          state.songs[action.payload.data.id] = action.payload.data as Song;
        }
        if (state.currentSong?.id === action.payload.data.id) {
          state.currentSong = action.payload.data as Song;
        }
      })
      .addCase(updateSongAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to update song';
      });

    builder
      .addCase(deleteSongAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteSongAsync.fulfilled, (state, action) => {
        state.loading = false;
        delete state.songs[action.meta.arg];
        if (state.currentSong?.id === action.meta.arg) {
          state.currentSong = null;
        }
      })
      .addCase(deleteSongAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to delete song';
      });

    builder
      .addCase(fetchSongReviewsAsync.pending, (state) => {
        state.loadingReviews = true;
        state.error = null;
      })
      .addCase(fetchSongReviewsAsync.fulfilled, (state, action) => {
        state.loadingReviews = false;
        state.songReviews = action.payload.items.map((review) => review.data as Review);
        const hasMore = action.payload.currentPage * action.payload.pageSize < action.payload.totalCount;
        state.reviewsPagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
          hasMore,
        };
      })
      .addCase(fetchSongReviewsAsync.rejected, (state, action) => {
        state.loadingReviews = false;
        state.error = action.payload || 'Failed to fetch song reviews';
      });

    builder
      .addCase(fetchMoreSongReviewsAsync.pending, (state) => {
        state.loadingMoreReviews = true;
        state.error = null;
      })
      .addCase(fetchMoreSongReviewsAsync.fulfilled, (state, action) => {
        state.loadingMoreReviews = false;
        const existingIds = new Set(state.songReviews.map(r => r.id));
        const newReviews = action.payload.items
          .map((review) => review.data as Review)
          .filter(r => !existingIds.has(r.id));
        state.songReviews = [...state.songReviews, ...newReviews];
        const hasMore = action.payload.currentPage * action.payload.pageSize < action.payload.totalCount;
        state.reviewsPagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
          hasMore,
        };
      })
      .addCase(fetchMoreSongReviewsAsync.rejected, (state, action) => {
        state.loadingMoreReviews = false;
        state.error = action.payload || 'Failed to fetch more song reviews';
      });

    // Fetch User's Song Review
    builder
      .addCase(fetchUserSongReviewAsync.fulfilled, (state, action) => {
        state.currentUserReview = action.payload;
      })
      .addCase(fetchUserSongReviewAsync.rejected, (state) => {
        state.currentUserReview = null;
      });

    builder
      .addCase(addSongFavoriteAsync.fulfilled, (state, action) => {
        const updatedSong = action.payload.data as Song;
        state.currentSong = updatedSong;
        state.songs[updatedSong.id] = updatedSong;
      })
      .addCase(removeSongFavoriteAsync.fulfilled, (state, action) => {
        const updatedSong = action.payload.data as Song;
        state.currentSong = updatedSong;
        state.songs[updatedSong.id] = updatedSong;
      });

    builder
      .addCase(blockReviewAsync.fulfilled, (state, action) => {
        const reviewData = action.payload.data as Review;
        const index = state.songReviews.findIndex((r) => r.id === reviewData.id);
        if (index !== -1) {
          state.songReviews[index] = reviewData;
        }
      })
      .addCase(unblockReviewAsync.fulfilled, (state, action) => {
        const reviewData = action.payload.data as Review;
        const index = state.songReviews.findIndex((r) => r.id === reviewData.id);
        if (index !== -1) {
          state.songReviews[index] = reviewData;
        }
      });

    // Like/Unlike Review - Update songReviews list
    builder
      .addCase(likeReviewAsync.fulfilled, (state, action) => {
        const reviewId = action.meta.arg;
        const index = state.songReviews.findIndex((r) => r.id === reviewId);
        if (index !== -1) {
          state.songReviews[index].likes = (state.songReviews[index].likes || 0) + 1;
          state.songReviews[index].liked = true;
        }
      })
      .addCase(unlikeReviewAsync.fulfilled, (state, action) => {
        const reviewId = action.meta.arg;
        const index = state.songReviews.findIndex((r) => r.id === reviewId);
        if (index !== -1) {
          state.songReviews[index].likes = Math.max(0, (state.songReviews[index].likes || 0) - 1);
          state.songReviews[index].liked = false;
        }
      });
  },
});

// ============================================================================
// Actions & Selectors
// ============================================================================

export const { clearError, clearCurrentSong, clearSongs, addSong, removeSong } = songSlice.actions;

export const selectSongs = (state: RootState) => state.songs.songs;
export const selectSongIds = (state: RootState) => state.songs.orderedSongsIds;
export const selectOrderedSongs = createSelector(
  [selectSongs, selectSongIds],
  (songs, ids) => ids.map((id) => songs[id]).filter(Boolean)
);
export const selectSongById = (songId: number) => (state: RootState) => state.songs.songs[songId] || null;
export const selectCurrentSong = (state: RootState) => state.songs.currentSong;
export const selectSongReviews = (state: RootState) => state.songs.songReviews;
export const selectSongPagination = (state: RootState) => state.songs.pagination;
export const selectSongReviewsPagination = (state: RootState) => state.songs.reviewsPagination;
export const selectSongLoading = (state: RootState) => state.songs.loading;
export const selectSongLoadingMore = (state: RootState) => state.songs.loadingMore;
export const selectSongError = (state: RootState) => state.songs.error;
export const selectLoadingSong = (state: RootState) => state.songs.loadingSong;
export const selectLoadingSongReviews = (state: RootState) => state.songs.loadingReviews;
export const selectLoadingMoreSongReviews = (state: RootState) => state.songs.loadingMoreReviews;
export const selectSongsHasMore = (state: RootState) => state.songs.pagination.hasMore;
export const selectSongReviewsHasMore = (state: RootState) => state.songs.reviewsPagination.hasMore;
export const selectCurrentUserSongReview = (state: RootState) => state.songs.currentUserReview;

export default songSlice.reducer;

