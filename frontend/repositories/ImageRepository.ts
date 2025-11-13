/**
 * Image Repository
 * Handles image-related operations
 */


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
}


// ============================================================================
// Export Singleton Instance
// ============================================================================

export const imageRepository = new ImageRepository();

