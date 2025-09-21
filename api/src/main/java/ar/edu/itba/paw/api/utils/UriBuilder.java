package ar.edu.itba.paw.api.utils;

import org.springframework.stereotype.Component;

@Component
public class UriBuilder {
    
    public String buildUserUri(String baseUrl, Long userId) {
        return baseUrl + ApiUriConstants.USERS_BASE + "/" + userId;
    }
    
    public String buildUserReviewsUri(String baseUrl, Long userId) {
        return baseUrl + ApiUriConstants.USERS_BASE + "/" + userId + "/reviews";
    }
    
    public String buildUserFollowersUri(String baseUrl, Long userId) {
        return baseUrl + ApiUriConstants.USERS_BASE + "/" + userId + "/followers";
    }
    
    public String buildUserFollowingUri(String baseUrl, Long userId) {
        return baseUrl + ApiUriConstants.USERS_BASE + "/" + userId + "/following";
    }
    
    public String buildCollectionUri(String baseUrl, String resource) {
        return baseUrl + ApiUriConstants.API_BASE + "/" + resource;
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
