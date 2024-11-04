package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import java.util.List;
import ar.edu.itba.paw.models.reviews.SongReview;

public interface SongDao extends CrudDao<Song> {

    List<Song> findByArtistId(long id);
    List<Song> findByAlbumId(long id);
    List<Song> findByTitleContaining(String sub);

    int saveSongArtist(Song song, Artist artist);
    boolean updateRating(long songId, Double newRating, int newRatingAmount);
    boolean hasUserReviewed(long userId, long songId);

    boolean deleteReviewsFromSong(long songId);
    List<SongReview> findReviewsBySongId(long songId);
}

