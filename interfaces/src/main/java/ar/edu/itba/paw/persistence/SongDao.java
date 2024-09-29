package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Song;

import java.util.List;
import java.util.Optional;

public interface SongDao extends CrudDao<Song> {

    List<Song> findByArtistId(long id);
    List<Song> findByAlbumId(long id);
    List<Song> findByTitleContaining(String sub);

    void updateRating(long songId, float newRating, int newRatingAmount);
    boolean hasUserReviewed(long userId, long songId);

}

