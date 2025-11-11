/**
 * User Slice
 * Redux slice for user state management
 */

import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { userRepository } from '@/repositories';
import { User, Artist, Album, Song, Review, Collection, HALResource } from '@/types';
import type { RootState } from '../index';

// ============================================================================
// State Interface
// ============================================================================

export interface UserState {
  // Users by ID (normalized state)
  users: Record<number, User>;
  // Current profile being viewed
  currentProfile: User | null;
  // Followers and following lists
  followers: User[];
  following: User[];
  // Favorites
  favoriteArtists: Artist[];
  favoriteAlbums: Album[];
  favoriteSongs: Song[];
  // User reviews
  userReviews: Review[];
  // Pagination info
  pagination: {
    page: number;
    size: number;
    totalCount: number;
  };
  // Loading states
  loading: boolean;
  loadingProfile: boolean;
  loadingFollowers: boolean;
  loadingFollowing: boolean;
  loadingFavorites: boolean;
  loadingReviews: boolean;
  // Error state
  error: string | null;
}

// ============================================================================
// Initial State
// ============================================================================

const initialState: UserState = {
  users: {},
  currentProfile: null,
  followers: [],
  following: [],
  favoriteArtists: [],
  favoriteAlbums: [],
  favoriteSongs: [],
  userReviews: [],
  pagination: {
    page: 0,
    size: 20,
    totalCount: 0,
  },
  loading: false,
  loadingProfile: false,
  loadingFollowers: false,
  loadingFollowing: false,
  loadingFavorites: false,
  loadingReviews: false,
  error: null,
};

// ============================================================================
// Async Thunks
// ============================================================================

/**
 * Fetch paginated users list
 */
export const fetchUsersAsync = createAsyncThunk<
  Collection<HALResource<User>>,
  { page?: number; size?: number; search?: string; filter?: string },
  { rejectValue: string }
>('users/fetchUsers', async ({ page = 0, size = 20, search, filter }, { rejectWithValue }) => {
  try {
    const response = await userRepository.getUsers(page, size, search, filter);
    return response as Collection<HALResource<User>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch users');
  }
});

/**
 * Fetch user by ID
 */
export const fetchUserByIdAsync = createAsyncThunk<
  HALResource<User>,
  number,
  { rejectValue: string }
>('users/fetchUserById', async (userId, { rejectWithValue }) => {
  try {
    const user = await userRepository.getUserById(userId);
    return user as HALResource<User>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch user');
  }
});

/**
 * Update user profile
 */
export const updateUserAsync = createAsyncThunk<
  HALResource<User>,
  { id: number; userData: Partial<User> },
  { rejectValue: string }
>('users/updateUser', async ({ id, userData }, { rejectWithValue }) => {
  try {
    const user = await userRepository.updateUser(id, userData);
    return user as HALResource<User>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to update user');
  }
});

/**
 * Delete user
 */
export const deleteUserAsync = createAsyncThunk<
  number,
  number,
  { rejectValue: string }
>('users/deleteUser', async (userId, { rejectWithValue }) => {
  try {
    await userRepository.deleteUser(userId);
    return userId;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to delete user');
  }
});

/**
 * Fetch user's followers
 */
export const fetchFollowersAsync = createAsyncThunk<
  Collection<HALResource<User>>,
  { userId: number; page?: number; size?: number },
  { rejectValue: string }
>('users/fetchFollowers', async ({ userId, page = 0, size = 20 }, { rejectWithValue }) => {
  try {
    const response = await userRepository.getFollowers(userId, page, size);
    return response as Collection<HALResource<User>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch followers');
  }
});

/**
 * Fetch users that this user is following
 */
export const fetchFollowingAsync = createAsyncThunk<
  Collection<HALResource<User>>,
  { userId: number; page?: number; size?: number },
  { rejectValue: string }
>('users/fetchFollowing', async ({ userId, page = 0, size = 20 }, { rejectWithValue }) => {
  try {
    const response = await userRepository.getFollowing(userId, page, size);
    return response as Collection<HALResource<User>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch following');
  }
});

