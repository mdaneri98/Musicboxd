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
  FOLLOWING = 'FOLLOWING',
  RECOMMENDED = 'RECOMMENDED',
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

// ============================================================================
// Review Item Types
// ============================================================================

/**
 * Review item type enum
 */
export enum ReviewItemType {
  ARTIST = 'ARTIST',
  ALBUM = 'ALBUM',
  SONG = 'SONG',
}


// ============================================================================
// Notification Types
// ============================================================================

/**
 * Notification type enum
 */
export enum NotificationType {
  FOLLOW = 'FOLLOW',
  LIKE = 'LIKE',
  COMMENT = 'COMMENT',
  NEW_REVIEW = 'NEW_REVIEW',
}

// ============================================================================
// Search Types
// ============================================================================

/**
 * Search entity type
 */
export enum SearchType {
  MUSIC = 'music',
  USERS = 'users',
  ARTISTS = 'artists',
  ALBUMS = 'albums',
  SONGS = 'songs',
}


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

// ============================================================================
// Profile Tab Types
// ============================================================================

/**
 * Profile tab type enum
 */
export enum ProfileTab {
  FAVORITES = 'favorites',
  REVIEWS = 'reviews',
}

// ============================================================================
// Review Tab Types
// ============================================================================

/**
 * Review tab type enum
 */
export enum ReviewTab {
  LIKES = 'likes',
  COMMENTS = 'comments',
}


// ============================================================================
// Search Tab Types
// ============================================================================

/**
 * Search tab type enum
 */
export enum SearchTab {
  MUSIC = 'music',
  USERS = 'users',
}


// ============================================================================
// Music Tab Types
// ============================================================================

/**
 * Music tab type enum
 */
export enum MusicTab {
  POPULAR = 'popular',
  TOP_RATED = 'top_rated',
}

// ============================================================================
// Home Tab Types
// ============================================================================

/**
 * Home tab type enum
 */
export enum HomeTab {
  FOR_YOU = 'for_you',
  FOLLOWING = 'following',
}