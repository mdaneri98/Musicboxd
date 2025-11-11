// /**
//  * Search Slice
//  * Redux slice for global search state management
//  */

// import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
// import { userRepository, artistRepository, albumRepository, songRepository, reviewRepository } from '@/repositories';
// import { User, Artist, Album, Song, Review, FilterType } from '@/types';
// import type { RootState } from '../index';

// // ============================================================================
// // State Interface
// // ============================================================================

// export interface SearchResults {
//   users: User[];
//   artists: Artist[];
//   albums: Album[];
//   songs: Song[];
//   reviews: Review[];
// }

// export interface SearchState {
//   // Search query
//   query: string;
//   // Search results
//   results: SearchResults;
//   // Active filters
//   filter: FilterType;
//   // Loading state
//   isSearching: boolean;
//   // Error state
//   error: string | null;
//   // Recent searches
//   recentSearches: string[];
// }

// // ============================================================================
// // Initial State
// // ============================================================================

// const initialState: SearchState = {
//   query: '',
//   results: {
//     users: [],
//     artists: [],
//     albums: [],
//     songs: [],
//     reviews: [],
//   },
//   filters: {
//     type: 'all',
//   },
//   isSearching: false,
//   error: null,
//   recentSearches: [],
// };

// // ============================================================================
// // Async Thunks
// // ============================================================================

// /**
//  * Perform multi-entity search
//  */
// export const searchAsync = createAsyncThunk<
//   SearchResults,
//   { query: string; filter?: FilterType },
//   { rejectValue: string }
// >('search/search', async ({ query, filter = 'all' }, { rejectWithValue }) => {
//   try {
//     const results: SearchResults = {
//       users: [],
//       artists: [],
//       albums: [],
//       songs: [],
//       reviews: [],
//     };

//     // Execute searches based on type filter
//     const searchPromises: Promise<void>[] = [];

//     if (filter === 'all' || filter === 'users') {
//       searchPromises.push(
//         userRepository.getUsers(0, 10, query).then((response) => {
//           results.users = response.items;
//         })
//       );
//     }

//     if (filter === 'all' || filter === 'artists') {
//       searchPromises.push(
//         artistRepository.getArtists(0, 10, query).then((response) => {
//           results.artists = response.items;
//         })
//       );
//     }

//     if (type === 'all' || type === 'albums') {
//       searchPromises.push(
//         albumRepository.getAlbums(0, 10, query).then((response) => {
//           results.albums = response.items;
//         })
//       );
//     }

//     if (type === 'all' || type === 'songs') {
//       searchPromises.push(
//         songRepository.getSongs(0, 10, query).then((response) => {
//           results.songs = response.items;
//         })
//       );
//     }

//     if (type === 'all' || type === 'reviews') {
//       searchPromises.push(
//         reviewRepository.getReviews(0, 10, query).then((response) => {
//           results.reviews = response.items;
//         })
//       );
//     }

//     // Wait for all searches to complete
//     await Promise.all(searchPromises);

//     return results;
//   } catch (error: any) {
//     return rejectWithValue(error.message || 'Search failed');
//   }
// });

// /**
//  * Search users
//  */
// export const searchUsersAsync = createAsyncThunk<
//   User[],
//   string,
//   { rejectValue: string }
// >('search/searchUsers', async (query, { rejectWithValue }) => {
//   try {
//     const response = await userRepository.getUsers(0, 10, query);
//     return response.items;
//   } catch (error: any) {
//     return rejectWithValue(error.message || 'Failed to search users');
//   }
// });

// /**
//  * Search artists
//  */
// export const searchArtistsAsync = createAsyncThunk<
//   Artist[],
//   string,
//   { rejectValue: string }
// >('search/searchArtists', async (query, { rejectWithValue }) => {
//   try {
//     const response = await artistRepository.getArtists(0, 10, query);
//     return response.items;
//   } catch (error: any) {
//     return rejectWithValue(error.message || 'Failed to search artists');
//   }
// });

// /**
//  * Search albums
//  */
// export const searchAlbumsAsync = createAsyncThunk<
//   Album[],
//   string,
//   { rejectValue: string }
// >('search/searchAlbums', async (query, { rejectWithValue }) => {
//   try {
//     const response = await albumRepository.getAlbums(0, 10, query);
//     return response.items;
//   } catch (error: any) {
//     return rejectWithValue(error.message || 'Failed to search albums');
//   }
// });

// /**
//  * Search songs
//  */
// export const searchSongsAsync = createAsyncThunk<
//   Song[],
//   string,
//   { rejectValue: string }
// >('search/searchSongs', async (query, { rejectWithValue }) => {
//   try {
//     const response = await songRepository.getSongs(0, 10, query);
//     return response.items;
//   } catch (error: any) {
//     return rejectWithValue(error.message || 'Failed to search songs');
//   }
// });

// /**
//  * Search reviews
//  */
// export const searchReviewsAsync = createAsyncThunk<
//   Review[],
//   string,
//   { rejectValue: string }
// >('search/searchReviews', async (query, { rejectWithValue }) => {
//   try {
//     const response = await reviewRepository.getReviews(0, 10, query);
//     return response.items;
//   } catch (error: any) {
//     return rejectWithValue(error.message || 'Failed to search reviews');
//   }
// });

// // ============================================================================
// // Slice
// // ============================================================================

// const searchSlice = createSlice({
//   name: 'search',
//   initialState,
//   reducers: {
//     /**
//      * Set search query
//      */
//     setQuery: (state, action: PayloadAction<string>) => {
//       state.query = action.payload;
//     },

