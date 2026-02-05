package ar.edu.itba.paw.webapp.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class UriBuilder {
    
    // User URIs
    public String buildUserUri(String baseUrl, Long userId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.USERS_BASE)
                .pathSegment(userId.toString())
                .toUriString();
    }
    
    public String buildUserReviewsUri(String baseUrl, Long userId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.USERS_BASE)
                .pathSegment(userId.toString(), ControllerUtils.RELATION_REVIEWS)
                .toUriString();
    }
    
    public String buildUserFollowersUri(String baseUrl, Long userId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.USERS_BASE)
                .pathSegment(userId.toString(), ControllerUtils.RELATION_FOLLOWERS)
                .toUriString();
    }
    
    public String buildUserFollowingUri(String baseUrl, Long userId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.USERS_BASE)
                .pathSegment(userId.toString(), ControllerUtils.RELATION_FOLLOWINGS)
                .toUriString();
    }

    public String buildUserFavoriteArtistsUri(String baseUrl, Long userId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.USERS_BASE)
                .pathSegment(userId.toString(), ControllerUtils.RELATION_FAVORITE_ARTISTS)
                .toUriString();
    }

    public String buildUserFavoriteAlbumsUri(String baseUrl, Long userId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.USERS_BASE)
                .pathSegment(userId.toString(), ControllerUtils.RELATION_FAVORITE_ALBUMS)
                .toUriString();
    }
    
    public String buildUserFavoriteSongsUri(String baseUrl, Long userId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.USERS_BASE)
                .pathSegment(userId.toString(), ControllerUtils.RELATION_FAVORITE_SONGS)
                .toUriString();
    }
    // Artist URIs
    public String buildArtistReviewsUri(String baseUrl, Long artistId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.ARTISTS_BASE)
                .pathSegment(artistId.toString(), ControllerUtils.RELATION_REVIEWS)
                .toUriString();
    }
    
        public String buildArtistAlbumsUri(String baseUrl, Long artistId) {
            return UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .path(ApiUriConstants.ARTISTS_BASE)
                    .pathSegment(artistId.toString(), ControllerUtils.RELATION_ALBUMS)
                    .toUriString();
        }

    public String buildArtistSongsUri(String baseUrl, Long artistId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.ARTISTS_BASE)
                .pathSegment(artistId.toString(), ControllerUtils.RELATION_SONGS)
                .toUriString();
    }

    public String buildArtistFavoriteUri(String baseUrl, Long artistId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.ARTISTS_BASE)
                .pathSegment(artistId.toString(), ControllerUtils.RELATION_FAVORITE)
                .toUriString();
    }
    
    // Album URIs
    public String buildAlbumReviewsUri(String baseUrl, Long albumId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.ALBUMS_BASE)
                .pathSegment(albumId.toString(), ControllerUtils.RELATION_REVIEWS)
                .toUriString();
    }
    
    public String buildAlbumSongsUri(String baseUrl, Long albumId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.ALBUMS_BASE)
                .pathSegment(albumId.toString(), ControllerUtils.RELATION_SONGS)
                .toUriString();
    }

    public String buildAlbumArtistUri(String baseUrl, Long artistId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.ARTISTS_BASE)
                .pathSegment(artistId.toString())
                .toUriString();
    }

    public String buildAlbumFavoriteUri(String baseUrl, Long albumId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.ALBUMS_BASE)
                .pathSegment(albumId.toString(), ControllerUtils.RELATION_FAVORITE)
                .toUriString();
    }
    
    // Song URIs
    public String buildSongReviewsUri(String baseUrl, Long songId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.SONGS_BASE)
                .pathSegment(songId.toString(), ControllerUtils.RELATION_REVIEWS)
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

    public String buildSongFavoriteUri(String baseUrl, Long songId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.SONGS_BASE)
                .pathSegment(songId.toString(), ControllerUtils.RELATION_FAVORITE)
                .toUriString();
    }
    
    // Review URIs
    public String buildReviewUri(String baseUrl, Long reviewId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.REVIEWS_BASE)
                .pathSegment(reviewId.toString())
                .toUriString();
    }
    
    public String buildReviewCommentsUri(String baseUrl, Long reviewId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.REVIEWS_BASE)
                .pathSegment(reviewId.toString(), ControllerUtils.RELATION_COMMENTS)
                .toUriString();
    }
    
    public String buildReviewLikesUri(String baseUrl, Long reviewId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.REVIEWS_BASE)
                .pathSegment(reviewId.toString(), ControllerUtils.RELATION_LIKES)
                .toUriString();
    }
    
    // Comment URIs
    public String buildCommentReviewUri(String baseUrl, Long reviewId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.REVIEWS_BASE)
                .pathSegment(reviewId.toString(), ControllerUtils.RELATION_COMMENTS)
                .toUriString();
    }

    public String buildCommentUri(String baseUrl, Long commentId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.COMMENTS_BASE)
                .pathSegment(commentId.toString())
                .toUriString();
    }

    // Notification URIs
    public String buildNotificationUri(String baseUrl, Long notificationId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.NOTIFICATIONS_BASE)
                .pathSegment(notificationId.toString())
                .toUriString();
    }

    public String buildNotificationReadUri(String baseUrl, Long notificationId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.NOTIFICATIONS_BASE)
                .pathSegment(notificationId.toString(), ControllerUtils.RELATION_READ)
                .toUriString();
    }

    public String buildNotificationReadAllUri(String baseUrl) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.NOTIFICATIONS_BASE)
                .pathSegment(ControllerUtils.RELATION_READ_ALL)
                .toUriString();
    }

    public String buildNotificationUnreadCountUri(String baseUrl) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.NOTIFICATIONS_BASE)
                .pathSegment(ControllerUtils.RELATION_UNREAD_COUNT)
                .toUriString();
    }


    // Image URIs
    public String buildImageUri(String baseUrl, Long imageId) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(ApiUriConstants.IMAGES_BASE)
                .pathSegment(imageId.toString())
                .toUriString();
    }
    
    // Generic URIs for HATEOAS
    public String buildResourceUri(String baseUrl, String resourcePath, Long id) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(resourcePath)
                .pathSegment(id.toString())
                .toUriString();
    }
    
    public String buildCollectionUri(String baseUrl, String resourcePath, Long id) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(resourcePath)
                .buildAndExpand(id != null ? id.toString() : "")
                .toUriString();
    }

    
    public String buildPaginatedUri(String baseUrl, String resourcePath, int page, int size, Long id) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(resourcePath)
                .queryParam(ControllerUtils.PAGE_PARAM_NAME, page)
                .queryParam(ControllerUtils.SIZE_PARAM_NAME, size)
                .buildAndExpand(id != null ? id.toString() : "")
                .toUriString();
    }
}
