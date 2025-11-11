/**
 * Enums and Type Unions
 */

// ============================================================================
// Filter Types
// ============================================================================

/**
 * Filter type for sorting/filtering lists
 */
export enum FilterType {
  POPULAR = 'POPULAR',
  RECENT = 'RECENT',
  OLDEST = 'OLDEST',
  LIKES = 'LIKES',
  NEWEST = 'NEWEST',
  FIRST = 'FIRST',
  RATING = 'RATING',
  USERNAME = 'USERNAME',
  EMAIL = 'EMAIL',
  NAME = 'NAME',
  BIO = 'BIO',
  IMAGE_ID = 'IMAGE_ID',
  FOLLOWERS_AMOUNT = 'FOLLOWERS_AMOUNT',
  FOLLOWING_AMOUNT = 'FOLLOWING_AMOUNT',
  REVIEW_AMOUNT = 'REVIEW_AMOUNT',
  UPDATED_AT = 'UPDATED_AT',
  CREATED_AT = 'CREATED_AT',
}

/**
 * Filter type as union
 */
export type FilterTypeUnion = 'POPULAR' | 'RECENT' | 'OLDEST' | 'LIKES' | 'NEWEST' | 'FIRST' | 'RATING' | 'USERNAME' | 'EMAIL' | 'NAME' | 'BIO' | 'IMAGE_ID' | 'FOLLOWERS_AMOUNT' | 'FOLLOWING_AMOUNT' | 'REVIEW_AMOUNT' | 'UPDATED_AT' | 'CREATED_AT';

// ============================================================================
// Review Item Types
// ============================================================================

/**
 * Review item type enum
 */
export enum ReviewItemType {
  ARTIST = 'Artist',
  ALBUM = 'Album',
  SONG = 'Song',
}

/**
 * Review item type as union
 */
export type ReviewItemTypeUnion = 'Artist' | 'Album' | 'Song';

// ============================================================================
// Notification Types
// ============================================================================

/**
 * Notification type enum
 */
export enum NotificationType {
  FOLLOW = 'follow',
  LIKE = 'like',
  COMMENT = 'comment',
  REVIEW = 'review',
}

/**
 * Notification type as union
 */
export type NotificationTypeUnion = 'follow' | 'like' | 'comment' | 'review';

// ============================================================================
// Search Types
// ============================================================================

/**
 * Search entity type
 */
export enum SearchType {
  ALL = 'all',
  USERS = 'users',
  ARTISTS = 'artists',
  ALBUMS = 'albums',
  SONGS = 'songs',
  REVIEWS = 'reviews',
}

/**
 * Search type as union
 */
export type SearchTypeUnion = 'all' | 'users' | 'artists' | 'albums' | 'songs' | 'reviews';

// ============================================================================
// Theme Types
// ============================================================================

/**
 * Available themes
 */
export enum Theme {
  DARK = 'dark',
  SEPIA = 'sepia',
  OCEAN = 'ocean',
  FOREST = 'forest',
  KAWAII = 'kawaii',
}

/**
 * Theme as union
 */
export type ThemeUnion = 'dark' | 'sepia' | 'ocean' | 'forest' | 'kawaii';

// ============================================================================
// Language Types
// ============================================================================

/**
 * Available languages for i18n
 */
export enum Language {
  EN = 'en',
  ES = 'es',
  DE = 'de',
  FR = 'fr',
  IT = 'it',
  JA = 'ja',
  PT = 'pt',
}

/**
 * Language as union
 */
export type LanguageUnion = 'en' | 'es' | 'de' | 'fr' | 'it' | 'ja' | 'pt';

