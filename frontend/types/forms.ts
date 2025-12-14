/**
 * Form Data Types
 * TypeScript interfaces for all form data (14 forms)
 */

import { LanguageEnum, ThemeEnum } from '.';
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
  imageId?: number;
}


/**
 * User config form data
 */
export interface UserConfigFormData {
  preferred_language?: LanguageEnum;
  preferred_theme?: ThemeEnum;
  has_follow_notifications_enabled?: boolean;
  has_like_notifications_enabled?: boolean;
  has_comments_notifications_enabled?: boolean;
  has_reviews_notifications_enabled?: boolean;
}
// ============================================================================
// Review Forms (2)
// ============================================================================

/**
 * Review form data
 */
export interface ReviewFormData {
  title?: string;
  description?: string;
  rating?: number;
  item_id?: number;
  item_type?: ReviewItemType;
  blocked?: boolean;
}

/**
 * Comment form data
 */
export interface CommentFormData {
  content: string;
  review_id: number;
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
  duration: string; // MM:SS
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
// Integrated Music Forms (ModArtistForm with nested albums and songs)
// ============================================================================

/**
 * Song form for nested usage in ModAlbumForm
 */
export interface ModSongFormData {
  id?: number;
  title: string;
  duration: string; // MM:SS format
  trackNumber?: number;
  albumId?: number;
  deleted?: boolean;
  // UI state
  _tempId?: string; // Temporary ID for new songs (UI only)
  _isCollapsed?: boolean;
}

/**
 * Album form for nested usage in ModArtistForm
 */
export interface ModAlbumFormData {
  id?: number;
  title: string;
  genre?: string;
  releaseDate?: string; // ISO date string (yyyy-MM-dd)
  albumImageId?: number;
  artistId?: number;
  songs: ModSongFormData[];
  deleted?: boolean;
  // UI state
  _tempId?: string; // Temporary ID for new albums (UI only)
  _isCollapsed?: boolean;
  _imageFile?: File; // For image upload
  _imagePreview?: string;
}

/**
 * Integrated artist form with nested albums and songs
 */
export interface ModArtistFormData {
  id?: number;
  name: string;
  bio?: string;
  artist_img_id?: number;
  albums: ModAlbumFormData[];
  deleted?: boolean;
  // UI state
  _imageFile?: File; // For image upload
  _imagePreview?: string;
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

