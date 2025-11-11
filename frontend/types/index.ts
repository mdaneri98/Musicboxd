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
  ReviewItemType as ReviewItemTypeEnum,
  NotificationType as NotificationTypeEnum,
  SearchType,
  Theme,
  Language,
} from './enums';

export type {
  FilterTypeUnion,
  ReviewItemTypeUnion,
  NotificationTypeUnion,
  SearchTypeUnion,
  ThemeUnion,
  LanguageUnion,
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

