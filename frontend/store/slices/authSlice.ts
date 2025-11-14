/**
 * Auth Slice
 * Redux slice for authentication state management
 */

import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { authRepository } from '@/repositories';
import { User, LoginResponse, RegisterFormData } from '@/types';
import type { RootState } from '../index';

// ============================================================================
// State Interface
// ============================================================================

export interface AuthState {
  currentUser: User | null;
  isAuthenticated: boolean;
  isModerator: boolean;
  loading: boolean;
  error: string | null;
  initializing: boolean; // For app initialization
}

// ============================================================================
// Initial State
// ============================================================================

const initialState: AuthState = {
  currentUser: null,
  isAuthenticated: false,
  isModerator: false,
  loading: false,
  error: null,
  initializing: true,
};

// ============================================================================
// Async Thunks
// ============================================================================

/**
 * Login user with credentials
 */
export const loginAsync = createAsyncThunk<
  LoginResponse,
  { username: string; password: string },
  { rejectValue: string }
>('auth/login', async (credentials, { rejectWithValue }) => {
  try {
    const response = await authRepository.login(credentials.username, credentials.password);
    return response;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Login failed');
  }
});

/**
 * Register a new user
 */
export const registerAsync = createAsyncThunk<
  User,
  RegisterFormData,
  { rejectValue: string }
>('auth/register', async (registerData, { rejectWithValue }) => {
  try {
    const response = await authRepository.register(registerData);
    return response.data;
  } catch (error: any) {
    return rejectWithValue(error.message || 'Registration failed');
  }
});

/**
 * Logout current user
 */
export const logoutAsync = createAsyncThunk<void, void, { rejectValue: string }>(
  'auth/logout',
  async (_, { rejectWithValue }) => {
    try {
      await authRepository.logout();
    } catch (error: any) {
      // Even if logout fails on server, we clear local state
      return rejectWithValue(error.message || 'Logout failed');
    }
  }
);

/**
 * Get current authenticated user
 */
export const getCurrentUserAsync = createAsyncThunk<User, void, { rejectValue: string }>(
  'auth/getCurrentUser',
  async (_, { rejectWithValue }) => {
    try {
      const response = await authRepository.getCurrentUser();
      return response.data as User;
    } catch (error: any) {
      return rejectWithValue(error.message || 'Failed to get current user');
    }
  }
);

/**
 * Check authentication status on app initialization
 */
export const checkAuthAsync = createAsyncThunk<User | null, void, { rejectValue: string }>(
  'auth/checkAuth',
  async (_, { rejectWithValue }) => {
    try {
      // Check if user has valid access token
      if (!authRepository.isAuthenticated()) {
        return null;
      }

      // Try to get current user to verify token is still valid
      const response = await authRepository.getCurrentUser();
      if (!response.data) {
        return rejectWithValue('Invalid user response');
      }
      return response.data as User;
    } catch (error: any) {
      // Token is invalid, clear auth state
      authRepository.clearAuth();
      return rejectWithValue('Failed to get current user');
    }
  }
);

/**
 * Refresh access token
 */
export const refreshTokenAsync = createAsyncThunk<void, void, { rejectValue: string }>(
  'auth/refreshToken',
  async (_, { rejectWithValue }) => {
    try {
      const refreshToken = authRepository.getRefreshToken();
      if (!refreshToken) {
        throw new Error('No refresh token available');
      }
      await authRepository.refresh(refreshToken);
    } catch (error: any) {
      return rejectWithValue(error.message || 'Token refresh failed');
    }
  }
);

