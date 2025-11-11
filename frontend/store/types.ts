/**
 * Redux Store Types
 * Shared types for the Redux store
 */

import { store } from './index';

// Inferred types from the store
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

// Async thunk types
export interface AsyncState {
  loading: boolean;
  error: string | null;
}

// Generic async thunk state
export interface AsyncThunkState<T = unknown> extends AsyncState {
  data: T | null;
}

