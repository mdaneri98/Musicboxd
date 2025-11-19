/**
 * Image Repository
 * Handles image-related operations
 */

import { apiClient } from '@/lib/apiClient';

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
    const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/api';
    return `${baseUrl}${IMAGE_ENDPOINTS.IMAGE_BY_ID(id)}`;
  }

  /**
   * Upload an image file to the server
   * @param file Image file to upload
   * @returns Promise resolving to the uploaded image ID
   */
  async uploadImage(file: File): Promise<number> {
    try {
      const formData = new FormData();
      formData.append('image', file);

      const response = await apiClient.post<{ id: number }>(
        IMAGE_ENDPOINTS.IMAGES,
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data',
          } as any,
        }
      );

      // Extract the image ID from the response
      if (response && response.id) {
        return response.id;
      }

      throw new Error('Invalid response: missing image ID');
    } catch (error) {
      console.error('Image upload error:', error);
      throw error;
    }
  }
}


// ============================================================================
// Export Singleton Instance
// ============================================================================

export const imageRepository = new ImageRepository();

