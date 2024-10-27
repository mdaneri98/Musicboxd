package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.SongReview;

import java.util.List;
import java.util.Optional;

public interface ReviewDao extends CrudDao<Review> {

    // Métodos específicos para ArtistReview
    Optional<ArtistReview> findArtistReviewById(long id);
    List<ArtistReview> findReviewsByArtistId(long artistId);
    Optional<ArtistReview> findArtistReviewByUserId(long userId, long artistId);
    ArtistReview saveArtistReview(ArtistReview review);

    // Métodos específicos para AlbumReview
    Optional<AlbumReview> findAlbumReviewById(long id);
    List<AlbumReview> findReviewsByAlbumId(long albumId);
    Optional<AlbumReview> findAlbumReviewByUserId(long userId, long albumId);
    AlbumReview saveAlbumReview(AlbumReview review);

    // Métodos específicos para SongReview
    Optional<SongReview> findSongReviewById(long id);
    List<SongReview> findReviewsBySongId(long songId);
    Optional<SongReview> findSongReviewByUserId(long userId, long songId);
    SongReview saveSongReview(SongReview review);


    // Métodos para likes
    List<User> likedBy(int page, int pageSize);
    void createLike(long userId, long reviewId);
    void deleteLike(long userId, long reviewId);
    void incrementLikes(long reviewId);
    void decrementLikes(long reviewId);
    boolean isLiked(Long userId, Long reviewId);

    // Métodos de paginación
    List<ArtistReview> findArtistReviewsPaginated(long artistId, int page, int pageSize);
    List<AlbumReview> findAlbumReviewsPaginated(long albumId, int page, int pageSize);
    List<SongReview> findSongReviewsPaginated(long songId, int page, int pageSize);

    List<Review> getPopularReviewsPaginated(int page, int pageSize);
    List<Review> getReviewsFromFollowedUsersPaginated(Long userId, int page, int pageSize);
    List<Review> findReviewsByUserPaginated(Long userId, int page, int pageSize);

    boolean isArtistReview(long reviewId);
    boolean isAlbumReview(long reviewId);
    boolean isSongReview(long reviewId);

    void block(Long reviewId);
    void unblock(Long reviewId);

    void updateCommentAmount(long reviewId);
}
