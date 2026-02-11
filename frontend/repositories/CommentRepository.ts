/**
 * Comment Repository
 * Handles comment-related API calls
 */

import { apiClient } from '@/lib/apiClient';
import { CustomMediaType } from '@/types/mediaTypes';
import { buildUrl } from '@/utils/halHelpers';
import {
  Comment,
  Collection,
  HALResource,
  FilterParams,
  CommentFormData,
} from '@/types';

// ============================================================================
// API Endpoints
// ============================================================================

const COMMENT_ENDPOINTS = {
  COMMENTS: '/comments',
  COMMENT_BY_ID: (id: number) => `/comments/${id}`,
};

// ============================================================================
// Comment Repository Class
// ============================================================================

class CommentRepository {
  /**
   * Get paginated list of comments with optional search and filter
   * @param page Page number (0-indexed)
   * @param size Page size
   * @param search Optional search query
   * @param filter Optional filter type
   * @returns Collection of comments with pagination metadata
   */
  async getComments(
    page: number = 1,
    size: number = 10,
    search?: string,
    filter?: string
  ): Promise<Collection<HALResource<Comment>>> {
    try {
      const params: FilterParams = { page, size };
      if (search) params.search = search;
      if (filter) params.filter = filter as any;

      const url = buildUrl(COMMENT_ENDPOINTS.COMMENTS, params as Record<string, string | number | boolean>);
      const response: Collection<HALResource<Comment>> = await apiClient.getCollection<Comment>(url);

      if (!response) {
        throw new Error('Invalid comments response: missing data');
      }

      return response as Collection<HALResource<Comment>>;
    } catch (error) {
      console.error('Get comments error:', error);
      throw error;
    }
  }

  /**
   * Get comment by ID
   * @param id Comment ID
   * @returns Comment data
   */
  async getCommentById(id: number): Promise<HALResource<Comment>> {
    try {
      const response: HALResource<Comment> = await apiClient.getResource<Comment>(
        COMMENT_ENDPOINTS.COMMENT_BY_ID(id)
      );

      if (!response) {
        throw new Error('Invalid comment response: missing data');
      }

      return response as HALResource<Comment>;
    } catch (error) {
      console.error(`Get comment ${id} error:`, error);
      throw error;
    }
  }

  /**
   * Create a new comment
   * @param commentData Comment data
   * @returns Created comment
   */
  async createComment(commentData: CommentFormData): Promise<HALResource<Comment>> {
    try {
      const response: HALResource<Comment> = await apiClient.postResource<Comment>(
        COMMENT_ENDPOINTS.COMMENTS,
        commentData,
        { headers: { 'Content-Type': CustomMediaType.COMMENT } }
      );

      if (!response) {
        throw new Error('Invalid create comment response: missing data');
      }

      return response as HALResource<Comment>;
    } catch (error) {
      console.error('Create comment error:', error);
      throw error;
    }
  }

  /**
   * Update comment
   * @param id Comment ID
   * @param commentData Updated comment data
   * @returns Updated comment
   */
  async updateComment(id: number, commentData: CommentFormData): Promise<HALResource<Comment>> {
    try {
      const response: HALResource<Comment> = await apiClient.putResource<Comment>(
        COMMENT_ENDPOINTS.COMMENT_BY_ID(id),
        commentData,
        { headers: { 'Content-Type': CustomMediaType.COMMENT } }
      );

      if (!response) {
        throw new Error('Invalid update comment response: missing data');
      }

      return response as HALResource<Comment>;
    } catch (error) {
      console.error(`Update comment ${id} error:`, error);
      throw error;
    }
  }

  /**
   * Delete comment
   * @param id Comment ID
   */
  async deleteComment(id: number): Promise<void> {
    try {
      await apiClient.deleteResource<Comment>(COMMENT_ENDPOINTS.COMMENT_BY_ID(id));
    } catch (error) {
      console.error(`Delete comment ${id} error:`, error);
      throw error;
    }
  }
}

// ============================================================================
// Export Singleton Instance
// ============================================================================

export const commentRepository = new CommentRepository();