//     /**
//      * Set search filters
//      */
//     setFilters: (state, action: PayloadAction<SearchFilters>) => {
//       state.filters = { ...state.filters, ...action.payload };
//     },

//     /**
//      * Clear search
//      */
//     clearSearch: (state) => {
//       state.query = '';
//       state.results = {
//         users: [],
//         artists: [],
//         albums: [],
//         songs: [],
//         reviews: [],
//       };
//       state.filters = {
//         type: 'all',
//       };
//       state.error = null;
//     },

//     /**
//      * Clear error state
//      */
//     clearError: (state) => {
//       state.error = null;
//     },

//     /**
//      * Add to recent searches
//      */
//     addRecentSearch: (state, action: PayloadAction<string>) => {
//       const query = action.payload.trim();
//       if (query) {
//         // Remove duplicate if exists
//         state.recentSearches = state.recentSearches.filter((s) => s !== query);
//         // Add to beginning
//         state.recentSearches = [query, ...state.recentSearches];
//         // Keep only last 10
//         state.recentSearches = state.recentSearches.slice(0, 10);
//       }
//     },

//     /**
//      * Clear recent searches
//      */
//     clearRecentSearches: (state) => {
//       state.recentSearches = [];
//     },
//   },
//   extraReducers: (builder) => {
//     // Search (multi-entity)
//     builder
//       .addCase(searchAsync.pending, (state) => {
//         state.isSearching = true;
//         state.error = null;
//       })
//       .addCase(searchAsync.fulfilled, (state, action) => {
//         state.isSearching = false;
//         state.results = action.payload;
//         // Add to recent searches
//         if (state.query) {
//           const query = state.query.trim();
//           state.recentSearches = state.recentSearches.filter((s) => s !== query);
//           state.recentSearches = [query, ...state.recentSearches].slice(0, 10);
//         }
//       })
//       .addCase(searchAsync.rejected, (state, action) => {
//         state.isSearching = false;
//         state.error = action.payload || 'Search failed';
//       });

//     // Search Users
//     builder
//       .addCase(searchUsersAsync.pending, (state) => {
//         state.isSearching = true;
//         state.error = null;
//       })
//       .addCase(searchUsersAsync.fulfilled, (state, action) => {
//         state.isSearching = false;
//         state.results.users = action.payload;
//       })
//       .addCase(searchUsersAsync.rejected, (state, action) => {
//         state.isSearching = false;
//         state.error = action.payload || 'Failed to search users';
//       });

//     // Search Artists
//     builder
//       .addCase(searchArtistsAsync.pending, (state) => {
//         state.isSearching = true;
//         state.error = null;
//       })
//       .addCase(searchArtistsAsync.fulfilled, (state, action) => {
//         state.isSearching = false;
//         state.results.artists = action.payload;
//       })
//       .addCase(searchArtistsAsync.rejected, (state, action) => {
//         state.isSearching = false;
//         state.error = action.payload || 'Failed to search artists';
//       });

//     // Search Albums
//     builder
//       .addCase(searchAlbumsAsync.pending, (state) => {
//         state.isSearching = true;
//         state.error = null;
//       })
//       .addCase(searchAlbumsAsync.fulfilled, (state, action) => {
//         state.isSearching = false;
//         state.results.albums = action.payload;
//       })
//       .addCase(searchAlbumsAsync.rejected, (state, action) => {
//         state.isSearching = false;
//         state.error = action.payload || 'Failed to search albums';
//       });

//     // Search Songs
//     builder
//       .addCase(searchSongsAsync.pending, (state) => {
//         state.isSearching = true;
//         state.error = null;
//       })
//       .addCase(searchSongsAsync.fulfilled, (state, action) => {
//         state.isSearching = false;
//         state.results.songs = action.payload;
//       })
//       .addCase(searchSongsAsync.rejected, (state, action) => {
//         state.isSearching = false;
//         state.error = action.payload || 'Failed to search songs';
//       });

//     // Search Reviews
//     builder
//       .addCase(searchReviewsAsync.pending, (state) => {
//         state.isSearching = true;
//         state.error = null;
//       })
//       .addCase(searchReviewsAsync.fulfilled, (state, action) => {
//         state.isSearching = false;
//         state.results.reviews = action.payload;
//       })
//       .addCase(searchReviewsAsync.rejected, (state, action) => {
//         state.isSearching = false;
//         state.error = action.payload || 'Failed to search reviews';
//       });
//   },
// });

// // ============================================================================
// // Actions & Selectors
// // ============================================================================

// export const { setQuery, setFilters, clearSearch, clearError, addRecentSearch, clearRecentSearches } =
//   searchSlice.actions;

// // Selectors
// export const selectSearchQuery = (state: RootState) => state.search.query;
// export const selectSearchResults = (state: RootState) => state.search.results;
// export const selectSearchFilters = (state: RootState) => state.search.filters;
// export const selectIsSearching = (state: RootState) => state.search.isSearching;
// export const selectSearchError = (state: RootState) => state.search.error;
// export const selectRecentSearches = (state: RootState) => state.search.recentSearches;
// export const selectHasResults = (state: RootState) =>
//   state.search.results.users.length > 0 ||
//   state.search.results.artists.length > 0 ||
//   state.search.results.albums.length > 0 ||
//   state.search.results.songs.length > 0 ||
//   state.search.results.reviews.length > 0;

// export default searchSlice.reducer;

