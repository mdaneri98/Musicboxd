package ar.edu.itba.paw.services;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.models.FilterType;

import java.util.List;
import java.util.Optional;

public interface ReviewService extends CrudService<Review> {

    boolean deleteReview(Review review, long userId);

    ArtistReview findArtistReviewById(long id, long loggedUserId);
    ArtistReview findArtistReviewByUserId(long userId, long artistId, long loggedUserId);
    ArtistReview saveArtistReview(ArtistReview review);

    AlbumReview findAlbumReviewById(long id, long loggedUserId);
    AlbumReview findAlbumReviewByUserId(long userId, long albumId, long loggedUserId);
    AlbumReview saveAlbumReview(AlbumReview review);

    SongReview findSongReviewById(long id, long loggedUserId);
    SongReview findSongReviewByUserId(long userId, long songId, long loggedUserId);
    SongReview saveSongReview(SongReview review);

    void createLike(long userId, long reviewId);
    void removeLike(long userId, long reviewId);
    boolean isLiked(long userId, long reviewId);

    List<Review> findReviewsByUserPaginated(long userId, int page, int pageSize, long loggedUserId);
    List<Review> getPopularReviewsPaginated(int page, int pageSize, long loggedUserId);
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

    void block(Long reviewId);
    void unblock(Long reviewId);

    void updateUserReviewAmount(long userId);
    void updateSongRating(long songId);
    void updateAlbumRating(long albumId);
    void updateArtistRating(long artistId);
    Review updateSongReview(SongReview review);
    Review updateArtistReview(ArtistReview review);
    Review updateAlbumReview(AlbumReview review);

    List<Review> findPaginated(FilterType filterType, int page, int pageSize);
}