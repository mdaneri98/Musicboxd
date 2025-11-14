/**
 * Album Repository
 * Handles album-related API calls
 */

import { apiClient } from '@/lib/apiClient';
import { buildUrl } from '@/utils/halHelpers';
import {
  Album,
  Review,
  Song,
  Collection,
  HALResource,
  FilterParams,
  CreateAlbumFormData,
  EditAlbumFormData,
  ReviewFormData,
  CreateSongFormData,
} from '@/types';

// ============================================================================
// API Endpoints
// ============================================================================

const ALBUM_ENDPOINTS = {
  ALBUMS: '/albums',
  ALBUM_BY_ID: (id: number) => `/albums/${id}`,
  ALBUM_REVIEWS: (id: number) => `/albums/${id}/reviews`,
  ALBUM_SONGS: (id: number) => `/albums/${id}/songs`,
  ADD_FAVORITE: (id: number) => `/albums/${id}/favorites`,
  REMOVE_FAVORITE: (id: number) => `/albums/${id}/favorites`,
};

// ============================================================================
// Album Repository Class
// ============================================================================

class AlbumRepository {
  /**
   * Get paginated list of albums with optional search and filter
   * @param page Page number (0-indexed)
   * @param size Page size
   * @param search Optional search query
   * @param filter Optional filter type
   * @returns Collection of albums with pagination metadata
   */
  async getAlbums(
    page: number = 1,
    size: number = 20,
    search?: string,
    filter?: string
  ): Promise<Collection<HALResource<Album>>> {
    try {
      const params: FilterParams = { page, size };
      if (search) params.search = search;
      if (filter) params.filter = filter as any;

      const url = buildUrl(ALBUM_ENDPOINTS.ALBUMS, params as Record<string, string | number | boolean>);
      const response: Collection<HALResource<Album>> = await apiClient.getCollection<Album>(url);

      if (!response) {
        throw new Error('Invalid albums response: missing data');
      }

      return response as Collection<HALResource<Album>>;
    } catch (error) {
      console.error('Get albums error:', error);
      throw error;
    }
  }

  /**
   * Get album by ID
   * @param id Album ID
   * @returns Album data
   */
  async getAlbumById(id: number): Promise<HALResource<Album>> {
    try {
      const response: HALResource<Album> = await apiClient.getResource<Album>(
        ALBUM_ENDPOINTS.ALBUM_BY_ID(id)
      );

      if (!response) {
        throw new Error('Invalid album response: missing data');
      }

      return response as HALResource<Album>;
    } catch (error) {
      console.error(`Get album ${id} error:`, error);
      throw error;
    }
  }

  /**
   * Create a new album
   * @param albumData Album data
   * @returns Created album
   */
  async createAlbum(albumData: CreateAlbumFormData): Promise<HALResource<Album>> {
    try {
      const response: HALResource<Album> = await apiClient.postResource<Album>(
        ALBUM_ENDPOINTS.ALBUMS,
        albumData
      );

      if (!response) {
        throw new Error('Invalid create album response: missing data');
      }

      return response as HALResource<Album>;
    } catch (error) {
      console.error('Create album error:', error);
      throw error;
    }
  }

  /**
   * Update album
   * @param id Album ID
   * @param albumData Updated album data
   * @returns Updated album
   */
    async updateAlbum(id: number, albumData: EditAlbumFormData): Promise<HALResource<Album>> {
    try {
      const response: HALResource<Album> = await apiClient.putResource<Album>(
        ALBUM_ENDPOINTS.ALBUM_BY_ID(id),
        albumData
      );

      if (!response) {
        throw new Error('Invalid update album response: missing data');
      }

      return response as HALResource<Album>;
    } catch (error) {
      console.error(`Update album ${id} error:`, error);
      throw error;
    }
  }

  /**
   * Delete album
   * @param id Album ID
   */
  async deleteAlbum(id: number): Promise<void> {
    try {
      await apiClient.deleteResource<Album>(ALBUM_ENDPOINTS.ALBUM_BY_ID(id));
    } catch (error) {
      console.error(`Delete album ${id} error:`, error);
      throw error;
    }
  }

