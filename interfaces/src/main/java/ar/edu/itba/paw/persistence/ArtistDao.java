package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;

import java.util.List;
import java.util.Optional;

public interface ArtistDao extends CrudDao<Artist> {

    List<Artist> findBySongId(long id);
    List<Artist> findByNameContaining(String sub);

    boolean updateRating(long artistId, float newRating, int newRatingAmount);
    boolean hasUserReviewed(long userId, long artistId);

}

