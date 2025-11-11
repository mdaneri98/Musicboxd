/**
 * Artist Repository
 * Handles artist-related API calls
 */

import { apiClient } from '@/lib/apiClient';
import { buildUrl } from '@/utils/halHelpers';
import {
  Artist,
  Review,
  Album,
  Song,
  Collection,
  HALResource,
  FilterParams,
} from '@/types';

// ============================================================================
// API Endpoints
// ============================================================================

const ARTIST_ENDPOINTS = {
  ARTISTS: '/artists',
  ARTIST_BY_ID: (id: number) => `/artists/${id}`,
  ARTIST_REVIEWS: (id: number) => `/artists/${id}/reviews`,
  ARTIST_ALBUMS: (id: number) => `/artists/${id}/albums`,
  ARTIST_SONGS: (id: number) => `/artists/${id}/songs`,
  ADD_FAVORITE: (id: number) => `/artists/${id}/favorites`,
  REMOVE_FAVORITE: (id: number) => `/artists/${id}/favorites`,
};

// ============================================================================
// Artist Repository Class
// ============================================================================

class ArtistRepository {
  /**
   * Get paginated list of artists with optional search and filter
   * @param page Page number (0-indexed)
   * @param size Page size
   * @param search Optional search query
   * @param filter Optional filter type
   * @returns Collection of artists with pagination metadata
   */
  async getArtists(
    page: number = 1,
    size: number = 20,
    search?: string,
    filter?: string
  ): Promise<Collection<HALResource<Artist>>> {
    try {
      const params: FilterParams = { page, size };
      if (search) params.search = search;
      if (filter) params.filter = filter as any;

      const url = buildUrl(ARTIST_ENDPOINTS.ARTISTS, params as Record<string, string | number | boolean>);
      const response: Collection<HALResource<Artist>> = await apiClient.getCollection<Artist>(url);

      if (!response) {
        throw new Error('Invalid artists response: missing data');
      }

      return response as Collection<HALResource<Artist>>;
    } catch (error) {
      console.error('Get artists error:', error);
      throw error;
    }
  }

  /**
   * Get artist by ID
   * @param id Artist ID
   * @returns Artist data
   */
  async getArtistById(id: number): Promise<HALResource<Artist>> {
    try {
      const response: HALResource<Artist> = await apiClient.getResource<Artist>(
        ARTIST_ENDPOINTS.ARTIST_BY_ID(id)
      );

      if (!response) {
        throw new Error('Invalid artist response: missing data');
      }

      return response as HALResource<Artist>;
    } catch (error) {
      console.error(`Get artist ${id} error:`, error);
      throw error;
    }
  }

  /**
   * Create a new artist
   * @param artistData Artist data
   * @returns Created artist
   */
  async createArtist(artistData: Partial<Artist>): Promise<HALResource<Artist>> {
    try {
      const response: HALResource<Artist> = await apiClient.postResource<Artist>(
        ARTIST_ENDPOINTS.ARTISTS,
        artistData
      );

      if (!response) {
        throw new Error('Invalid create artist response: missing data');
      }

      return response as HALResource<Artist>;
    } catch (error) {
      console.error('Create artist error:', error);
      throw error;
    }
  }

  /**
   * Update artist
   * @param id Artist ID
   * @param artistData Updated artist data
   * @returns Updated artist
   */
  async updateArtist(id: number, artistData: Partial<Artist>): Promise<HALResource<Artist>> {
    try {
      const response: HALResource<Artist> = await apiClient.putResource<Artist>(
        ARTIST_ENDPOINTS.ARTIST_BY_ID(id),
        artistData
      );

      if (!response) {
        throw new Error('Invalid update artist response: missing data');
      }

      return response as HALResource<Artist>;
    } catch (error) {
      console.error(`Update artist ${id} error:`, error);
      throw error;
    }
  }

  /**
   * Delete artist
   * @param id Artist ID
   * @returns Deleted artist
   */
  async deleteArtist(id: number): Promise<void> {
    try {
      await apiClient.deleteResource<Artist>(ARTIST_ENDPOINTS.ARTIST_BY_ID(id));
    } catch (error) {
      console.error(`Delete artist ${id} error:`, error);
      throw error;
    }
  }

