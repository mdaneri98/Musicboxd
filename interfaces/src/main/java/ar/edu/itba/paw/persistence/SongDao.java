package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import java.util.List;

public interface SongDao extends CrudDao<Song> {

    List<Song> findByArtistId(long id);
    List<Song> findByAlbumId(long id);
    List<Song> findByTitleContaining(String sub);

    int saveSongArtist(Song song, Artist artist);
    boolean updateRating(long songId, double newRating, int newRatingAmount);
    boolean hasUserReviewed(long userId, long songId);

    boolean deleteReviewsFromSong(long songId);
}

