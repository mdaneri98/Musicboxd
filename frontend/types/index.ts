/**
 * Type Exports
 * Central export point for all TypeScript types
 */

// Models
export type {
  User,
  Artist,
  Album,
  Song,
  Review,
  ReviewItemType,
  Comment,
  Notification,
  NotificationType,
  Image,
} from './models';

// API Types
export type {
  HALLink,
  HALResource,
  Collection,
  PaginationParams,
  SearchParams,
  LoginCredentials,
  LoginResponse,
  RegisterData,
  RefreshTokenResponse,
  APIError,
  ValidationError,
  APIResponse,
  APIRequestOptions,
  FilterParams,
} from './api';

// Enums
export {
  FilterType as FilterTypeEnum,
  HomeTab as HomeTabEnum,
  ReviewItemType as ReviewItemTypeEnum,
  NotificationType as NotificationTypeEnum,
  SearchType as SearchTypeEnum,
  Theme as ThemeEnum,
  Language as LanguageEnum,
  ProfileTab as ProfileTabEnum,
  ReviewTab as ReviewTabEnum,
  SearchTab as SearchTabEnum,
  MusicTab as MusicTabEnum,
} from './enums';

// Form Types
export type {
  LoginFormData,
  RegisterFormData,
  ForgotPasswordFormData,
  ResetPasswordFormData,
  EditProfileFormData,
  ReviewFormData,
  CommentFormData,
  CreateArtistFormData,
  EditArtistFormData,
  CreateAlbumFormData,
  EditAlbumFormData,
  CreateSongFormData,
  EditSongFormData,
  SearchFormData,
  FormFieldError,
  FormErrors,
  FormSubmissionState,
} from './forms';

