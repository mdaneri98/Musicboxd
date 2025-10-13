package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import java.util.List;
import ar.edu.itba.paw.models.reviews.SongReview;

public interface SongDao extends CrudDao<Song> {

    List<Song> findByArtistId(Long id, Integer pageNum, Integer pageSize);
    List<Song> findByAlbumId(Long id);
    List<Song> findByTitleContaining(String sub);

    Integer saveSongArtist(Song song, Artist artist);
    Boolean updateRating(Long songId, Double newRating, Integer newRatingAmount);
    Boolean hasUserReviewed(Long userId, Long songId);

    Boolean deleteReviewsFromSong(Long songId);
    List<SongReview> findReviewsBySongId(Long songId);
    
    // Count methods for pagination
    Long countAll();
}

