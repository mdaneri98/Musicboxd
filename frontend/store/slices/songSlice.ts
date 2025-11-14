/**
 * Song Slice
 * Redux slice for song state management
 */

import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { songRepository } from '@/repositories';
import { Song, Review, Collection, HALResource, CreateSongFormData, ReviewFormData } from '@/types';
import type { RootState } from '../index';
import { EditSongFormData } from '@/types/forms';

// ============================================================================
// State Interface
// ============================================================================

export interface SongState {
  // Songs by ID (normalized state)
  songs: Record<number, Song>;
  // Current song being viewed
  currentSong: Song | null;
  // Related data for current song
  songReviews: Review[];
  // Pagination info
  pagination: {
    page: number;
    size: number;
    totalCount: number;
  };
  // Loading states
  loading: boolean;
  loadingSong: boolean;
  loadingReviews: boolean;
  // Error state
  error: string | null;
}

// ============================================================================
// Initial State
// ============================================================================

const initialState: SongState = {
  songs: {},
  currentSong: null,
  songReviews: [],
  pagination: {
    page: 1,
    size: 20,
    totalCount: 0,
  },
  loading: false,
  loadingSong: false,
  loadingReviews: false,
  error: null,
};

// ============================================================================
// Async Thunks
// ============================================================================

export const fetchSongsAsync = createAsyncThunk<
  Collection<HALResource<Song>>,
  { page?: number; size?: number; search?: string; filter?: string },
  { rejectValue: string }
>('songs/fetchSongs', async ({ page = 1, size = 20, search, filter }, { rejectWithValue }) => {
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
>('songs/fetchSongReviews', async ({ songId, page = 1, size = 20, filter }, { rejectWithValue }) => {
  try {
    const response = await songRepository.getSongReviews(songId, page, size, filter);
    return response as Collection<HALResource<Review>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch song reviews');
  }
});

export const createSongReviewAsync = createAsyncThunk<
  HALResource<Review>,
  { songId: number; reviewData: Omit<ReviewFormData, 'itemId' | 'itemType'> },
  { rejectValue: string }
>('songs/createSongReview', async ({ songId, reviewData }, { rejectWithValue }) => {
  try {
    const response = await songRepository.createSongReview(songId, reviewData as any);
    // El repositorio retorna Review, pero necesitamos HALResource<Review>
    // Lo envolvemos con la estructura HAL
    return { data: response, _links: {} } as HALResource<Review>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to create song review');
  }
});

export const addSongFavoriteAsync = createAsyncThunk<
  number,
  number,
  { rejectValue: string }
>('songs/addFavorite', async (songId, { rejectWithValue }) => {
  try {
    await songRepository.addSongFavorite(songId);
    return songId;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to add song to favorites');
  }
});

export const removeSongFavoriteAsync = createAsyncThunk<
  number,
  number,
  { rejectValue: string }
>('songs/removeFavorite', async (songId, { rejectWithValue }) => {
  try {
    await songRepository.removeSongFavorite(songId);
    return songId;
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
    },
    addSong: (state, action: PayloadAction<Song>) => {
      state.songs[action.payload.id] = action.payload;
    },
    removeSong: (state, action: PayloadAction<number>) => {
      delete state.songs[action.payload];
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
        action.payload.items.forEach((song) => {
          state.songs[song.data.id] = song.data as Song;
        });
        state.pagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
        };
      })
      .addCase(fetchSongsAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to fetch songs';
      });

    builder
      .addCase(fetchSongByIdAsync.pending, (state) => {
        state.loadingSong = true;
        state.error = null;
      })
      .addCase(fetchSongByIdAsync.fulfilled, (state, action) => {
        state.loadingSong = false;
        state.currentSong = action.payload.data as Song;
        state.songs[action.payload.data.id] = action.payload.data as Song;
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
        state.songs[action.payload.data.id] = action.payload.data as Song;
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
        state.songs[action.payload.data.id] = action.payload.data as Song;
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
        // Sobrescribir el array completo en lugar de acumular
        state.songReviews = action.payload.items.map((review) => review.data as Review);
      })
      .addCase(fetchSongReviewsAsync.rejected, (state, action) => {
        state.loadingReviews = false;
        state.error = action.payload || 'Failed to fetch song reviews';
      });

    builder
      .addCase(addSongFavoriteAsync.rejected, (state, action) => {
        state.error = action.payload || 'Failed to add song to favorites';
      })
      .addCase(removeSongFavoriteAsync.rejected, (state, action) => {
        state.error = action.payload || 'Failed to remove song from favorites';
      });
  },
});

// ============================================================================
// Actions & Selectors
// ============================================================================

export const { clearError, clearCurrentSong, addSong, removeSong } = songSlice.actions;

export const selectSongs = (state: RootState) => state.songs.songs;
export const selectSongById = (songId: number) => (state: RootState) =>
  state.songs.songs[songId] || null;
export const selectCurrentSong = (state: RootState) => state.songs.currentSong;
export const selectSongReviews = (state: RootState) => state.songs.songReviews;
export const selectSongPagination = (state: RootState) => state.songs.pagination;
export const selectSongLoading = (state: RootState) => state.songs.loading;
export const selectSongError = (state: RootState) => state.songs.error;
export const selectLoadingSong = (state: RootState) => state.songs.loadingSong;
export const selectLoadingSongReviews = (state: RootState) => state.songs.loadingReviews;

export default songSlice.reducer;

