/**
 * Redux Store Configuration
 * Central store with all slices and middleware
 */

import { configureStore, Middleware, combineReducers } from '@reduxjs/toolkit';
import authReducer from './slices/authSlice';
import userReducer from './slices/userSlice';
import artistReducer from './slices/artistSlice';
import albumReducer from './slices/albumSlice';
import songReducer from './slices/songSlice';
import reviewReducer from './slices/reviewSlice';
import notificationReducer from './slices/notificationSlice';
import searchReducer from './slices/searchSlice';
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

const rootReducer = combineReducers({
  auth: authReducer,
  users: userReducer,
  artists: artistReducer,
  albums: albumReducer,
  songs: songReducer,
  reviews: reviewReducer,
  notifications: notificationReducer,
  ui: uiReducer,
  search: searchReducer,
});

export type RootState = ReturnType<typeof rootReducer>;

export const setupStore = (preloadedState?: Partial<RootState>) => {
  return configureStore({
    reducer: rootReducer,
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
    preloadedState: preloadedState as any, // Type cast needed for partial state
    devTools: process.env.NODE_ENV !== 'production',
  });
};

export const store = setupStore();

// ============================================================================
// Types
// ============================================================================

export type AppDispatch = typeof store.dispatch;