  /**
   * Get album's reviews
   * @param id Album ID
   * @param page Page number
   * @param size Page size
   * @param filter Optional filter
   * @returns Collection of reviews
   */
  async getAlbumReviews(
    id: number,
    page: number = 1,
    size: number = 20,
    filter?: string
  ): Promise<Collection<HALResource<Review>>> {
    try {
      const params: FilterParams = { page, size };
      if (filter) params.filter = filter as any;

      const url = buildUrl(ALBUM_ENDPOINTS.ALBUM_REVIEWS(id), params as Record<string, string | number | boolean>);
      const response: Collection<HALResource<Review>> = await apiClient.getCollection<Review>(url);

      if (!response) {
        throw new Error('Invalid album reviews response: missing data');
      }

      return response as Collection<HALResource<Review>>;
    } catch (error) {
      console.error(`Get album ${id} reviews error:`, error);
      throw error;
    }
  }

  /**
   * Create a review for an album
   * @param albumId Album ID
   * @param reviewData Review data
   * @returns Created review
   */
  async createAlbumReview(albumId: number, reviewData: ReviewFormData): Promise<HALResource<Review>> {
    try {
      const response: HALResource<Review> = await apiClient.postResource<Review>(
        ALBUM_ENDPOINTS.ALBUM_REVIEWS(albumId),
        reviewData
      );

      if (!response) {
        throw new Error('Invalid create review response: missing data');
      }

      return response as HALResource<Review>;
    } catch (error) {
      console.error(`Create album ${albumId} review error:`, error);
      throw error;
    }
  }

  /**
   * Get album's songs
   * @param id Album ID
   * @param page Page number
   * @param size Page size
   * @returns Collection of songs
   */
  async getAlbumSongs(
    id: number,
    page: number = 1,
    size: number = 20
  ): Promise<Collection<HALResource<Song>>> {
    try {
      const params = { page, size };
      const url = buildUrl(ALBUM_ENDPOINTS.ALBUM_SONGS(id), params);
      const response: Collection<HALResource<Song>> = await apiClient.getCollection<Song>(url);

      if (!response) {
        throw new Error('Invalid album songs response: missing data');
      }

      return response as Collection<HALResource<Song>>;
    } catch (error) {
      console.error(`Get album ${id} songs error:`, error);
      throw error;
    }
  }

  /**
   * Create a song for an album
   * @param albumId Album ID
   * @param songData Song data
   * @returns Created song
   */
  async createAlbumSong(albumId: number, songData: CreateSongFormData): Promise<HALResource<Song>> {
    try {
      const response: HALResource<Song> = await apiClient.postResource<Song>(
        ALBUM_ENDPOINTS.ALBUM_SONGS(albumId),
        songData
      );

      if (!response) {
        throw new Error('Invalid create song response: missing data');
      }

      return response as HALResource<Song>;
    } catch (error) {
      console.error(`Create album ${albumId} song error:`, error);
      throw error;
    }
  }

  /**
   * Add album to user's favorites
   * @param id Album ID
   */
  async addAlbumFavorite(id: number): Promise<void> {
    try {
      await apiClient.postResource<Album>(ALBUM_ENDPOINTS.ADD_FAVORITE(id));
    } catch (error) {
      console.error(`Add album ${id} to favorites error:`, error);
      throw error;
    }
  }

  /**
   * Remove album from user's favorites
   * @param id Album ID
   */
  async removeAlbumFavorite(id: number): Promise<void> {
    try {
      await apiClient.deleteResource<Album>(ALBUM_ENDPOINTS.REMOVE_FAVORITE(id));
    } catch (error) {
      console.error(`Remove album ${id} from favorites error:`, error);
      throw error;
    }
  }
}

// ============================================================================
// Export Singleton Instance
// ============================================================================

export const albumRepository = new AlbumRepository();

