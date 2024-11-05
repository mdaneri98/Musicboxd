package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.dtos.SongDTO;
import java.util.List;
import ar.edu.itba.paw.models.reviews.SongReview;

public interface SongService extends CrudService<Song> {

    List<Song> findByArtistId(long id, int pageNum, int pageSize);
    List<Song> findByAlbumId(long id);
    List<Song> findByTitleContaining(String sub);
    List<SongReview> findReviewsBySongId(long songId);
    Song create(SongDTO songDTO, Album album);
    boolean createAll(List<SongDTO> songsDTO, Album album);
    Song update(SongDTO songDTO, Album album);
    boolean updateAll(List<SongDTO> songsDTO, Album album);
    boolean updateRating(long songId, Double newRating, int newRatingAmount);
    boolean hasUserReviewed(long userId, long songId);
    boolean deleteReviewsFromSong(long id);
}
