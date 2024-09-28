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
    Optional<ArtistReview> findArtistReviewByUserId(long userId, long artistId);
    int saveArtistReview(ArtistReview review);

    Optional<AlbumReview> findAlbumReviewById(long id);
    Optional<AlbumReview> findAlbumReviewByUserId(long userId, long albumId);
    int saveAlbumReview(AlbumReview review);

    Optional<SongReview> findSongReviewById(long id);
    Optional<SongReview> findSongReviewByUserId(long userId, long songId);
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

    boolean hasUserReviewedArtist(long userId, long artistId);
    boolean hasUserReviewedAlbum(long userId, long albumId);
    boolean hasUserReviewedSong(long userId, long songId);
    int updateAvgRatingForAll();

    boolean isArtistReview(long reviewId);
    boolean isAlbumReview(long reviewId);
    boolean isSongReview(long reviewId);

    boolean block(Long reviewId);
    boolean unblock(Long reviewId);

}