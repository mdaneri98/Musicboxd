package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Album;

import java.util.List;
import java.util.Optional;

public interface AlbumDao extends CrudDao<Album> {

    List<Album> findByArtistId(long id);
    List<Album> findByTitleContaining(String sub);

    void updateRating(long albumId, float newRating, int newRatingAmount);
    boolean hasUserReviewed(long userId, long albumId);

    Album saveX(Album album);
    Album updateX(Album album);

}
