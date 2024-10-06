package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.SongReview;

import java.util.List;
import java.util.Optional;

public interface ReviewService extends CrudService<Review> {

    Optional<ArtistReview> findArtistReviewById(long id);
    Optional<ArtistReview> findArtistReviewByUserId(long userId, long artistId);
    ArtistReview saveArtistReview(ArtistReview review);

    Optional<AlbumReview> findAlbumReviewById(long id);
    Optional<AlbumReview> findAlbumReviewByUserId(long userId, long albumId);
    AlbumReview saveAlbumReview(AlbumReview review);

    Optional<SongReview> findSongReviewById(long id);
    Optional<SongReview> findSongReviewByUserId(long userId, long songId);
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
}