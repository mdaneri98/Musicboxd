/**
 * Domain Models
 * TypeScript interfaces for all domain entities
 */

// ============================================================================
// User Model
// ============================================================================
export interface User {
  id: number;
  username: string;
  email: string;
  name?: string;
  bio?: string;
  imageId?: number;
  followersAmount: number;
  followingAmount: number;
  reviewsAmount: number;
  isFollowing?: boolean; // Context-dependent
  isModerator: boolean;
  isVerified: boolean;
  createdAt: Date;
}

// ============================================================================
// Artist Model
// ============================================================================
export interface Artist {
  id: number;
  name: string;
  bio?: string;
  imageId?: number;
  albumsCount: number;
  songsCount: number;
  reviewsCount: number;
  averageRating?: number;
  isFavorite?: boolean; // Context-dependent
}

// ============================================================================
// Album Model
// ============================================================================
export interface Album {
  id: number;
  title: string;
  artistId: number;
  artistName: string;
  releaseDate?: Date;
  genre?: string;
  imageId?: number;
  songsCount: number;
  reviewsCount: number;
  averageRating?: number;
  isFavorite?: boolean; // Context-dependent
}

// ============================================================================
// Song Model
// ============================================================================
export interface Song {
  id: number;
  title: string;
  albumId: number;
  albumTitle: string;
  artistId: number;
  artistName: string;
  duration: number; // seconds
  trackNumber?: number;
  reviewsCount: number;
  averageRating?: number;
  isFavorite?: boolean; // Context-dependent
}

// ============================================================================
// Review Model
// ============================================================================
export type ReviewItemType = 'Artist' | 'Album' | 'Song';

export interface Review {
  id: number;
  user_id: number;
  user_image_id: number;
  username: string;
  item_id: number;
  item_type: ReviewItemType;
  item_name: string;
  item_image_id: number;
  title: string;
  description: string;
  rating: number; // 1-5, step 0.5
  likes: number;
  comment_amount: number;
  is_liked?: boolean; // Context-dependent
  is_blocked: boolean;
  created_at: Date;
  updated_at?: Date;
  time_ago: string;
}

// ============================================================================
// Comment Model
// ============================================================================
export interface Comment {
  id: number;
  userId: number;
  username: string;
  userImageId?: number;
  reviewId: number;
  content: string;
  createdAt: Date;
  updatedAt?: Date;
}

// ============================================================================
// Notification Model
// ============================================================================
export type NotificationType = 'follow' | 'like' | 'comment' | 'review';

export interface Notification {
  id: number;
  userId: number;
  type: NotificationType;
  message: string;
  relatedId?: number; // e.g., review ID, user ID
  isRead: boolean;
  createdAt: Date;
}

// ============================================================================
// Image Model
// ============================================================================
export interface Image {
  id: number;
  url: string; // Constructed as /api/images/{id}
}

