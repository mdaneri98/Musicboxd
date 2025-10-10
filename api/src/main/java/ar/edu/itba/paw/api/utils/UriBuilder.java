package ar.edu.itba.paw.api.utils;

import org.springframework.stereotype.Component;

@Component
public class UriBuilder {
    
    // User URIs
    public String buildUserUri(String baseUrl, Long userId) {
        return baseUrl + ApiUriConstants.USER_BY_ID.replace("{id}", userId.toString());
    }
    
    public String buildUserReviewsUri(String baseUrl, Long userId) {
        return baseUrl + ApiUriConstants.USER_REVIEWS.replace("{id}", userId.toString());
    }
    
    public String buildUserFollowersUri(String baseUrl, Long userId) {
        return baseUrl + ApiUriConstants.USER_FOLLOWERS.replace("{id}", userId.toString());
    }
    
    public String buildUserFollowingUri(String baseUrl, Long userId) {
        return baseUrl + ApiUriConstants.USER_FOLLOWING.replace("{id}", userId.toString());
    }
    
    // Artist URIs
    public String buildArtistUri(String baseUrl, Long artistId) {
        return baseUrl + ApiUriConstants.ARTIST_BY_ID.replace("{id}", artistId.toString());
    }
    
    public String buildArtistReviewsUri(String baseUrl, Long artistId) {
        return baseUrl + ApiUriConstants.ARTIST_REVIEWS.replace("{id}", artistId.toString());
    }
    
    // Album URIs
    public String buildAlbumUri(String baseUrl, Long albumId) {
        return baseUrl + ApiUriConstants.ALBUM_BY_ID.replace("{id}", albumId.toString());
    }
    
    public String buildAlbumReviewsUri(String baseUrl, Long albumId) {
        return baseUrl + ApiUriConstants.ALBUM_REVIEWS.replace("{id}", albumId.toString());
    }
    
    public String buildAlbumSongsUri(String baseUrl, Long albumId) {
        return baseUrl + ApiUriConstants.ALBUM_SONGS.replace("{id}", albumId.toString());
    }
    
    // Song URIs
    public String buildSongUri(String baseUrl, Long songId) {
        return baseUrl + ApiUriConstants.SONG_BY_ID.replace("{id}", songId.toString());
    }
    
    public String buildSongReviewsUri(String baseUrl, Long songId) {
        return baseUrl + ApiUriConstants.SONG_REVIEWS.replace("{id}", songId.toString());
    }
    
    // Review URIs
    public String buildReviewUri(String baseUrl, Long reviewId) {
        return baseUrl + ApiUriConstants.REVIEW_BY_ID.replace("{id}", reviewId.toString());
    }
    
    public String buildReviewCommentsUri(String baseUrl, Long reviewId) {
        return baseUrl + ApiUriConstants.REVIEW_COMMENTS.replace("{id}", reviewId.toString());
    }
    
    public String buildReviewLikesUri(String baseUrl, Long reviewId) {
        return baseUrl + ApiUriConstants.REVIEW_LIKES.replace("{id}", reviewId.toString());
    }
    
    // Comment URIs
    public String buildCommentUri(String baseUrl, Long commentId) {
        return baseUrl + ApiUriConstants.COMMENT_BY_ID.replace("{id}", commentId.toString());
    }
    
    // Image URIs
    public String buildImageUri(String baseUrl, Long imageId) {
        return baseUrl + ApiUriConstants.IMAGE_BY_ID.replace("{id}", imageId.toString());
    }
    
    // Generic URIs
    public String buildCollectionUri(String baseUrl, String resource) {
        return baseUrl + ApiUriConstants.API_BASE + "/" + resource + "s";
    }
    
    public String buildPaginationUri(String baseUrl, String resource, int page, int size) {
        return baseUrl + ApiUriConstants.API_BASE + "/" + resource + 
               "?page=" + page + "&size=" + size;
    }
    
    public String buildSearchUri(String baseUrl, String resource, String query) {
        return baseUrl + ApiUriConstants.API_BASE + "/" + resource + 
               "/search?q=" + query;
    }
    
    public String buildAuthUri(String baseUrl, String action) {
        return baseUrl + ApiUriConstants.AUTH_BASE + "/" + action;
    }
}