/**
 * Follow a user
 */
export const followUserAsync = createAsyncThunk<
  number,
  number,
  { rejectValue: string }
>('users/followUser', async (userId, { rejectWithValue }) => {
  try {
    await userRepository.followUser(userId);
    return userId;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to follow user');
  }
});

/**
 * Unfollow a user
 */
export const unfollowUserAsync = createAsyncThunk<
  number,
  number,
  { rejectValue: string }
>('users/unfollowUser', async (userId, { rejectWithValue }) => {
  try {
    await userRepository.unfollowUser(userId);
    return userId;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to unfollow user');
  }
});

/**
 * Fetch user's favorite artists
 */
export const fetchFavoriteArtistsAsync = createAsyncThunk<
  Collection<HALResource<Artist>>,
  number,
  { rejectValue: string }
>('users/fetchFavoriteArtists', async (userId, { rejectWithValue }) => {
  try {
    const artists = await userRepository.getFavoriteArtists(userId);
    return artists as Collection<HALResource<Artist>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch favorite artists');
  }
});

/**
 * Fetch user's favorite albums
 */
export const fetchFavoriteAlbumsAsync = createAsyncThunk<
  Collection<HALResource<Album>>,
  number,
  { rejectValue: string }
>('users/fetchFavoriteAlbums', async (userId, { rejectWithValue }) => {
  try {
    const albums = await userRepository.getFavoriteAlbums(userId);
    return albums as Collection<HALResource<Album>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch favorite albums');
  }
});

/**
 * Fetch user's favorite songs
 */
export const fetchFavoriteSongsAsync = createAsyncThunk<
  Collection<HALResource<Song>>,
  number,
  { rejectValue: string }
>('users/fetchFavoriteSongs', async (userId, { rejectWithValue }) => {
  try {
    const songs = await userRepository.getFavoriteSongs(userId);
    return songs as Collection<HALResource<Song>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch favorite songs');
  }
});

/**
 * Fetch user's reviews
 */
export const fetchUserReviewsAsync = createAsyncThunk<
  Collection<HALResource<Review>>,
  { userId: number; page?: number; size?: number; filter?: string },
  { rejectValue: string }
>('users/fetchUserReviews', async ({ userId, page = 0, size = 20, filter }, { rejectWithValue }) => {
  try {
    const response = await userRepository.getUserReviews(userId, page, size, filter);
    return response as Collection<HALResource<Review>>;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Failed to fetch user reviews');
  }
});

// ============================================================================
// Slice
// ============================================================================

