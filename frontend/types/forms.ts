/**
 * Form Data Types
 * TypeScript interfaces for all form data (14 forms)
 */

import { ReviewItemType, SearchType } from './enums';

/**
 * Login form data
 */
export interface LoginFormData {
  username: string;
  password: string;
  rememberMe?: boolean;
}

/**
 * Registration form data
 */
export interface RegisterFormData {
  username: string;
  email: string;
  password: string;
  repeatPassword: string;
}

/**
 * Forgot password form data
 */
export interface ForgotPasswordFormData {
  email: string;
}

/**
 * Reset password form data
 */
export interface ResetPasswordFormData {
  password: string;
  confirmPassword: string;
  token: string;
}

// ============================================================================
// User Forms (1)
// ============================================================================

/**
 * Edit profile form data
 */
export interface EditProfileFormData {
  username: string;
  name?: string;
  bio?: string;
  profilePicture?: number;
}

// ============================================================================
// Review Forms (2)
// ============================================================================

/**
 * Review form data
 */
export interface ReviewFormData {
  title: string;
  description: string;
  rating: number; // 1-5, step 1
  item_id: number;
  item_type: ReviewItemType;
}

/**
 * Comment form data
 */
export interface CommentFormData {
  content: string;
  reviewId: number;
}

// ============================================================================
// Moderator Forms (6)
// ============================================================================

/**
 * Create artist form data
 */
export interface CreateArtistFormData {
  name: string;
  bio?: string;
  artist_img_id?: number;
}

/**
 * Edit artist form data
 */
export interface EditArtistFormData {
  id: number;
  name: string;
  bio?: string;
  artist_img_id?: number;
}

/**
 * Create album form data
 */
export interface CreateAlbumFormData {
  title: string;
  artist_id: number;
  release_date?: string; // ISO date string
  genre?: string;
  album_image_id?: number;
}

/**
 * Edit album form data
 */
export interface EditAlbumFormData {
  id: number;
  title: string;
  artist_id: number;
  release_date?: string; // ISO date string
  genre?: string;
  album_image_id?: number;
}

/**
 * Create song form data
 */
export interface CreateSongFormData {
  title: string;
  album_id: number;
  duration: number; // seconds
  track_number?: number;
  song_image_id?: number;
}

/**
 * Edit song form data
 */
export interface EditSongFormData {
  id: number;
  title: string;
  album_id: number;
  duration: number; // seconds
  track_number?: number;
  song_image_id?: number;
}

// ============================================================================
// Search Form (1)
// ============================================================================

/**
 * Search form data
 */
export interface SearchFormData {
  query: string;
  type?: SearchType;
}

// ============================================================================
// Form Validation Error Type
// ============================================================================

/**
 * Form field error
 */
export interface FormFieldError {
  type: string;
  message: string;
}

/**
 * Form errors by field
 */
export type FormErrors<T> = Partial<Record<keyof T, FormFieldError>>;

// ============================================================================
// Form Submission State
// ============================================================================

/**
 * Form submission state
 */
export interface FormSubmissionState {
  isSubmitting: boolean;
  isSuccess: boolean;
  isError: boolean;
  error?: string;
}

