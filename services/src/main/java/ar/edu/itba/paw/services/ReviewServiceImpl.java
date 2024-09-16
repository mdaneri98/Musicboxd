package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.persistence.ReviewDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewDao reviewDao;

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao) {
        this.reviewDao = reviewDao;
    }

    @Override
    public Optional<Review> findById(long id) {
        return reviewDao.findById(id);
    }

    @Override
    public List<Review> findAll() {
        return reviewDao.findAll();
    }

    @Override
    public List<Review> findByUserId(long userId) {
        return reviewDao.findByUserId(userId);
    }

    @Override
    public int update(Review review) {
        return reviewDao.update(review);
    }

    @Override
    public int deleteById(long id) {
        return reviewDao.deleteById(id);
    }

    @Override
    public Optional<ArtistReview> findArtistReviewById(long id) {
        return reviewDao.findArtistReviewById(id);
    }

    @Override
    public List<ArtistReview> findReviewsByArtistId(long artistId) {
        return reviewDao.findReviewsByArtistId(artistId);
    }

    @Override
    public int saveArtistReview(ArtistReview review) {
        return reviewDao.saveArtistReview(review);
    }

    @Override
    public Optional<AlbumReview> findAlbumReviewById(long id) {
        return reviewDao.findAlbumReviewById(id);
    }

    @Override
    public List<AlbumReview> findReviewsByAlbumId(long albumId) {
        return reviewDao.findReviewsByAlbumId(albumId);
    }

    @Override
    public int saveAlbumReview(AlbumReview review) {
        return reviewDao.saveAlbumReview(review);
    }

    @Override
    public Optional<SongReview> findSongReviewById(long id) {
        return reviewDao.findSongReviewById(id);
    }

    @Override
    public List<SongReview> findReviewsBySongId(long songId) {
        return reviewDao.findReviewsBySongId(songId);
    }

    @Override
    public int saveSongReview(SongReview review) {
        return reviewDao.saveSongReview(review);
    }

    @Override
    public List<Review> findRecentReviews(int limit) {
        return reviewDao.findRecentReviews(limit);
    }

    @Override
    public List<Review> findMostLikedReviews(int limit) {
        return reviewDao.findMostLikedReviews(limit);
    }

    @Override
    public List<Review> findByRating(int rating) {
        return reviewDao.findByRating(rating);
    }

    @Override
    public int incrementLikes(long reviewId) {
        return reviewDao.incrementLikes(reviewId);
    }

    @Override
    public int decrementLikes(long reviewId) {
        return reviewDao.decrementLikes(reviewId);
    }

    @Override
    public List<Review> findAllPaginated(int page, int pageSize) {
        return reviewDao.findAllPaginated(page, pageSize);
    }

    @Override
    public List<ArtistReview> findArtistReviewsPaginated(long artistId, int page, int pageSize) {
        return reviewDao.findArtistReviewsPaginated(artistId, page, pageSize);
    }

    @Override
    public List<AlbumReview> findAlbumReviewsPaginated(long albumId, int page, int pageSize) {
        return reviewDao.findAlbumReviewsPaginated(albumId, page, pageSize);
    }

    @Override
    public List<SongReview> findSongReviewsPaginated(long songId, int page, int pageSize) {
        return reviewDao.findSongReviewsPaginated(songId, page, pageSize);
    }
}