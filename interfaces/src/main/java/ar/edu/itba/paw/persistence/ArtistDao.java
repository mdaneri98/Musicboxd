package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;

import java.util.List;
import java.util.Optional;

public interface ArtistDao {

    Optional<Artist> findById(long id);
    List<Artist> findAll();
    List<Artist> findBySongId(long id);
    List<Artist> findByNameContaining(String sub);

    void updateRating(long artistId, float newRating, int newRatingAmount);
    boolean hasUserReviewed(long userId, long artistId);

    long save(Artist artist);
    int update(Artist artist);
    int deleteById(long id);
}

