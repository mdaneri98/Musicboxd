import { NotificationType, ReviewItemType } from "./enums";

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
  image_id?: number;
  followers_amount: number;
  following_amount: number;
  review_amount: number;
  followed?: boolean; // Context-dependent
  moderator: boolean;
  verified: boolean;
  created_at: Date;
}

// ============================================================================
// Artist Model
// ============================================================================
export interface Artist {
  id: number;
  name: string;
  bio?: string;
  image_id?: number;
  rating_count: number;
  avg_rating: number;
  created_at: number;
  updated_at?: number;
  favorite?: boolean; // Context-dependent
  reviewed?: boolean; // Context-dependent
}

// ============================================================================
// Album Model
// ============================================================================
export interface Album {
  id: number;
  title: string;
  artist_id: number;
  artist_name: string;
  release_date?: Date;
  genre?: string;
  image_id?: number;
  rating_count: number;
  avg_rating: number;
  created_at: number;
  updated_at?: number;
  deleted: boolean;
  formatted_release_date?: string;
  favorite?: boolean; // Context-dependent
  reviewed?: boolean; // Context-dependent
}

// ============================================================================
// Song Model
// ============================================================================
export interface Song {
  id: number;
  title: string;
  album_id: number;
  album_title: string;
  artist_id: number;
  album_image_id: number;
  duration: string;
  track_number?: number;
  rating_count: number;
  avg_rating: number;
  created_at: number;
  updated_at?: number;
  deleted: boolean;
  formatted_release_date?: string;
  favorite?: boolean; // Context-dependent
  reviewed?: boolean; // Context-dependent
}

// ============================================================================
// Review Model
// ============================================================================

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
  rating: number; // 1-5, step 1
  likes: number;
  comment_amount: number;
  liked?: boolean; // Context-dependent
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
  user_id: number;
  username: string;
  user_image_id?: number;
  review_id: number;
  content: string;
  created_at: Date;
  time_ago: string;
}

// ============================================================================
// Notification Model
// ============================================================================

export interface Notification {
  id: number;
  recipient_user_id: number;
  type: NotificationType;
  message: string;
  recipient_username: string;
  trigger_user_id: number;
  trigger_username: string;
  trigger_user_image_id: number;
  review_id?: number;
  review_item_name?: string;
  review_item_image_id?: number;
  is_read: boolean;
  created_at: Date;
  time_ago: string;
}

// ============================================================================
// Image Model
// ============================================================================
export interface Image {
  id: number;
  url: string; // Constructed as /api/images/{id}
}