const userSlice = createSlice({
  name: 'users',
  initialState,
  reducers: {
    /**
     * Clear error state
     */
    clearError: (state) => {
      state.error = null;
    },

    /**
     * Clear current profile
     */
    clearCurrentProfile: (state) => {
      state.currentProfile = null;
      state.followers = [];
      state.following = [];
      state.favoriteArtists = [];
      state.favoriteAlbums = [];
      state.favoriteSongs = [];
      state.userReviews = [];
    },

    /**
     * Add user to normalized state
     */
    addUser: (state, action: PayloadAction<User>) => {
      state.users[action.payload.id] = action.payload;
    },

    /**
     * Remove user from normalized state
     */
    removeUser: (state, action: PayloadAction<number>) => {
      delete state.users[action.payload];
    },
  },
  extraReducers: (builder) => {
    // Fetch Users
    builder
      .addCase(fetchUsersAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchUsersAsync.fulfilled, (state, action) => {
        state.loading = false;
        // Add users to normalized state
        action.payload.items.forEach((user) => {
          state.users[user.data.id] = user.data as User;
        });
        state.pagination = {
          page: action.payload.currentPage,
          size: action.payload.pageSize,
          totalCount: action.payload.totalCount,
        };
      })
      .addCase(fetchUsersAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to fetch users';
      });

    // Fetch User By ID
    builder
      .addCase(fetchUserByIdAsync.pending, (state) => {
        state.loadingProfile = true;
        state.error = null;
      })
      .addCase(fetchUserByIdAsync.fulfilled, (state, action) => {
        state.loadingProfile = false;
        state.currentProfile = action.payload.data as User;
        state.users[action.payload.data.id] = action.payload.data as User;
      })
      .addCase(fetchUserByIdAsync.rejected, (state, action) => {
        state.loadingProfile = false;
        state.error = action.payload || 'Failed to fetch user';
      });

    // Update User
    builder
      .addCase(updateUserAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateUserAsync.fulfilled, (state, action) => {
        state.loading = false;
        state.users[action.payload.data.id] = action.payload.data as User;
        if (state.currentProfile?.id === action.payload.data.id) {
          state.currentProfile = action.payload.data as User;
        }
      })
      .addCase(updateUserAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to update user';
      });

    // Delete User
    builder
      .addCase(deleteUserAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteUserAsync.fulfilled, (state, action) => {
        state.loading = false;
        delete state.users[action.meta.arg];
        if (state.currentProfile?.id === action.meta.arg) {
          state.currentProfile = null;
        }
      })
      .addCase(deleteUserAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to delete user';
      });

    // Fetch Followers
    builder
      .addCase(fetchFollowersAsync.pending, (state) => {
        state.loadingFollowers = true;
        state.error = null;
      })
      .addCase(fetchFollowersAsync.fulfilled, (state, action) => {
        state.loadingFollowers = false;
        action.payload.items.forEach((user) => {
          state.followers.push(user.data as User);
        });
        // Add to normalized state
        action.payload.items.forEach((user) => {
          state.users[user.data.id] = user.data as User;
        });
      })
      .addCase(fetchFollowersAsync.rejected, (state, action) => {
        state.loadingFollowers = false;
        state.error = action.payload || 'Failed to fetch followers';
      });

    // Fetch Following
    builder
      .addCase(fetchFollowingAsync.pending, (state) => {
        state.loadingFollowing = true;
        state.error = null;
      })
      .addCase(fetchFollowingAsync.fulfilled, (state, action) => {
        state.loadingFollowing = false;
        action.payload.items.forEach((user) => {
          state.following.push(user.data as User);
        });
        // Add to normalized state
        action.payload.items.forEach((user) => {
          state.users[user.data.id] = user.data as User;
        });
      })
      .addCase(fetchFollowingAsync.rejected, (state, action) => {
        state.loadingFollowing = false;
        state.error = action.payload || 'Failed to fetch following';
      });

    // Follow User
    builder
      .addCase(followUserAsync.fulfilled, (state, action) => {
        // Update follower count in current profile if it's the followed user
        if (state.currentProfile?.id === action.payload) {
          state.currentProfile.followersAmount += 1;
        }
        // Update in normalized state
        if (state.users[action.payload]) {
          state.users[action.payload].followersAmount += 1;
        }
      })
      .addCase(followUserAsync.rejected, (state, action) => {
        state.error = action.payload || 'Failed to follow user';
      });

    // Unfollow User
    builder
      .addCase(unfollowUserAsync.fulfilled, (state, action) => {
        // Update follower count in current profile if it's the unfollowed user
        if (state.currentProfile?.id === action.payload) {
          state.currentProfile.followersAmount -= 1;
        }
        // Update in normalized state
        if (state.users[action.payload]) {
          state.users[action.payload].followersAmount -= 1;
        }
      })
      .addCase(unfollowUserAsync.rejected, (state, action) => {
        state.error = action.payload || 'Failed to unfollow user';
      });

    // Fetch Favorite Artists
    builder
      .addCase(fetchFavoriteArtistsAsync.pending, (state) => {
        state.loadingFavorites = true;
        state.error = null;
      })
      .addCase(fetchFavoriteArtistsAsync.fulfilled, (state, action) => {
        state.loadingFavorites = false;
        action.payload.items.forEach((artist) => {
          state.favoriteArtists.push(artist.data as Artist);
        });
        // Add to normalized state
        action.payload.items.forEach((artist) => {
          state.favoriteArtists[artist.data.id] = artist.data as Artist;
        });
      })
      .addCase(fetchFavoriteArtistsAsync.rejected, (state, action) => {
        state.loadingFavorites = false;
        state.error = action.payload || 'Failed to fetch favorite artists';
      });

    // Fetch Favorite Albums
    builder
      .addCase(fetchFavoriteAlbumsAsync.pending, (state) => {
        state.loadingFavorites = true;
        state.error = null;
      })
      .addCase(fetchFavoriteAlbumsAsync.fulfilled, (state, action) => {
        state.loadingFavorites = false;
        action.payload.items.forEach((album) => {
          state.favoriteAlbums.push(album.data as Album);
        });
        // Add to normalized state
        action.payload.items.forEach((album) => {
          state.favoriteAlbums[album.data.id] = album.data as Album;
        });
      })
      .addCase(fetchFavoriteAlbumsAsync.rejected, (state, action) => {
        state.loadingFavorites = false;
        state.error = action.payload || 'Failed to fetch favorite albums';
      });

    // Fetch Favorite Songs
    builder
      .addCase(fetchFavoriteSongsAsync.pending, (state) => {
        state.loadingFavorites = true;
        state.error = null;
      })
      .addCase(fetchFavoriteSongsAsync.fulfilled, (state, action) => {
        state.loadingFavorites = false;
        action.payload.items.forEach((song) => {
          state.favoriteSongs.push(song.data as Song);
        });
        // Add to normalized state
        action.payload.items.forEach((song) => {
          state.favoriteSongs[song.data.id] = song.data as Song;
        });
      })
      .addCase(fetchFavoriteSongsAsync.rejected, (state, action) => {
        state.loadingFavorites = false;
        state.error = action.payload || 'Failed to fetch favorite songs';
      });

    // Fetch User Reviews
    builder
      .addCase(fetchUserReviewsAsync.pending, (state) => {
        state.loadingReviews = true;
        state.error = null;
      })
      .addCase(fetchUserReviewsAsync.fulfilled, (state, action) => {
        state.loadingReviews = false;
        action.payload.items.forEach((review) => {
          state.userReviews[review.data.id] = review.data as Review;
        });
        // Add to normalized state
        action.payload.items.forEach((review) => {
          state.userReviews[review.data.id] = review.data as Review;
        });
      })
      .addCase(fetchUserReviewsAsync.rejected, (state, action) => {
        state.loadingReviews = false;
        state.error = action.payload || 'Failed to fetch user reviews';
      });
  },
});

