/**
 * User Repository
 * Handles user-related API calls
 */

import { apiClient } from '@/lib/apiClient';
import { buildUrl } from '@/utils/halHelpers';
import {
  User,
  Review,
  Artist,
  Album,
  Song,
  Collection,
  HALResource,
  FilterParams,
  PaginationParams,
  EditProfileFormData,
  UserConfigFormData,
} from '@/types';
import { FilterType } from '@/types/enums';

// ============================================================================
// API Endpoints
// ============================================================================

const USER_ENDPOINTS = {
  USERS: '/users',
  USER_BY_ID: (id: number) => `/users/${id}`,
  USER_REVIEWS: (id: number) => `/users/${id}/reviews`,
  USER_FOLLOWERS: (id: number) => `/users/${id}/followers`,
  USER_FOLLOWING: (id: number) => `/users/${id}/followings`,
  USER_FAVORITE_ARTISTS: (id: number) => `/users/${id}/favorites/artists`,
  USER_FAVORITE_ALBUMS: (id: number) => `/users/${id}/favorites/albums`,
  USER_FAVORITE_SONGS: (id: number) => `/users/${id}/favorites/songs`,
};

// ============================================================================
// User Repository Class
// ============================================================================

class UserRepository {
  /**
   * Get paginated list of users with optional search and filter
   * @param page Page number (0-indexed)
   * @param size Page size
   * @param search Optional search query
   * @param filter Optional filter type
   * @returns Collection of users with pagination metadata
   */
  async getUsers(
    page: number = 1,
    size: number = 20,
    search?: string,
    filter?: string
  ): Promise<Collection<HALResource<User>>> {
    try {
      const params: FilterParams = { page, size };
      if (search) params.search = search;
      if (filter) params.filter = filter as FilterType;

      const url = buildUrl(USER_ENDPOINTS.USERS, params as Record<string, string | number | boolean>);
      const response: Collection<HALResource<User>> = await apiClient.getCollection<User>(url);

      if (!response) {
        throw new Error('Invalid users response: missing data');
      }

      return response as Collection<HALResource<User>>;
    } catch (error) {
      console.error('Get users error:', error);
      throw error;
    }
  }

  /**
   * Get user by ID
   * @param id User ID
   * @returns User data
   */
  async getUserById(id: number): Promise<HALResource<User>> {
    try {
      const response: HALResource<User> = await apiClient.getResource<User>(
        USER_ENDPOINTS.USER_BY_ID(id)
      );

      if (!response) {
        throw new Error('Invalid user response: missing data');
      }

      return response as HALResource<User>;
    } catch (error) {
      console.error(`Get user ${id} error:`, error);
      throw error;
    }
  }

  /**
   * Create a new user
   * @param userData User data
   * @returns Created user
   */
  async createUser(userData: Partial<User>): Promise<HALResource<User>> {
    try {
      const response: HALResource<User> = await apiClient.postResource<User>(
        USER_ENDPOINTS.USERS,
        userData
      );

      if (!response) {
        throw new Error('Invalid create user response: missing data');
      }

      return response as HALResource<User>;
    } catch (error) {
      console.error('Create user error:', error);
      throw error;
    }
  }

  /**
   * Update user
   * @param id User ID
   * @param userData Updated user data
   * @returns Updated user
   */
  async updateUser(id: number, userData: EditProfileFormData): Promise<HALResource<User>> {
    try {
      const response: HALResource<User> = await apiClient.putResource<User>(
        USER_ENDPOINTS.USER_BY_ID(id),
        userData
      );

      if (!response) {
        throw new Error('Invalid update user response: missing data');
      }

      return response as HALResource<User>;
    } catch (error) {
      console.error(`Update user ${id} error:`, error);
      throw error;
    }
  }

  /**
   * Delete user
   * @param id User ID
   */
  async deleteUser(id: number): Promise<void> {
    try {
      await apiClient.deleteResource<User>(USER_ENDPOINTS.USER_BY_ID(id));
    } catch (error) {
      console.error(`Delete user ${id} error:`, error);
      throw error;
    }
  }

  /**
   * Get user's reviews
   * @param id User ID
   * @param page Page number
   * @param size Page size
   * @param filter Optional filter
   * @returns Collection of reviews
   */
  async getUserReviews(
    id: number,
    page: number = 1,
    size: number = 20,
    filter?: string
  ): Promise<Collection<HALResource<Review>>> {
    try {
      const params: FilterParams = { page, size };
      if (filter) params.filter = filter as any;

      const url = buildUrl(USER_ENDPOINTS.USER_REVIEWS(id), params as Record<string, string | number | boolean>);
      const response: Collection<HALResource<Review>> = await apiClient.getCollection<Review>(url);

      if (!response) {
        throw new Error('Invalid user reviews response: missing data');
      }

      return response as Collection<HALResource<Review>>;
    } catch (error) {
      console.error(`Get user ${id} reviews error:`, error);
      throw error;
    }
  }

