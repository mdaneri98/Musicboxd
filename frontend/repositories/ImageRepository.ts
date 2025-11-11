/**
 * Image Repository
 * Handles image-related operations
 */

import { apiClient } from '@/lib/apiClient';
import { Image, HALResource } from '@/types';

// ============================================================================
// API Endpoints
// ============================================================================

const IMAGE_ENDPOINTS = {
  IMAGES: '/images',
  IMAGE_BY_ID: (id: number) => `/images/${id}` 
};

// ============================================================================
// Image Repository Class
// ============================================================================

class ImageRepository {
  /**
   * Get full image URL from image ID
   * @param id Image ID
   * @returns Full image URL
   */
  getImageUrl(id: number): string {
    const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/api_war/api';
    return `${baseUrl}${IMAGE_ENDPOINTS.IMAGE_BY_ID(id)}`;
  }

  /**
   * Upload an image file
   * @param file Image file to upload
   * @returns Uploaded image metadata
   */
  // async uploadImage(file: File): Promise<Image> {
  //   try {
  //     const formData = new FormData();
  //     formData.append('image', file);

  //     const response: HALResource<Image> = await apiClient.upload<Image>(
  //       IMAGE_ENDPOINTS.UPLOAD,
  //       formData
  //     );

  //     const image = response.data;
  //     if (!image) {
  //       throw new Error('Invalid upload image response: missing data');
  //     }

  //     return image;
  //   } catch (error) {
  //     console.error('Upload image error:', error);
  //     throw error;
  //   }
  // }

  /**
   * Get image metadata by ID
   * @param id Image ID
   * @returns Image metadata
   */
  async getImageById(id: number): Promise<HALResource<Image>> {
    try {
      const response: HALResource<Image> = await apiClient.getResource<Image>(
        IMAGE_ENDPOINTS.IMAGE_BY_ID(id)
      );

      if (!response) {
        throw new Error('Invalid image response: missing data');
      }

      return response as HALResource<Image>;
    } catch (error) {
      console.error(`Get image ${id} error:`, error);
      throw error;
    }
  }
}

// ============================================================================
// Export Singleton Instance
// ============================================================================

export const imageRepository = new ImageRepository();

