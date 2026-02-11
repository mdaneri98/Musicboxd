/**
 * Song Repository
 * Handles song-related API calls
 */

import { apiClient } from '@/lib/apiClient';
import { CustomMediaType } from '@/types/mediaTypes';
import { buildUrl } from '@/utils/halHelpers';
import {
  Song,
  Review,
  Collection,
  HALResource,
  FilterParams,
  EditSongFormData,
  CreateSongFormData
} from '@/types';

// ============================================================================
// API Endpoints
// ============================================================================

const SONG_ENDPOINTS = {
  SONGS: '/songs',
  SONG_BY_ID: (id: number) => `/songs/${id}`,
  SONG_REVIEWS: (id: number) => `/songs/${id}/reviews`,
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
    size: number = 10,
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

      return response;
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
  async createSong(songData: CreateSongFormData): Promise<HALResource<Song>> {
    try {
      const response: HALResource<Song> = await apiClient.postResource<Song>(
        SONG_ENDPOINTS.SONGS,
        songData,
        { headers: { 'Content-Type': CustomMediaType.SONG } }
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
  async updateSong(id: number, songData: EditSongFormData): Promise<HALResource<Song>> {
    try {
      const response: HALResource<Song> = await apiClient.putResource<Song>(
        SONG_ENDPOINTS.SONG_BY_ID(id),
        songData,
        { headers: { 'Content-Type': CustomMediaType.SONG } }
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
    size: number = 10,
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

      return response;
    } catch (error) {
      console.error(`Get song ${id} reviews error:`, error);
      throw error;
    }
  }

  /**
   * Get a specific user's review for a song
   * @param songId Song ID
   * @param userId User ID
   * @returns The user's review or null if not found
   */
  async getUserReviewForSong(
    songId: number,
    userId: number
  ): Promise<Review | null> {
    try {
      const url = buildUrl(SONG_ENDPOINTS.SONG_REVIEWS(songId), { userId } as Record<string, string | number | boolean>);
      const response: Collection<HALResource<Review>> = await apiClient.getCollection<Review>(url);

      if (response && response.items && response.items.length > 0) {
        return response.items[0];
      }

      return null;
    } catch (error) {
      console.error(`Get user ${userId} review for song ${songId} error:`, error);
      return null;
    }
  }
}


// ============================================================================
// Export Singleton Instance
// ============================================================================

export const songRepository = new SongRepository();

