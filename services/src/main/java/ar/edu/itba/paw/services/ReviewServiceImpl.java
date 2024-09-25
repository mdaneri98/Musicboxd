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

    private List<ArtistReview> setIsLikedArtistReview(List<ArtistReview> reviews, long userId) {
        for (Review review : reviews) {
            review.setLiked(isLiked(userId, review.getId()));
        }
        return reviews;
    }

    private List<AlbumReview> setIsLikedAlbumReview(List<AlbumReview> reviews, long userId) {
        for (Review review : reviews) {
            review.setLiked(isLiked(userId, review.getId()));
        }
        return reviews;
    }

    private List<SongReview> setIsLikedSongReview(List<SongReview> reviews, long userId) {
        for (Review review : reviews) {
            review.setLiked(isLiked(userId, review.getId()));
        }
        return reviews;
    }

    @Override
    public List<ArtistReview> findArtistReviewsPaginated(long artistId, int page, int pageSize, long loggedUserId) {
        List<ArtistReview> reviews = reviewDao.findArtistReviewsPaginated(artistId, page, pageSize);
        setIsLikedArtistReview(reviews, loggedUserId);
        return reviews;
    }

    @Override
    public List<AlbumReview> findAlbumReviewsPaginated(long albumId, int page, int pageSize, long loggedUserId) {
        List<AlbumReview> reviews = reviewDao.findAlbumReviewsPaginated(albumId, page, pageSize);
        setIsLikedAlbumReview(reviews, loggedUserId);
        return reviews;
    }

    @Override
    public List<SongReview> findSongReviewsPaginated(long songId, int page, int pageSize, long loggedUserId) {
        List<SongReview> reviews = reviewDao.findSongReviewsPaginated(songId, page, pageSize);
        setIsLikedSongReview(reviews, loggedUserId);
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
        List<Review> allReviews = new ArrayList<>();

        // Obtener las reviews de artistas
        List<ArtistReview> artistReviews = reviewDao.findArtistReviewsByUser(userId);
        setIsLikedArtistReview(artistReviews, loggedUserId);
        List<AlbumReview> albumReviews = reviewDao.findAlbumReviewsByUser(userId);
        setIsLikedAlbumReview(albumReviews, loggedUserId);
        List<SongReview> songReviews = reviewDao.findSongReviewsByUser(userId);
        setIsLikedSongReview(songReviews, loggedUserId);

        allReviews.addAll(artistReviews);
        allReviews.addAll(albumReviews);
        allReviews.addAll(songReviews);

        allReviews.sort((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()));

        // Aplicar la paginación
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, allReviews.size());

        return allReviews.subList(start, end);
    }

    @Override
    public List<Review> getPopularReviewsNDaysPaginated(int days, int page, int pageSize, long loggedUserId) {
        List<Review> allReviews = new ArrayList<>();
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(days);

        // Obtener las reviews de artistas
        List<ArtistReview> artistReviews = reviewDao.findPopularArtistReviewsSince(thirtyDaysAgo);
        setIsLikedArtistReview(artistReviews, loggedUserId);
        List<AlbumReview> albumReviews = reviewDao.findPopularAlbumReviewsSince(thirtyDaysAgo);
        setIsLikedAlbumReview(albumReviews, loggedUserId);
        List<SongReview> songReviews = reviewDao.findPopularSongReviewsSince(thirtyDaysAgo);
        setIsLikedSongReview(songReviews, loggedUserId);

        allReviews.addAll(artistReviews);
        allReviews.addAll(albumReviews);
        allReviews.addAll(songReviews);

        allReviews.sort((r1, r2) -> r2.getLikes().compareTo(r1.getLikes()));

        // Aplicar la paginación
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, allReviews.size());

        if (start < end) return allReviews.subList(start, end);
        return allReviews;
    }

    @Override
    public List<Review> getReviewsFromFollowedUsersPaginated(Long userId, int page, int pageSize, long loggedUserId) {
        List<Review> allReviews = new ArrayList<>();

        // Obtener las reviews de artistas
        List<ArtistReview> artistReviews = reviewDao.findArtistReviewsFromFollowedUsers(userId);
        setIsLikedArtistReview(artistReviews, loggedUserId);
        List<AlbumReview> albumReviews = reviewDao.findAlbumReviewsFromFollowedUsers(userId);
        setIsLikedAlbumReview(albumReviews, loggedUserId);
        List<SongReview> songReviews = reviewDao.findSongReviewsFromFollowedUsers(userId);
        setIsLikedSongReview(songReviews, loggedUserId);

        allReviews.addAll(artistReviews);
        allReviews.addAll(albumReviews);
        allReviews.addAll(songReviews);

        allReviews.sort((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()));

        // Aplicar la paginación
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, allReviews.size());

        if (start < end) return allReviews.subList(start, end);
        return allReviews;
    }
}