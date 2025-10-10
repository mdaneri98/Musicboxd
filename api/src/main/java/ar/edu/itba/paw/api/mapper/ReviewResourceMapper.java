package ar.edu.itba.paw.api.mapper;

import ar.edu.itba.paw.api.models.ReviewResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.HATEOASUtils;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.models.reviews.Review;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewResourceMapper {

    public ReviewResource toResource(ArtistReview artistReview, String baseUrl) {
        ReviewResource resource = new ReviewResource(artistReview);
        
        // Add CRUD links
        HATEOASUtils.addCrudLinks(resource, baseUrl, ApiUriConstants.REVIEWS_BASE, artistReview.getId());
        
        // Add related resources links
        resource.addLink(baseUrl + ApiUriConstants.REVIEWS_BASE + "/" + artistReview.getId() + "/comments", 
                        "comments", "Review comments");
        resource.addLink(baseUrl + ApiUriConstants.REVIEWS_BASE + "/" + artistReview.getId() + "/likes", 
                        "likes", "Review likes");
        
        // Add user link
        if (artistReview.getUser() != null && artistReview.getUser().getId() != null) {
            resource.addLink(baseUrl + ApiUriConstants.USERS_BASE + "/" + artistReview.getUser().getId(), 
                            "user", "Review author");
        }
        
        // Add artist link
        if (artistReview.getArtist() != null && artistReview.getArtist().getId() != null) {
            resource.addLink(baseUrl + ApiUriConstants.ARTISTS_BASE + "/" + artistReview.getArtist().getId(), 
                            "artist", "Reviewed artist");
        }
        
        // Add action links for like/unlike
        resource.addLink(baseUrl + ApiUriConstants.REVIEWS_BASE + "/" + artistReview.getId() + "/likes", 
                        "like", "Like this review", null, "POST");
        resource.addLink(baseUrl + ApiUriConstants.REVIEWS_BASE + "/" + artistReview.getId() + "/likes", 
                        "unlike", "Unlike this review", null, "DELETE");
        
        return resource;
    }

    public ReviewResource toResource(AlbumReview albumReview, String baseUrl) {
        ReviewResource resource = new ReviewResource(albumReview);
        
        // Add CRUD links
        HATEOASUtils.addCrudLinks(resource, baseUrl, ApiUriConstants.REVIEWS_BASE, albumReview.getId());
        
        // Add related resources links
        resource.addLink(baseUrl + ApiUriConstants.REVIEWS_BASE + "/" + albumReview.getId() + "/comments", 
                        "comments", "Review comments");
        resource.addLink(baseUrl + ApiUriConstants.REVIEWS_BASE + "/" + albumReview.getId() + "/likes", 
                        "likes", "Review likes");
        
        // Add user link
        if (albumReview.getUser() != null && albumReview.getUser().getId() != null) {
            resource.addLink(baseUrl + ApiUriConstants.USERS_BASE + "/" + albumReview.getUser().getId(), 
                            "user", "Review author");
        }
        
        // Add album link
        if (albumReview.getAlbum() != null && albumReview.getAlbum().getId() != null) {
            resource.addLink(baseUrl + ApiUriConstants.ALBUMS_BASE + "/" + albumReview.getAlbum().getId(), 
                            "album", "Reviewed album");
        }
        
        // Add action links for like/unlike
        resource.addLink(baseUrl + ApiUriConstants.REVIEWS_BASE + "/" + albumReview.getId() + "/likes", 
                        "like", "Like this review", null, "POST");
        resource.addLink(baseUrl + ApiUriConstants.REVIEWS_BASE + "/" + albumReview.getId() + "/likes", 
                        "unlike", "Unlike this review", null, "DELETE");
        
        return resource;
    }

    public ReviewResource toResource(SongReview songReview, String baseUrl) {
        ReviewResource resource = new ReviewResource(songReview);
        
        // Add CRUD links
        HATEOASUtils.addCrudLinks(resource, baseUrl, ApiUriConstants.REVIEWS_BASE, songReview.getId());
        
        // Add related resources links
        resource.addLink(baseUrl + ApiUriConstants.REVIEWS_BASE + "/" + songReview.getId() + "/comments", 
                        "comments", "Review comments");
        resource.addLink(baseUrl + ApiUriConstants.REVIEWS_BASE + "/" + songReview.getId() + "/likes", 
                        "likes", "Review likes");
        
        // Add user link
        if (songReview.getUser() != null && songReview.getUser().getId() != null) {
            resource.addLink(baseUrl + ApiUriConstants.USERS_BASE + "/" + songReview.getUser().getId(), 
                            "user", "Review author");
        }
        
        // Add song link
        if (songReview.getSong() != null && songReview.getSong().getId() != null) {
            resource.addLink(baseUrl + ApiUriConstants.SONGS_BASE + "/" + songReview.getSong().getId(), 
                            "song", "Reviewed song");
        }
        
        // Add action links for like/unlike
        resource.addLink(baseUrl + ApiUriConstants.REVIEWS_BASE + "/" + songReview.getId() + "/likes", 
                        "like", "Like this review", null, "POST");
        resource.addLink(baseUrl + ApiUriConstants.REVIEWS_BASE + "/" + songReview.getId() + "/likes", 
                        "unlike", "Unlike this review", null, "DELETE");
        
        return resource;
    }

    public <T extends Review> List<ReviewResource> toResourceList(List<T> reviews, String baseUrl) {
        return reviews.stream()
                .map(review -> {
                    if (review instanceof ArtistReview) {
                        return toResource((ArtistReview) review, baseUrl);
                    } else if (review instanceof AlbumReview) {
                        return toResource((AlbumReview) review, baseUrl);
                    } else if (review instanceof SongReview) {
                        return toResource((SongReview) review, baseUrl);
                    } else {
                        throw new IllegalArgumentException("Unknown review type: " + review.getClass());
                    }
                })
                .collect(Collectors.toList());
    }
}
