package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.persistence.ReviewDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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
    public int saveArtistReview(ArtistReview review) {
        return reviewDao.saveArtistReview(review);
    }

    @Override
    public Optional<AlbumReview> findAlbumReviewById(long id) {
        return reviewDao.findAlbumReviewById(id);
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
    public int saveSongReview(SongReview review) {
        return reviewDao.saveSongReview(review);
    }

    @Override
    public int createLike(long userId, long reviewId) {
        reviewDao.createLike(userId, reviewId);
        return reviewDao.incrementLikes(reviewId);
    }

    @Override
    public int removeLike(long userId, long reviewId) {
        reviewDao.deleteLike(userId, reviewId);
        return reviewDao.decrementLikes(reviewId);
    }

    @Override
    public boolean isLiked(long userId, long reviewId) {
        return reviewDao.isLiked(userId, reviewId);
    }

    private <T extends Review> void setIsLiked(List<T> reviews, long userId) {
        for (T review : reviews) {
            review.setLiked(isLiked(userId, review.getId()));
        }
    }

    @Override
    public List<ArtistReview> findArtistReviewsPaginated(long artistId, int page, int pageSize, long loggedUserId) {
        List<ArtistReview> reviews = reviewDao.findArtistReviewsPaginated(artistId, page, pageSize);
        setIsLiked(reviews, loggedUserId);
        return reviews;
    }

    @Override
    public List<AlbumReview> findAlbumReviewsPaginated(long albumId, int page, int pageSize, long loggedUserId) {
        List<AlbumReview> reviews = reviewDao.findAlbumReviewsPaginated(albumId, page, pageSize);
        setIsLiked(reviews, loggedUserId);
        return reviews;
    }

    @Override
    public List<SongReview> findSongReviewsPaginated(long songId, int page, int pageSize, long loggedUserId) {
        List<SongReview> reviews = reviewDao.findSongReviewsPaginated(songId, page, pageSize);
        setIsLiked(reviews, loggedUserId);
        return reviews;
    }

    @Override
    public boolean isArtistReview(long reviewId) {
        return reviewDao.isArtistReview(reviewId);
    }

    @Override
    public boolean isAlbumReview(long reviewId) {
        return reviewDao.isAlbumReview(reviewId);
    }

    @Override
    public boolean isSongReview(long reviewId) {
        return reviewDao.isSongReview(reviewId);
    }

    @Override
    public List<Review> findReviewsByUserPaginated(long userId, int page, int pageSize, long loggedUserId) {
        List<Review> list = reviewDao.findReviewsByUserPaginated(userId, page, pageSize);
        setIsLiked(list, loggedUserId);
        return list;
    }

    @Override
    public List<Review> getPopularReviewsNDaysPaginated(int days, int page, int pageSize, long loggedUserId) {
        LocalDate nDaysAgo = LocalDate.now().minusDays(days);
        List<Review> list = reviewDao.getPopularReviewsSincePaginated(nDaysAgo,page, pageSize);
        setIsLiked(list, loggedUserId);
        return list;
    }

    @Override
    public List<Review> getReviewsFromFollowedUsersPaginated(Long userId, int page, int pageSize, long loggedUserId) {
        List<Review> list = reviewDao.getReviewsFromFollowedUsersPaginated(userId, page, pageSize);
        setIsLiked(list, loggedUserId);
        return list;
    }

    @Override
    public boolean block(Long reviewId) {
        return reviewDao.block(reviewId);
    }

    @Override
    public boolean unblock(Long reviewId) {
        return reviewDao.unblock(reviewId);
    }

}