// ============================================================================
// Actions
// ============================================================================

export const { clearError, clearCurrentProfile, addUser, removeUser } = userSlice.actions;

// ============================================================================
// Selectors
// ============================================================================

export const selectUsers = (state: RootState) => state.users.users;
export const selectUserById = (userId: number) => (state: RootState) =>
  state.users.users[userId] || null;
export const selectCurrentProfile = (state: RootState) => state.users.currentProfile;
export const selectFollowers = (state: RootState) => state.users.followers;
export const selectFollowing = (state: RootState) => state.users.following;
export const selectFavoriteArtists = (state: RootState) => state.users.favoriteArtists;
export const selectFavoriteAlbums = (state: RootState) => state.users.favoriteAlbums;
export const selectFavoriteSongs = (state: RootState) => state.users.favoriteSongs;
export const selectUserReviews = (state: RootState) => state.users.userReviews;
export const selectUserPagination = (state: RootState) => state.users.pagination;
export const selectUserLoading = (state: RootState) => state.users.loading;
export const selectUserError = (state: RootState) => state.users.error;
export const selectLoadingProfile = (state: RootState) => state.users.loadingProfile;
export const selectLoadingFollowers = (state: RootState) => state.users.loadingFollowers;
export const selectLoadingFollowing = (state: RootState) => state.users.loadingFollowing;
export const selectLoadingFavorites = (state: RootState) => state.users.loadingFavorites;
export const selectLoadingReviews = (state: RootState) => state.users.loadingReviews;

// ============================================================================
// Reducer Export
// ============================================================================

export default userSlice.reducer;

