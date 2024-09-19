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
    List<ArtistReview> findReviewsByArtistId(long artistId);
    int saveArtistReview(ArtistReview review);

    Optional<AlbumReview> findAlbumReviewById(long id);
    List<AlbumReview> findReviewsByAlbumId(long albumId);
    int saveAlbumReview(AlbumReview review);

    Optional<SongReview> findSongReviewById(long id);
    List<SongReview> findReviewsBySongId(long songId);
    int saveSongReview(SongReview review);


    int incrementLikes(long reviewId);
    int decrementLikes(long reviewId);

    List<Review> findReviewsByUserPaginated(long userId, int page, int pageSize);
    List<ArtistReview> findArtistReviewsPaginated(long artistId, int page, int pageSize);
    List<AlbumReview> findAlbumReviewsPaginated(long albumId, int page, int pageSize);
    List<SongReview> findSongReviewsPaginated(long songId, int page, int pageSize);
}