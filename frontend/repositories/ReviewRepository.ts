/**
 * Review Repository
 * Handles review-related API calls
 */

import { apiClient } from '@/lib/apiClient';
import { buildUrl } from '@/utils/halHelpers';
import {
  Review,
  User,
  Comment,
  Collection,
  HALResource,
  FilterParams,
  PaginationParams,
  ReviewFormData,
} from '@/types';

// ============================================================================
// API Endpoints
// ============================================================================

const REVIEW_ENDPOINTS = {
  REVIEWS: '/reviews',
  REVIEW_BY_ID: (id: number) => `/reviews/${id}`,
  REVIEW_LIKES: (id: number) => `/reviews/${id}/likes`,
  REVIEW_COMMENTS: (id: number) => `/reviews/${id}/comments`,
};

// ============================================================================
// Review Repository Class
// ============================================================================

class ReviewRepository {
  /**
   * Get paginated list of reviews with optional search and filter
   * @param page Page number (0-indexed)
   * @param size Page size
   * @param search Optional search query
   * @param filter Optional filter type
   * @returns Collection of reviews with pagination metadata
   */
  async getReviews(
    page: number = 1,
    size: number = 10,
    search?: string,
    filter?: string
  ): Promise<Collection<HALResource<Review>>> {
    try {
      const params: FilterParams = { page, size };
      if (search) params.search = search;
      if (filter) params.filter = filter as any;

      const url = buildUrl(REVIEW_ENDPOINTS.REVIEWS, params as Record<string, string | number | boolean>);
      const response: Collection<HALResource<Review>> = await apiClient.getCollection<Review>(url);

      if (!response) {
        throw new Error('Invalid reviews response: missing data');
      }

      return response;
    } catch (error) {
      console.error('Get reviews error:', error);
      throw error;
    }
  }

  /**
   * Get review by ID
   * @param id Review ID
   * @returns Review data
   */
  async getReviewById(id: number): Promise<HALResource<Review>> {
    try {
      const response: HALResource<Review> = await apiClient.getResource<Review>(
        REVIEW_ENDPOINTS.REVIEW_BY_ID(id)
      );

      if (!response) {
        throw new Error('Invalid review response: missing data');
      }

      return response as HALResource<Review>;
    } catch (error) {
      console.error(`Get review ${id} error:`, error);
      throw error;
    }
  }

  /**
   * Create a new review
   * @param reviewData Review data
   * @returns Created review
   */
  async createReview(reviewData: ReviewFormData): Promise<HALResource<Review>> {
    try {
      const response: HALResource<Review> = await apiClient.postResource<Review>(
        REVIEW_ENDPOINTS.REVIEWS,
        reviewData
      );

      if (!response) {
        throw new Error('Invalid create review response: missing data');
      }

      return response as HALResource<Review>;
    } catch (error) {
      console.error('Create review error:', error);
      throw error;
    }
  }

  /**
   * Update review
   * @param id Review ID
   * @param reviewData Updated review data
   * @returns Updated review
   */
  async updateReview(id: number, reviewData: ReviewFormData): Promise<HALResource<Review>> {
    try {
      const response: HALResource<Review> = await apiClient.putResource<Review>(
        REVIEW_ENDPOINTS.REVIEW_BY_ID(id),
        reviewData
      );

      if (!response) {
        throw new Error('Invalid update review response: missing data');
      }

      return response as HALResource<Review>;
    } catch (error) {
      console.error(`Update review ${id} error:`, error);
      throw error;
    }
  }

  /**
   * Delete review
   * @param id Review ID
   */
  async deleteReview(id: number): Promise<void> {
    try {
      await apiClient.deleteResource<Review>(REVIEW_ENDPOINTS.REVIEW_BY_ID(id));
    } catch (error) {
      console.error(`Delete review ${id} error:`, error);
      throw error;
    }
  }

  /**
   * Get users who liked a review
   * @param reviewId Review ID
   * @param page Page number
   * @param size Page size
   * @returns Collection of users
   */
  async getReviewLikes(
    reviewId: number,
    page: number = 1,
    size: number = 10
  ): Promise<Collection<HALResource<User>>> {
    try {
      const params: PaginationParams = { page, size };
      const url = buildUrl(REVIEW_ENDPOINTS.REVIEW_LIKES(reviewId), params as Record<string, string | number | boolean>);
      const response: Collection<HALResource<User>> = await apiClient.getCollection<User>(url);

      if (!response) {
        throw new Error('Invalid review likes response: missing data');
      }

      return response;
    } catch (error) {
      console.error(`Get review ${reviewId} likes error:`, error);
      throw error;
    }
  }

  /**
   * Like a review
   * @param reviewId Review ID
   */
  async likeReview(reviewId: number): Promise<void> {
    try {
      await apiClient.postResource<Review>(REVIEW_ENDPOINTS.REVIEW_LIKES(reviewId));
    } catch (error) {
      console.error(`Like review ${reviewId} error:`, error);
      throw error;
    }
  }

  /**
   * Unlike a review
   * @param reviewId Review ID
   */
  async unlikeReview(reviewId: number): Promise<void> {
    try {
      await apiClient.deleteResource<Review>(REVIEW_ENDPOINTS.REVIEW_LIKES(reviewId));
    } catch (error) {
      console.error(`Unlike review ${reviewId} error:`, error);
      throw error;
    }
  }

  /**
   * Get comments for a review
   * @param reviewId Review ID
   * @param page Page number
   * @param size Page size
   * @returns Collection of comments
   */
  async getReviewComments(
    reviewId: number,
    page: number = 1,
    size: number = 10
  ): Promise<Collection<HALResource<Comment>>> {
    try {
      const params: PaginationParams = { page, size };
      const url = buildUrl(REVIEW_ENDPOINTS.REVIEW_COMMENTS(reviewId), params as Record<string, string | number | boolean>);
      const response: Collection<HALResource<Comment>> = await apiClient.getCollection<Comment>(url);

      if (!response) {
        throw new Error('Invalid review comments response: missing data');
      }

      return response;
    } catch (error) {
      console.error(`Get review ${reviewId} comments error:`, error);
      throw error;
    }
  }

}

// ============================================================================
// Export Singleton Instance
// ============================================================================

export const reviewRepository = new ReviewRepository();

