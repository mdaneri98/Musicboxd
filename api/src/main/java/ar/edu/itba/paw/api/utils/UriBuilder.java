package ar.edu.itba.paw.api.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Utility class for building URIs using Spring's UriComponentsBuilder.
 * This approach provides:
 * - Automatic URL encoding
 * - Security against path traversal attacks
 * - Type-safe URI construction
 * - Better maintainability
 */
@Component
public class UriBuilder {
    
    // User URIs
    public String buildUserReviewsUri(String baseUrl, Long userId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.USERS_BASE)
                .pathSegment(userId.toString(), "reviews")
                .toUriString();
    }
    
    public String buildUserFollowersUri(String baseUrl, Long userId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.USERS_BASE)
                .pathSegment(userId.toString(), "followers")
                .toUriString();
    }
    
    public String buildUserFollowingUri(String baseUrl, Long userId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.USERS_BASE)
                .pathSegment(userId.toString(), "following")
                .toUriString();
    }
    
    // Artist URIs
    public String buildArtistReviewsUri(String baseUrl, Long artistId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.ARTISTS_BASE)
                .pathSegment(artistId.toString(), "reviews")
                .toUriString();
    }
    
    public String buildArtistAlbumsUri(String baseUrl, Long artistId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.ARTISTS_BASE)
                .pathSegment(artistId.toString(), "albums")
                .toUriString();
    }
    
    
    // Album URIs
    public String buildAlbumReviewsUri(String baseUrl, Long albumId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.ALBUMS_BASE)
                .pathSegment(albumId.toString(), "reviews")
                .toUriString();
    }
    
    public String buildAlbumSongsUri(String baseUrl, Long albumId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.ALBUMS_BASE)
                .pathSegment(albumId.toString(), "songs")
                .toUriString();
    }

    public String buildAlbumArtistUri(String baseUrl, Long artistId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.ARTISTS_BASE)
                .pathSegment(artistId.toString())
                .toUriString();
    }
    
    // Song URIs
    public String buildSongReviewsUri(String baseUrl, Long songId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.SONGS_BASE)
                .pathSegment(songId.toString(), "reviews")
                .toUriString();
    }

    public String buildSongAlbumUri(String baseUrl, Long albumId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.ALBUMS_BASE)
                .pathSegment(albumId.toString())
                .toUriString();
    }

    public String buildSongArtistUri(String baseUrl, Long artistId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.ARTISTS_BASE)
                .pathSegment(artistId.toString())
                .toUriString();
    }
    
    // Review URIs
    public String buildReviewCommentsUri(String baseUrl, Long reviewId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.REVIEWS_BASE)
                .pathSegment(reviewId.toString(), "comments")
                .toUriString();
    }
    
    public String buildReviewLikesUri(String baseUrl, Long reviewId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.REVIEWS_BASE)
                .pathSegment(reviewId.toString(), "likes")
                .toUriString();
    }
    
    // Comment URIs
    public String buildCommentReviewUri(String baseUrl, Long reviewId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.REVIEWS_BASE)
                .pathSegment(reviewId.toString(), "comments")
                .toUriString();
    }

    public String buildCommentUri(String baseUrl, Long commentId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.COMMENTS_BASE)
                .pathSegment(commentId.toString())
                .toUriString();
    }


    // Image URIs
    public String buildImageUri(String baseUrl, Long imageId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.IMAGES_BASE)
                .pathSegment(imageId.toString())
                .toUriString();
    }
    
    // Auth URIs
    public String buildAuthUri(String baseUrl, String action) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.AUTH_BASE)
                .pathSegment(action)
                .toUriString();
    }
    
    // Generic URIs for HATEOAS
    public String buildResourceUri(String baseUrl, String resourcePath, Long id) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(resourcePath)
                .pathSegment(id.toString())
                .toUriString();
    }
    
    public String buildCollectionUri(String baseUrl, String resourcePath) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(resourcePath)
                .toUriString();
    }
    
    public String buildPaginatedUri(String baseUrl, String resourcePath, int page, int size) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(resourcePath)
                .queryParam("page", page)
                .queryParam("size", size)
                .toUriString();
    }
}
