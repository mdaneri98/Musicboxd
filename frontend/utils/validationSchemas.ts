/**
 * Validation Schemas
 * Yup validation schemas for all forms in the application
 */

import * as Yup from 'yup';

// ============================================================================
// Auth Forms
// ============================================================================

export const loginSchema = Yup.object().shape({
  username: Yup.string()
    .required('Username is required')
    .min(3, 'Username must be at least 3 characters')
    .max(50, 'Username must be at most 50 characters'),
  password: Yup.string()
    .required('Password is required')
    .min(6, 'Password must be at least 6 characters'),
  rememberMe: Yup.boolean(),
});

export const registerSchema = Yup.object().shape({
  username: Yup.string()
    .required('Username is required')
    .min(3, 'Username must be at least 3 characters')
    .max(50, 'Username must be at most 50 characters')
    .matches(/^[a-zA-Z0-9_-]+$/, 'Username can only contain letters, numbers, underscores and dashes'),
  email: Yup.string()
    .required('Email is required')
    .email('Invalid email address')
    .max(100, 'Email must be at most 100 characters'),
  password: Yup.string()
    .required('Password is required')
    .min(6, 'Password must be at least 6 characters')
    .max(100, 'Password must be at most 100 characters'),
  repeatPassword: Yup.string()
    .required('Please confirm your password')
    .oneOf([Yup.ref('password')], 'Passwords must match'),
});

export const forgotPasswordSchema = Yup.object().shape({
  email: Yup.string()
    .required('Email is required')
    .email('Invalid email address'),
});

export const resetPasswordSchema = Yup.object().shape({
  password: Yup.string()
    .required('Password is required')
    .min(6, 'Password must be at least 6 characters')
    .max(100, 'Password must be at most 100 characters'),
  repeatPassword: Yup.string()
    .required('Please confirm your password')
    .oneOf([Yup.ref('password')], 'Passwords must match'),
});

// ============================================================================
// User Forms
// ============================================================================

export const editProfileSchema = Yup.object().shape({
  username: Yup.string()
    .required('Username is required')
    .min(3, 'Username must be at least 3 characters')
    .max(50, 'Username must be at most 50 characters')
    .matches(/^[a-zA-Z0-9_-]+$/, 'Username can only contain letters, numbers, underscores and dashes'),
  name: Yup.string()
    .optional()
    .max(100, 'Name must be at most 100 characters'),
  bio: Yup.string()
    .optional()
    .max(500, 'Bio must be at most 500 characters'),
  profilePicture: Yup.mixed().optional().nullable(),
});

// ============================================================================
// Review Forms
// ============================================================================

export const reviewSchema = Yup.object().shape({
  title: Yup.string()
    .required('Title is required')
    .min(3, 'Title must be at least 3 characters')
    .max(100, 'Title must be at most 100 characters'),
  description: Yup.string()
    .required('Description is required')
    .min(10, 'Description must be at least 10 characters')
    .max(2000, 'Description must be at most 2000 characters'),
  rating: Yup.number()
    .required('Rating is required')
    .min(1, 'Rating must be at least 1')
    .max(5, 'Rating must be at most 5'),
});

// ============================================================================
// Comment Forms
// ============================================================================

export const commentSchema = Yup.object().shape({
  content: Yup.string()
    .required('Comment is required')
    .min(1, 'Comment must be at least 1 character')
    .max(500, 'Comment must be at most 500 characters'),
  reviewId: Yup.number().required(),
});

// ============================================================================
// Music Forms (Moderator)
// ============================================================================

export const artistSchema = Yup.object().shape({
  name: Yup.string()
    .required('Artist name is required')
    .min(1, 'Artist name must be at least 1 character')
    .max(100, 'Artist name must be at most 100 characters'),
  bio: Yup.string()
    .max(2000, 'Bio must be at most 2000 characters'),
  image: Yup.mixed().nullable(),
});

export const albumSchema = Yup.object().shape({
  title: Yup.string()
    .required('Album title is required')
    .min(1, 'Album title must be at least 1 character')
    .max(100, 'Album title must be at most 100 characters'),
  artistId: Yup.number()
    .required('Artist is required')
    .positive('Please select an artist'),
  releaseDate: Yup.date()
    .nullable()
    .max(new Date(), 'Release date cannot be in the future'),
  genre: Yup.string()
    .max(50, 'Genre must be at most 50 characters'),
  image: Yup.mixed().nullable(),
});

export const songSchema = Yup.object().shape({
  title: Yup.string()
    .required('Song title is required')
    .min(1, 'Song title must be at least 1 character')
    .max(100, 'Song title must be at most 100 characters'),
  albumId: Yup.number()
    .required('Album is required')
    .positive('Please select an album'),
  duration: Yup.number()
    .nullable()
    .positive('Duration must be positive')
    .max(7200, 'Duration must be at most 2 hours (7200 seconds)'),
  trackNumber: Yup.number()
    .nullable()
    .positive('Track number must be positive')
    .max(999, 'Track number must be at most 999'),
});

// ============================================================================
// Search Form
// ============================================================================

export const searchSchema = Yup.object().shape({
  query: Yup.string()
    .required('Search query is required')
    .min(1, 'Search query must be at least 1 character')
    .max(100, 'Search query must be at most 100 characters'),
  type: Yup.string()
    .oneOf(['all', 'users', 'artists', 'albums', 'songs', 'reviews'], 'Invalid search type'),
});

// ============================================================================
// Settings Forms
// ============================================================================

export const changePasswordSchema = Yup.object().shape({
  currentPassword: Yup.string()
    .required('Current password is required'),
  newPassword: Yup.string()
    .required('New password is required')
    .min(6, 'Password must be at least 6 characters')
    .max(100, 'Password must be at most 100 characters'),
  confirmPassword: Yup.string()
    .required('Please confirm your new password')
    .oneOf([Yup.ref('newPassword')], 'Passwords must match'),
});

export const deleteAccountSchema = Yup.object().shape({
  password: Yup.string()
    .required('Password is required to delete your account'),
  confirmation: Yup.string()
    .required('Please type DELETE to confirm')
    .oneOf(['DELETE'], 'Please type DELETE to confirm'),
});

