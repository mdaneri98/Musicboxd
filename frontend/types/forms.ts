/**
 * Form Data Types
 * TypeScript interfaces for all form data (14 forms)
 */

import { ReviewItemType, SearchType } from './enums';

// ============================================================================
// Authentication Forms (4)
// ============================================================================

/**
 * Login form data
 */
export interface LoginFormData {
  username: string; // Backend uses username, not email for login
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
  repeatPassword: string; // Changed from confirmPassword to match JSP
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
  token: string; // Hidden field
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
  rating: number; // 1-5, step 0.5
  itemId: number; // Hidden field
  itemType: ReviewItemType; // Hidden field
}

/**
 * Comment form data
 */
export interface CommentFormData {
  content: string;
  reviewId: number; // Hidden field
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
  artistImgId?: number;
}

/**
 * Edit artist form data
 */
export interface EditArtistFormData extends CreateArtistFormData {
  id: number;
}

/**
 * Create album form data
 */
export interface CreateAlbumFormData {
  title: string;
  artistId: number;
  releaseDate?: string; // ISO date string
  genre?: string;
  albumImageId?: number;
}

/**
 * Edit album form data
 */
export interface EditAlbumFormData extends CreateAlbumFormData {
  id: number; // Hidden field
}

/**
 * Create song form data
 */
export interface CreateSongFormData {
  title: string;
  albumId: number;
  duration: number; // seconds
  trackNumber?: number;
  songImageId?: number;
}

/**
 * Edit song form data
 */
export interface EditSongFormData extends CreateSongFormData {
  id: number; // Hidden field
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

