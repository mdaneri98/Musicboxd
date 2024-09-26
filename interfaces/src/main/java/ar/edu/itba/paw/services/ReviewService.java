package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.SongReview;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    int update(Review review);
    int deleteById(long id);

    Optional<ArtistReview> findArtistReviewById(long id);
    int saveArtistReview(ArtistReview review);

    Optional<AlbumReview> findAlbumReviewById(long id);
    int saveAlbumReview(AlbumReview review);

    Optional<SongReview> findSongReviewById(long id);
    int saveSongReview(SongReview review);


    int createLike(long userId, long reviewId);
    int removeLike(long userId, long reviewId);
    boolean isLiked(long userId, long reviewId);

    List<Review> findReviewsByUserPaginated(long userId, int page, int pageSize, long loggedUserId);
    List<Review> getPopularReviewsNDaysPaginated(int days, int page, int pageSize, long loggedUserId);
    List<Review> getReviewsFromFollowedUsersPaginated(Long userId, int page, int pageSize, long loggedUserId);

    List<ArtistReview> findArtistReviewsPaginated(long artistId, int page, int pageSize, long loggedUserId);
    List<AlbumReview> findAlbumReviewsPaginated(long albumId, int page, int pageSize, long loggedUserId);
    List<SongReview> findSongReviewsPaginated(long songId, int page, int pageSize, long loggedUserId);

    boolean isArtistReview(long reviewId);
    boolean isAlbumReview(long reviewId);
    boolean isSongReview(long reviewId);

    boolean block(Long reviewId);
    boolean unblock(Long reviewId);

}