  /**
   * Get artist's reviews
   * @param id Artist ID
   * @param page Page number
   * @param size Page size
   * @param filter Optional filter
   * @returns Collection of reviews
   */
  async getArtistReviews(
    id: number,
    page: number = 1,
    size: number = 20,
    filter?: string
  ): Promise<Collection<HALResource<Review>>> {
    try {
      const params: FilterParams = { page, size };
      if (filter) params.filter = filter as any;

      const url = buildUrl(ARTIST_ENDPOINTS.ARTIST_REVIEWS(id), params as Record<string, string | number | boolean>);
      const response: Collection<HALResource<Review>> = await apiClient.getCollection<Review>(url);

      if (!response) {
        throw new Error('Invalid artist reviews response: missing data');
      }

      return response as Collection<HALResource<Review>>;
    } catch (error) {
      console.error(`Get artist ${id} reviews error:`, error);
      throw error;
    }
  }

  /**
   * Create a review for an artist
   * @param artistId Artist ID
   * @param reviewData Review data
   * @returns Created review
   */
  async createArtistReview(artistId: number, reviewData: Partial<Review>): Promise<HALResource<Review>> {
    try {
      const response: HALResource<Review> = await apiClient.postResource<Review>(
        ARTIST_ENDPOINTS.ARTIST_REVIEWS(artistId),
        reviewData
      );

      if (!response) {
        throw new Error('Invalid create review response: missing data');
      }

      return response as HALResource<Review>;
    } catch (error) {
      console.error(`Create artist ${artistId} review error:`, error);
      throw error;
    }
  }

  /**
   * Get artist's albums
   * @param id Artist ID
   * @param page Page number
   * @param size Page size
   * @returns Collection of albums
   */
  async getArtistAlbums(
    id: number,
    page: number = 1,
    size: number = 20
  ): Promise<Collection<HALResource<Album>>> {
    try {
      const params = { page, size };
      const url = buildUrl(ARTIST_ENDPOINTS.ARTIST_ALBUMS(id), params);
      const response: Collection<HALResource<Album>> = await apiClient.getCollection<Album>(url);

      if (!response) {
        throw new Error('Invalid artist albums response: missing data');
      }

      return response as Collection<HALResource<Album>>;
    } catch (error) {
      console.error(`Get artist ${id} albums error:`, error);
      throw error;
    }
  }

  /**
   * Create an album for an artist
   * @param artistId Artist ID
   * @param albumData Album data
   * @returns Created album
   */
  async createArtistAlbum(artistId: number, albumData: Partial<Album>): Promise<HALResource<Album>> {
    try {
      const response: HALResource<Album> = await apiClient.postResource<Album>(
        ARTIST_ENDPOINTS.ARTIST_ALBUMS(artistId),
        albumData
      );

      if (!response) {
        throw new Error('Invalid create album response: missing data');
      }

      return response as HALResource<Album>;
    } catch (error) {
      console.error(`Create artist ${artistId} album error:`, error);
      throw error;
    }
  }

  /**
   * Get artist's songs
   * @param id Artist ID
   * @param page Page number
   * @param size Page size
   * @returns Collection of songs
   */
  async getArtistSongs(
    id: number,
    page: number = 1,
    size: number = 20
  ): Promise<Collection<HALResource<Song>>> {
    try {
      const params = { page, size };
      const url = buildUrl(ARTIST_ENDPOINTS.ARTIST_SONGS(id), params);
      const response: Collection<HALResource<Song>> = await apiClient.getCollection<Song>(url);

      if (!response) {
        throw new Error('Invalid artist songs response: missing data');
      }

      return response as Collection<HALResource<Song>>;
    } catch (error) {
      console.error(`Get artist ${id} songs error:`, error);
      throw error;
    }
  }

  /**
   * Add artist to user's favorites
   * @param id Artist ID
   */
  async addArtistFavorite(id: number): Promise<void> {
    try {
      await apiClient.postResource<Artist>(ARTIST_ENDPOINTS.ADD_FAVORITE(id));
    } catch (error) {
      console.error(`Add artist ${id} to favorites error:`, error);
      throw error;
    }
  }

  /**
   * Remove artist from user's favorites
   * @param id Artist ID
   */
  async removeArtistFavorite(id: number): Promise<void> {
    try {
      await apiClient.deleteResource<Artist>(ARTIST_ENDPOINTS.REMOVE_FAVORITE(id));
    } catch (error) {
      console.error(`Remove artist ${id} from favorites error:`, error);
      throw error;
    }
  }
}

// ============================================================================
// Export Singleton Instance
// ============================================================================

export const artistRepository = new ArtistRepository();

