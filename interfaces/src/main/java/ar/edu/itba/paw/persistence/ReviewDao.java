package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.SongReview;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReviewDao {
    // Métodos generales para todos los tipos de reviews
    int update(Review review);
    int deleteById(long id);

    // Métodos específicos para ArtistReview
    Optional<ArtistReview> findArtistReviewById(long id);
    List<ArtistReview> findReviewsByArtistId(long artistId);
    int saveArtistReview(ArtistReview review);

    // Métodos específicos para AlbumReview
    Optional<AlbumReview> findAlbumReviewById(long id);
    List<AlbumReview> findReviewsByAlbumId(long albumId);
    int saveAlbumReview(AlbumReview review);

    // Métodos específicos para SongReview
    Optional<SongReview> findSongReviewById(long id);
    List<SongReview> findReviewsBySongId(long songId);
    int saveSongReview(SongReview review);


    // Métodos para likes
    int createLike(long userId, long reviewId);
    int deleteLike(long userId, long reviewId);
    int incrementLikes(long reviewId);
    int decrementLikes(long reviewId);
    boolean isLiked(Long userId, Long reviewId);

    // Métodos de paginación
    List<ArtistReview> findArtistReviewsPaginated(long artistId, int page, int pageSize);
    List<AlbumReview> findAlbumReviewsPaginated(long albumId, int page, int pageSize);
    List<SongReview> findSongReviewsPaginated(long songId, int page, int pageSize);

    List<ArtistReview> findArtistReviewsByUser(long userId);
    List<AlbumReview> findAlbumReviewsByUser(long userId);
    List<SongReview> findSongReviewsByUser(long userId);

    List<ArtistReview> findPopularArtistReviewsSince(LocalDate date);
    List<AlbumReview> findPopularAlbumReviewsSince(LocalDate date);
    List<SongReview> findPopularSongReviewsSince(LocalDate date);

    List<ArtistReview> findArtistReviewsFromFollowedUsers(Long userId);
    List<AlbumReview> findAlbumReviewsFromFollowedUsers(Long userId);
    List<SongReview> findSongReviewsFromFollowedUsers(Long userId);

    boolean isArtistReview(long reviewId);
    boolean isAlbumReview(long reviewId);
    boolean isSongReview(long reviewId);

    boolean block(Long reviewId);
    boolean unblock(Long reviewId);
}