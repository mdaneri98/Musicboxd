package ar.edu.itba.paw.domain.song;

import ar.edu.itba.paw.models.FilterType;

import java.util.List;
import java.util.Optional;

public interface SongRepository {
    Optional<Song> findById(SongId id);

    Song save(Song song);

    void delete(SongId id);

    List<Song> findAll(int page, int size);

    List<Song> findByArtistId(Long artistId, FilterType filterType, int page, int size);

    List<Song> findByAlbumId(Long albumId);

    List<Song> findByTitleContaining(String titleSubstring, int page, int size);

    Long countAll();

    boolean hasUserReviewed(Long userId, Long songId);

    boolean deleteReviewsFromSong(Long songId);

    boolean updateRating(Long songId, double newRating, int newRatingCount);

    void saveSongArtist(Long songId, Long artistId);
}
