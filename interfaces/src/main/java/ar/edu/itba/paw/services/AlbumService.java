package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.reviews.Review;

import java.util.List;

public interface AlbumService extends CrudService<Album> {

    List<Album> findByArtistId(Long id);

    List<Album> findByTitleContaining(String sub, Integer page, Integer size);

    Boolean createAll(List<Album> albums, Artist artist);

    Boolean updateAll(List<Album> albums, Artist artist);

    Boolean hasUserReviewed(Long userId, Long albumId);

    List<Review> findReviewsByAlbumId(Long albumId);

    Boolean updateRating(Long albumId);
    
    Long countAll();
}
