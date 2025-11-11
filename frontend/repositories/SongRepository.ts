/**
 * Song Repository
 * Handles song-related API calls
 */

import { apiClient } from '@/lib/apiClient';
import { buildUrl } from '@/utils/halHelpers';
import {
  Song,
  Review,
  Collection,
  HALResource,
  FilterParams,
} from '@/types';

// ============================================================================
// API Endpoints
// ============================================================================

const SONG_ENDPOINTS = {
  SONGS: '/songs',
  SONG_BY_ID: (id: number) => `/songs/${id}`,
  SONG_REVIEWS: (id: number) => `/songs/${id}/reviews`,
  ADD_FAVORITE: (id: number) => `/songs/${id}/favorites`,
  REMOVE_FAVORITE: (id: number) => `/songs/${id}/favorites`,
};

// ============================================================================
// Song Repository Class
// ============================================================================

class SongRepository {
  /**
   * Get paginated list of songs with optional search and filter
   * @param page Page number (0-indexed)
   * @param size Page size
   * @param search Optional search query
   * @param filter Optional filter type
   * @returns Collection of songs with pagination metadata
   */
  async getSongs(
    page: number = 1,
    size: number = 20,
    search?: string,
    filter?: string
  ): Promise<Collection<HALResource<Song>>> {
    try {
      const params: FilterParams = { page, size };
      if (search) params.search = search;
      if (filter) params.filter = filter as any;

      const url = buildUrl(SONG_ENDPOINTS.SONGS, params as Record<string, string | number | boolean>);
      const response: Collection<HALResource<Song>> = await apiClient.getCollection<Song>(url);

        if (!response) {
        throw new Error('Invalid songs response: missing data');
      }

      return response as Collection<HALResource<Song>>;
    } catch (error) {
      console.error('Get songs error:', error);
      throw error;
    }
  }

  /**
   * Get song by ID
   * @param id Song ID
   * @returns Song data
   */
  async getSongById(id: number): Promise<HALResource<Song>> {
    try {
      const response: HALResource<Song> = await apiClient.getResource<Song>(
        SONG_ENDPOINTS.SONG_BY_ID(id)
      );

      if (!response) {
        throw new Error('Invalid song response: missing data');
      }

      return response as HALResource<Song>;
    } catch (error) {
      console.error(`Get song ${id} error:`, error);
      throw error;
    }
  }

  /**
   * Create a new song
   * @param songData Song data
   * @returns Created song
   */
  async createSong(songData: Partial<Song>): Promise<HALResource<Song>> {
    try {
      const response: HALResource<Song> = await apiClient.postResource<Song>(
        SONG_ENDPOINTS.SONGS,
        songData
      );

      if (!response) {
        throw new Error('Invalid create song response: missing data');
      }

      return response as HALResource<Song>;
    } catch (error) {
      console.error('Create song error:', error);
      throw error;
    }
  }

  /**
   * Update song
   * @param id Song ID
   * @param songData Updated song data
   * @returns Updated song
   */
  async updateSong(id: number, songData: Partial<Song>): Promise<HALResource<Song>> {
    try {
      const response: HALResource<Song> = await apiClient.putResource<Song>(
        SONG_ENDPOINTS.SONG_BY_ID(id),
        songData
      );

      if (!response) {
        throw new Error('Invalid update song response: missing data');
      }

      return response as HALResource<Song>;
    } catch (error) {
      console.error(`Update song ${id} error:`, error);
      throw error;
    }
  }

  /**
   * Delete song
   * @param id Song ID
   */
  async deleteSong(id: number): Promise<void> {
    try {
      await apiClient.deleteResource<Song>(SONG_ENDPOINTS.SONG_BY_ID(id));
    } catch (error) {
      console.error(`Delete song ${id} error:`, error);
      throw error;
    }
  }

  /**
   * Get song's reviews
   * @param id Song ID
   * @param page Page number
   * @param size Page size
   * @param filter Optional filter
   * @returns Collection of reviews
   */
  async getSongReviews(
    id: number,
    page: number = 1,
    size: number = 20,
    filter?: string
  ): Promise<Collection<HALResource<Review>>> {
    try {
      const params: FilterParams = { page, size };
      if (filter) params.filter = filter as any;

      const url = buildUrl(SONG_ENDPOINTS.SONG_REVIEWS(id), params as Record<string, string | number | boolean>);
      const response: Collection<HALResource<Review>> = await apiClient.getCollection<Review>(url);

      if (!response) {
        throw new Error('Invalid song reviews response: missing data');
      }

      return response as Collection<HALResource<Review>>;
    } catch (error) {
      console.error(`Get song ${id} reviews error:`, error);
      throw error;
    }
  }

  /**
   * Create a review for a song
   * @param songId Song ID
   * @param reviewData Review data
   * @returns Created review
   */
  async createSongReview(songId: number, reviewData: Partial<Review>): Promise<Review> {
    try {
      const response: HALResource<Review> = await apiClient.postResource<Review>(
        SONG_ENDPOINTS.SONG_REVIEWS(songId),
        reviewData
      );

      const review = response.data;
      if (!review) {
        throw new Error('Invalid create review response: missing data');
      }

      return review;
    } catch (error) {
      console.error(`Create song ${songId} review error:`, error);
      throw error;
    }
  }

  /**
   * Add song to user's favorites
   * @param id Song ID
   */
  async addSongFavorite(id: number): Promise<void> {
    try {
      await apiClient.postResource<Song>(SONG_ENDPOINTS.ADD_FAVORITE(id));
    } catch (error) {
      console.error(`Add song ${id} to favorites error:`, error);
      throw error;
    }
  }

  /**
   * Remove song from user's favorites
   * @param id Song ID
   */
  async removeSongFavorite(id: number): Promise<void> {
    try {
      await apiClient.deleteResource<Song>(SONG_ENDPOINTS.REMOVE_FAVORITE(id));
    } catch (error) {
      console.error(`Remove song ${id} from favorites error:`, error);
      throw error;
    }
  }
}

// ============================================================================
// Export Singleton Instance
// ============================================================================

export const songRepository = new SongRepository();