// ============================================================================
// Slice
// ============================================================================

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    /**
     * Clear error state
     */
    clearError: (state) => {
      state.error = null;
    },

    /**
     * Set current user (for manual updates)
     */
    setCurrentUser: (state, action: PayloadAction<User>) => {
      state.currentUser = action.payload;
      state.isAuthenticated = true;
      state.isModerator = action.payload.moderator;
    },

    /**
     * Clear auth state (used for forced logout)
     */
    clearAuth: (state) => {
      state.currentUser = null;
      state.isAuthenticated = false;
      state.isModerator = false;
      state.error = null;
      authRepository.clearAuth();
    },
  },
  extraReducers: (builder) => {
    // Login
    builder
      .addCase(loginAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(loginAsync.fulfilled, (state, action) => {
        state.loading = false;
        state.currentUser = action.payload.user as User;
        state.isAuthenticated = true;
        state.isModerator = action.payload.user.moderator;
        state.error = null;
      })
      .addCase(loginAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Login failed';
        state.isAuthenticated = false;
        state.currentUser = null;
        state.isModerator = false;
      });

    // Register
    builder
      .addCase(registerAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(registerAsync.fulfilled, (state) => {
        state.loading = false;
        state.error = null;
        // After registration, user needs to login
      })
      .addCase(registerAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Registration failed';
      });

    // Logout
    builder
      .addCase(logoutAsync.pending, (state) => {
        state.loading = true;
      })
      .addCase(logoutAsync.fulfilled, (state) => {
        state.loading = false;
        state.currentUser = null;
        state.isAuthenticated = false;
        state.isModerator = false;
        state.error = null;
      })
      .addCase(logoutAsync.rejected, (state) => {
        // Even on error, clear local state
        state.loading = false;
        state.currentUser = null;
        state.isAuthenticated = false;
        state.isModerator = false;
      });

    // Get Current User
    builder
      .addCase(getCurrentUserAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getCurrentUserAsync.fulfilled, (state, action) => {
        state.loading = false;
        state.currentUser = action.payload;
        state.isAuthenticated = true;
        state.isModerator = action.payload.moderator;
        state.error = null;
      })
      .addCase(getCurrentUserAsync.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to get current user';
        state.isAuthenticated = false;
        state.currentUser = null;
        state.isModerator = false;
      });

    // Check Auth (on app init)
    builder
      .addCase(checkAuthAsync.pending, (state) => {
        state.initializing = true;
      })
      .addCase(checkAuthAsync.fulfilled, (state, action) => {
        state.initializing = false;
        if (action.payload) {
          state.currentUser = action.payload;
          state.isAuthenticated = true;
          state.isModerator = action.payload.moderator;
        } else {
          state.currentUser = null;
          state.isAuthenticated = false;
          state.isModerator = false;
        }
      })
      .addCase(checkAuthAsync.rejected, (state) => {
        state.initializing = false;
        state.currentUser = null;
        state.isAuthenticated = false;
        state.isModerator = false;
      });

    // Refresh Token
    builder
      .addCase(refreshTokenAsync.pending, (state) => {
        state.error = null;
      })
      .addCase(refreshTokenAsync.fulfilled, (state) => {
        state.error = null;
      })
      .addCase(refreshTokenAsync.rejected, (state, action) => {
        state.error = action.payload || 'Token refresh failed';
        state.isAuthenticated = false;
        state.currentUser = null;
        state.isModerator = false;
      });
  },
});

// ============================================================================
// Actions
// ============================================================================

export const { clearError, setCurrentUser, clearAuth } = authSlice.actions;

// ============================================================================
// Selectors
// ============================================================================

export const selectCurrentUser = (state: RootState) => state.auth.currentUser;
export const selectIsAuthenticated = (state: RootState) => state.auth.isAuthenticated;
export const selectIsModerator = (state: RootState) => state.auth.isModerator;
export const selectAuthLoading = (state: RootState) => state.auth.loading;
export const selectAuthError = (state: RootState) => state.auth.error;
export const selectAuthInitializing = (state: RootState) => state.auth.initializing;

// Computed selectors
export const selectUserId = (state: RootState) => state.auth.currentUser?.id ?? null;
export const selectUsername = (state: RootState) => state.auth.currentUser?.username ?? null;
export const selectUserEmail = (state: RootState) => state.auth.currentUser?.email ?? null;

// ============================================================================
// Reducer Export
// ============================================================================

export default authSlice.reducer;

