/**
 * Redux Store Configuration
 * Central store with all slices and middleware
 */

import { configureStore, Middleware } from '@reduxjs/toolkit';
import authReducer from './slices/authSlice';
import userReducer from './slices/userSlice';
import artistReducer from './slices/artistSlice';
import albumReducer from './slices/albumSlice';
import songReducer from './slices/songSlice';
import reviewReducer from './slices/reviewSlice';
import notificationReducer from './slices/notificationSlice';
import uiReducer from './slices/uiSlice';

// ============================================================================
// Logger Middleware (Development only)
// ============================================================================

const logger: Middleware = (store) => (next) => (action) => {
  if (process.env.NODE_ENV === 'development') {
    console.group((action as any).type);
    console.info('dispatching', action);
    const result = next(action);
    console.log('next state', store.getState());
    console.groupEnd();
    return result;
  }
  return next(action);
};

// ============================================================================
// Store Configuration
// ============================================================================

export const store = configureStore({
  reducer: {
    auth: authReducer,
    users: userReducer,
    artists: artistReducer,
    albums: albumReducer,
    songs: songReducer,
    reviews: reviewReducer,
    notifications: notificationReducer,
    ui: uiReducer,
  },
  middleware: (getDefaultMiddleware) => {
    const middlewares = getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: ['persist/PERSIST'],
        ignoredActionPaths: ['payload.timestamp'],
        ignoredPaths: ['auth.timestamp'],
      },
    });

    // Add logger only in development
    if (process.env.NODE_ENV === 'development') {
      return middlewares.concat(logger);
    }

    return middlewares;
  },
  devTools: process.env.NODE_ENV !== 'production',
});

// ============================================================================
// Types
// ============================================================================

// Infer the `RootState` and `AppDispatch` types from the store itself
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