  /**
   * Get user's followers
   * @param id User ID
   * @param page Page number
   * @param size Page size
   * @returns Collection of users who follow this user
   */
  async getFollowers(
    id: number,
    page: number = 1,
    size: number = 20
  ): Promise<Collection<HALResource<User>>> {
    try {
      const params: PaginationParams = { page, size };
      const url = buildUrl(USER_ENDPOINTS.USER_FOLLOWERS(id), params as Record<string, string | number | boolean>);
      const response: Collection<HALResource<User>> = await apiClient.getCollection<User>(url);

      if (!response) {
        throw new Error('Invalid followers response: missing data');
      }

      return response as Collection<HALResource<User>>;
    } catch (error) {
      console.error(`Get user ${id} followers error:`, error);
      throw error;
    }
  }

  /**
   * Get users that this user is following
   * @param id User ID
   * @param page Page number
   * @param size Page size
   * @returns Collection of users this user follows
   */
  async getFollowing(
    id: number,
    page: number = 1,
    size: number = 20
  ): Promise<Collection<HALResource<User>>> {
    try {
      const params: PaginationParams = { page, size };
      const url = buildUrl(USER_ENDPOINTS.USER_FOLLOWING(id), params as Record<string, string | number | boolean>);
      const response: Collection<HALResource<User>> = await apiClient.getCollection<User>(url);

      if (!response) {
        throw new Error('Invalid following response: missing data');
      }

      return response as Collection<HALResource<User>>;
    } catch (error) {
      console.error(`Get user ${id} following error:`, error);
      throw error;
    }
  }

  /**
   * Follow a user
   * @param id User ID to follow
   */
  async followUser(id: number): Promise<void> {
    try {
      await apiClient.post<void>(USER_ENDPOINTS.USER_FOLLOWERS(id));
    } catch (error) {
      console.error(`Follow user ${id} error:`, error);
      throw error;
    }
  }

  /**
   * Unfollow a user
   * @param id User ID to unfollow
   */
  async unfollowUser(id: number): Promise<void> {
    try {
      await apiClient.delete<void>(USER_ENDPOINTS.USER_FOLLOWERS(id));
    } catch (error) {
      console.error(`Unfollow user ${id} error:`, error);
      throw error;
    }
  }

  /**
   * Get user's favorite artists
   * @param id User ID
   * @returns Array of favorite artists
   */
  async getFavoriteArtists(id: number): Promise<Collection<HALResource<Artist>>> {
    try {
      const response: Collection<HALResource<Artist>> = await apiClient.getCollection<Artist>(
        USER_ENDPOINTS.USER_FAVORITE_ARTISTS(id)
      );

      if (!response) {
        throw new Error('Invalid favorite artists response: missing data');
      }

      return response as Collection<HALResource<Artist>>;
    } catch (error) {
      console.error(`Get user ${id} favorite artists error:`, error);
      throw error;
    }
  }

  /**
   * Get user's favorite albums
   * @param id User ID
   * @returns Array of favorite albums
   */
  async getFavoriteAlbums(id: number): Promise<Collection<HALResource<Album>>> {
    try {
      const response: Collection<HALResource<Album>> = await apiClient.getCollection<Album>(
        USER_ENDPOINTS.USER_FAVORITE_ALBUMS(id)
      );

      if (!response) {
        throw new Error('Invalid favorite albums response: missing data');
      }

      return response as Collection<HALResource<Album>>;
    } catch (error) {
      console.error(`Get user ${id} favorite albums error:`, error);
      throw error;
    }
  }

  /**
   * Get user's favorite songs
   * @param id User ID
   * @returns Array of favorite songs
   */
  async getFavoriteSongs(id: number): Promise<Collection<HALResource<Song>>> {
    try {
      const response: Collection<HALResource<Song>> = await apiClient.getCollection<Song>(
        USER_ENDPOINTS.USER_FAVORITE_SONGS(id)
      );

      if (!response) {
        throw new Error('Invalid favorite songs response: missing data');
      }

      return response as Collection<HALResource<Song>>;
    } catch (error) {
      console.error(`Get user ${id} favorite songs error:`, error);
      throw error;
    }
  }

  /**
   * Update user config
   * @param userId User ID
   * @param userData User config data
   * @returns Updated user config
   */
  async updateUserConfig(userId: number, userData: UserConfigFormData): Promise<HALResource<User>> {
    try {
      const response: HALResource<User> = await apiClient.patchResource<User>(
        USER_ENDPOINTS.USER_BY_ID(userId),
        userData
      );

      if (!response) {
        throw new Error('Invalid update user config response: missing data');
      }

      return response as HALResource<User>;
    } catch (error) {
      console.error(`Update user ${userId} config error:`, error);
      throw error;
    }
  }
}

// ============================================================================
// Export Singleton Instance
// ============================================================================

export const userRepository = new UserRepository();
