package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ar.edu.itba.paw.services.utils.TimeUtils;
import org.springframework.context.MessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;


@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewDao reviewDao;
    private final SongService songService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final UserService userService;

    @Autowired
        public ReviewServiceImpl(ReviewDao reviewDao, SongService songService, ArtistService artistService, AlbumService albumService, UserService userService, MessageSource messageSource) {
        this.reviewDao = reviewDao;
        this.songService = songService;
        this.artistService = artistService;
        this.albumService = albumService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public int updateAvgRatingForAll(){
        LOGGER.info("Updating average ratings for all songs, albums, and artists");
        List<Song> songs = songService.findAll();
        List<Album> albums = albumService.findAll();
        List<Artist> artists = artistService.findAll();
        for (Song song : songs) {
            updateSongRating(song.getId());
        }
        for (Album album : albums) {
            updateAlbumRating(album.getId());
        }
        for (Artist artist : artists) {
            updateArtistRating(artist.getId());
        }
        LOGGER.info("Finished updating average ratings for all songs, albums, and artists");
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Review> find(long id) {
        return reviewDao.find(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findAll() {
        throw new RuntimeException("Method not allowed");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findPaginated(FilterType filterType, int page, int pageSize) {
        return reviewDao.findPaginated(filterType, page, pageSize);
    }

    @Override
    @Transactional
    public Review create(Review entity) {
        LOGGER.info("Creating new review: {}", entity);
        Review createdReview = reviewDao.create(entity);
        LOGGER.info("Review created successfully with ID: {}", createdReview.getId());
        return createdReview;
    }

    @Override
    @Transactional
    public Review update(Review review) {
        LOGGER.info("Updating review with ID: {}", review.getId());
        Review updatedReview = reviewDao.update(review);
        LOGGER.info("Review updated successfully");
        return updatedReview;
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        LOGGER.info("Attempting to delete review with ID: {}", id);
        boolean res = false;
        if (isArtistReview(id)) {
            ArtistReview r = reviewDao.findArtistReviewById(id).get();
            res = reviewDao.delete(id);
            updateArtistRating(r.getArtist().getId());
        }
        if (isAlbumReview(id)) {
            AlbumReview r = reviewDao.findAlbumReviewById(id).get();
            res = reviewDao.delete(id);
            updateAlbumRating(r.getAlbum().getId());
        }
        if (isSongReview(id)) {
            SongReview r = reviewDao.findSongReviewById(id).get();
            res = reviewDao.delete(id);
            updateSongRating(r.getSong().getId());
        }
        if (res) {
            LOGGER.info("Review with ID: {} deleted successfully", id);
        } else {
            LOGGER.warn("Failed to delete review with ID: {}", id);
        }
        return res;
    }

    @Override
    public List<User> likedBy(int page, int pageSize) {
        return reviewDao.likedBy(page, pageSize);
    }

    @Override
    @Transactional
    public boolean deleteReview(Review review, long userId) {
        LOGGER.info("Attempting to delete review with ID: {} for user ID: {}", review.getId(), userId);
        boolean res = delete(review.getId());
        if (res) {
            updateUserReviewAmount(userId);
            LOGGER.info("Review deleted and user review amount updated for user ID: {}", userId);
        }
        return res;
    }

    @Override
    @Transactional
    public Review updateArtistReview(ArtistReview review) {
        LOGGER.info("Updating artist review with ID: {}", review.getId());
        Review r = reviewDao.update(review);
        updateArtistRating(review.getArtist().getId());
        LOGGER.info("Artist review updated and artist rating recalculated for artist ID: {}", review.getArtist().getId());
        return r;
    }

    @Override
    @Transactional
    public Review updateAlbumReview(AlbumReview review) {
        LOGGER.info("Updating album review with ID: {}", review.getId());
        Review r = reviewDao.update(review);
        updateAlbumRating(review.getAlbum().getId());
        LOGGER.info("Album review updated and album rating recalculated for album ID: {}", review.getAlbum().getId());
        return r;
    }

    @Override
    @Transactional
    public Review updateSongReview(SongReview review) {
        LOGGER.info("Updating song review with ID: {}", review.getId());
        Review r = reviewDao.update(review);
        updateSongRating(review.getSong().getId());
        LOGGER.info("Song review updated and song rating recalculated for song ID: {}", review.getSong().getId());
        return r;
    }

    @Override
    @Transactional(readOnly = true)
    public ArtistReview findArtistReviewById(long id, long loggedUserId) {
        ArtistReview review = reviewDao.findArtistReviewById(id).get();
        review.setTimeAgo(TimeUtils.formatTimeAgo(review.getCreatedAt()));
        review.setLiked(isLiked(loggedUserId, review.getId()));
        return review;
    }

    @Override
    @Transactional
    public ArtistReview saveArtistReview(ArtistReview review) {
        LOGGER.info("Saving new artist review for artist ID: {}", review.getArtist().getId());
        ArtistReview result = reviewDao.saveArtistReview(review);
        updateUserReviewAmount(review.getUser().getId());
        updateArtistRating(review.getArtist().getId());
        LOGGER.info("Artist review saved, user review amount updated, and artist rating recalculated");
        return result;
    }

    @Override
    @Transactional
    public void updateArtistRating(long artistId) {
        LOGGER.info("Updating rating for artist ID: {}", artistId);
        List<ArtistReview> reviews = reviewDao.findReviewsByArtistId(artistId);
        Double avgRating = reviews.stream().mapToInt(ArtistReview::getRating).average().orElse(0.0);
        Double roundedAvgRating = Math.round(avgRating * 100.0) / 100.0;
        Integer ratingAmount = reviews.size();
        artistService.updateRating(artistId, roundedAvgRating, ratingAmount);
        LOGGER.info("Artist rating updated. New average rating: {}, Total reviews: {}", roundedAvgRating, ratingAmount);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReviewedArtist(long userId, long artistId) {
        return artistService.hasUserReviewed(userId, artistId);
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumReview findAlbumReviewById(long id, long loggedUserId) {
        AlbumReview review = reviewDao.findAlbumReviewById(id).get();
        review.setTimeAgo(TimeUtils.formatTimeAgo(review.getCreatedAt()));
        review.setLiked(isLiked(loggedUserId, review.getId()));
        return review;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ArtistReview> findArtistReviewByUserId(long userId, long artistId, long loggedUserId) {
        Optional<ArtistReview> reviewOptional = reviewDao.findArtistReviewByUserId(userId, artistId);
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setTimeAgo(TimeUtils.formatTimeAgo(review.getCreatedAt()));
            review.setLiked(isLiked(loggedUserId, review.getId()));
        }
        return reviewOptional;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AlbumReview> findAlbumReviewByUserId(long userId, long albumId, long loggedUserId) {
        Optional<AlbumReview> reviewOptional = reviewDao.findAlbumReviewByUserId(userId, albumId);
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setTimeAgo(TimeUtils.formatTimeAgo(review.getCreatedAt()));
            review.setLiked(isLiked(loggedUserId, review.getId()));
        }
        return reviewOptional;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SongReview> findSongReviewByUserId(long userId, long songId, long loggedUserId) {
        Optional<SongReview> reviewOptional = reviewDao.findSongReviewByUserId(userId, songId);
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setTimeAgo(TimeUtils.formatTimeAgo(review.getCreatedAt()));
            review.setLiked(isLiked(loggedUserId, review.getId()));
        }
        return reviewOptional;
    }

    @Override
    @Transactional
    public AlbumReview saveAlbumReview(AlbumReview review) {
        LOGGER.info("Saving new album review for album ID: {}", review.getAlbum().getId());
        AlbumReview result = reviewDao.saveAlbumReview(review);
        updateUserReviewAmount(review.getUser().getId());
        updateAlbumRating(review.getAlbum().getId());
        LOGGER.info("Album review saved, user review amount updated, and album rating recalculated");
        return result;
    }

    @Override
    @Transactional
    public void updateAlbumRating(long albumId) {
        LOGGER.info("Updating rating for album ID: {}", albumId);
        List<AlbumReview> reviews = reviewDao.findReviewsByAlbumId(albumId);
        Double avgRating = reviews.stream().mapToInt(AlbumReview::getRating).average().orElse(0.0);
        Double roundedAvgRating = Math.round(avgRating * 100.0) / 100.0;
        int ratingAmount = reviews.size();
        albumService.updateRating(albumId, roundedAvgRating, ratingAmount);
        LOGGER.info("Album rating updated. New average rating: {}, Total reviews: {}", roundedAvgRating, ratingAmount);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReviewedAlbum(long userId, long albumId) {
        return albumService.hasUserReviewed(userId, albumId);
    }

    @Override
    @Transactional(readOnly = true)
    public SongReview findSongReviewById(long id, long loggedUserId) {
        SongReview review = reviewDao.findSongReviewById(id).get();
        review.setTimeAgo(TimeUtils.formatTimeAgo(review.getCreatedAt()));
        review.setLiked(isLiked(loggedUserId, review.getId()));
        return review;
    }


    @Override
    @Transactional
    public SongReview saveSongReview(SongReview review) {
        LOGGER.info("Saving new song review for song ID: {}", review.getSong().getId());
        SongReview result = reviewDao.saveSongReview(review);
        updateUserReviewAmount(review.getUser().getId());
        updateSongRating(review.getSong().getId());
        LOGGER.info("Song review saved, user review amount updated, and song rating recalculated");
        return result;
    }

    @Override
    @Transactional
    public void updateUserReviewAmount(long userId) {
        LOGGER.info("Updating review amount for user ID: {}", userId);
        userService.updateUserReviewAmount(userId);
        LOGGER.info("User review amount updated for user ID: {}", userId);
    }

    @Override
    @Transactional
    public void updateSongRating(long songId) {
        LOGGER.info("Updating rating for song ID: {}", songId);
        List<SongReview> reviews = reviewDao.findReviewsBySongId(songId);
        Double avgRating = reviews.stream().mapToInt(SongReview::getRating).average().orElse(0.0);
        Double roundedAvgRating = Math.round(avgRating * 100.0) / 100.0;
        int ratingAmount = reviews.size();
        songService.updateRating(songId, roundedAvgRating, ratingAmount);
        LOGGER.info("Song rating updated. New average rating: {}, Total reviews: {}", roundedAvgRating, ratingAmount);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReviewedSong(long userId, long songId) {
        return songService.hasUserReviewed(userId, songId);
    }

    @Override
    @Transactional
    public void createLike(long userId, long reviewId) {
        LOGGER.info("Creating like for review ID: {} by user ID: {}", reviewId, userId);
        reviewDao.createLike(userId, reviewId);
        reviewDao.updateLikeCount(reviewId);
        LOGGER.info("Like created and like count incremented for review ID: {}", reviewId);
    }

    @Override
    @Transactional
    public void removeLike(long userId, long reviewId) {
        LOGGER.info("Removing like for review ID: {} by user ID: {}", reviewId, userId);
        reviewDao.deleteLike(userId, reviewId);
        reviewDao.updateLikeCount(reviewId);
        LOGGER.info("Like removed and like count decremented for review ID: {}", reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLiked(long userId, long reviewId) {
        return reviewDao.isLiked(userId, reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArtistReview> findArtistReviewsPaginated(long artistId, int page, int pageSize, long loggedUserId) {
        List<ArtistReview> reviews = reviewDao.findArtistReviewsPaginated(artistId, page, pageSize);
        setIsLiked(reviews, loggedUserId);
        setTimeAgo(reviews);
        return reviews;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlbumReview> findAlbumReviewsPaginated(long albumId, int page, int pageSize, long loggedUserId) {
        List<AlbumReview> reviews = reviewDao.findAlbumReviewsPaginated(albumId, page, pageSize);
        setIsLiked(reviews, loggedUserId);
        setTimeAgo(reviews);
        return reviews;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SongReview> findSongReviewsPaginated(long songId, int page, int pageSize, long loggedUserId) {
        List<SongReview> reviews = reviewDao.findSongReviewsPaginated(songId, page, pageSize);
        setIsLiked(reviews, loggedUserId);
        setTimeAgo(reviews);
        return reviews;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isArtistReview(long reviewId) {
        return reviewDao.isArtistReview(reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAlbumReview(long reviewId) {
        return reviewDao.isAlbumReview(reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSongReview(long reviewId) {
        return reviewDao.isSongReview(reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findReviewsByUserPaginated(long userId, int page, int pageSize, long loggedUserId) {
        List<Review> list = reviewDao.findReviewsByUserPaginated(userId, page, pageSize);
        setIsLiked(list, loggedUserId);
        setTimeAgo(list);
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getPopularReviewsPaginated(int page, int pageSize, long loggedUserId) {
        List<Review> list = reviewDao.getPopularReviewsPaginated(page, pageSize);
        setIsLiked(list, loggedUserId);
        setTimeAgo(list);
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsFromFollowedUsersPaginated(Long userId, int page, int pageSize, long loggedUserId) {
        List<Review> list = reviewDao.getReviewsFromFollowedUsersPaginated(userId, page, pageSize);
        setIsLiked(list, loggedUserId);
        setTimeAgo(list);
        return list;
    }

    @Override
    @Transactional
    public void block(Long reviewId) {
        LOGGER.info("Blocking review with ID: {}", reviewId);
        reviewDao.block(reviewId);
        LOGGER.info("Review blocked successfully: {}", reviewId);
    }

    @Override
    @Transactional
    public void unblock(Long reviewId) {
        LOGGER.info("Unblocking review with ID: {}", reviewId);
        reviewDao.unblock(reviewId);
        LOGGER.info("Review unblocked successfully: {}", reviewId);
    }

    private <T extends Review> void setTimeAgo(List<T> reviews) {
        for (T review : reviews) {
            review.setTimeAgo(TimeUtils.formatTimeAgo(review.getCreatedAt()));
        }
    }

    private <T extends Review> void setIsLiked(List<T> reviews, long userId) {
        for (T review : reviews) {
            if (userId < 1)
                review.setLiked(false);
            else
                review.setLiked(isLiked(userId, review.getId()));
        }
    }
}
