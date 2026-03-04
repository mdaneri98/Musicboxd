package ar.edu.itba.paw.services;

import ar.edu.itba.paw.domain.user.User;
import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.user.UserRepository;
import ar.edu.itba.paw.domain.artist.ArtistId;
import ar.edu.itba.paw.domain.artist.ArtistRepository;
import ar.edu.itba.paw.domain.album.AlbumId;
import ar.edu.itba.paw.domain.album.AlbumRepository;
import ar.edu.itba.paw.usecases.user.UpdateUserReviewCount;
import ar.edu.itba.paw.usecases.artist.UpdateArtistRating;
import ar.edu.itba.paw.usecases.album.UpdateAlbumRating;
import ar.edu.itba.paw.usecases.song.UpdateSongRating;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.models.reviews.ReviewType;
import ar.edu.itba.paw.persistence.*;
import ar.edu.itba.paw.exception.email.AcknowledgementEmailException;
import ar.edu.itba.paw.exception.conflict.UserAlreadyReviewedException;
import ar.edu.itba.paw.exception.not_found.ReviewNotFoundException;
import ar.edu.itba.paw.exception.not_found.UserNotFoundException;
import ar.edu.itba.paw.exception.UnkownReviewTypeException;
import ar.edu.itba.paw.exception.not_found.ArtistNotFoundException;
import ar.edu.itba.paw.exception.not_found.AlbumNotFoundException;
import ar.edu.itba.paw.exception.not_found.SongNotFoundException;
import ar.edu.itba.paw.exception.conflict.LikeAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewDao reviewDao;
    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final ar.edu.itba.paw.domain.song.SongRepository songRepository;
    private final UpdateUserReviewCount updateUserReviewCount;
    private final UpdateArtistRating updateArtistRating;
    private final UpdateAlbumRating updateAlbumRating;
    private final UpdateSongRating updateSongRating;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao, UserRepository userRepository, ArtistRepository artistRepository, AlbumRepository albumRepository,
            ar.edu.itba.paw.domain.song.SongRepository songRepository,
            UpdateUserReviewCount updateUserReviewCount, UpdateArtistRating updateArtistRating,
            UpdateAlbumRating updateAlbumRating, UpdateSongRating updateSongRating,
            EmailService emailService, NotificationService notificationService) {
        this.reviewDao = reviewDao;
        this.userRepository = userRepository;
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.songRepository = songRepository;
        this.updateUserReviewCount = updateUserReviewCount;
        this.updateArtistRating = updateArtistRating;
        this.updateAlbumRating = updateAlbumRating;
        this.updateSongRating = updateSongRating;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findBySubstring(String substring, Integer page, Integer size) {
        return reviewDao.findBySubstring(substring, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public Review findById(Long id) {
        return reviewDao.findById(id).orElseThrow(() -> new ReviewNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findAll() {
        return reviewDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findPaginated(FilterType filterType, Integer page, Integer pageSize) {
        return reviewDao.findPaginated(filterType, page, pageSize);
    }

    @Override
    @Transactional
    public Review create(Review reviewInput) {
        LOGGER.info("Creating new review: {}", reviewInput);
        reviewInput.setLikes(0);
        reviewInput.setBlocked(false);
        Review createdReview = switch (reviewInput.getItemType()) {
            case ARTIST -> createArtistReview(reviewInput);
            case ALBUM -> createAlbumReview(reviewInput);
            case SONG -> createSongReview(reviewInput);
            default -> throw new UnkownReviewTypeException(reviewInput.getItemType().toString());
        };
        updateUserReviewCount.increment(createdReview.getUserId());
        updateRatingForItem(createdReview);
        return createdReview;
    }

    @Override
    public Review createArtistReview(Review reviewInput) {
        ArtistReview entity = new ArtistReview();
        entity.setUserId(reviewInput.getUserId());

        ArtistId artistId = new ArtistId(reviewInput.getItemId());
        artistRepository.findById(artistId)
                .orElseThrow(() -> new ArtistNotFoundException(reviewInput.getItemId()));

        ar.edu.itba.paw.infrastructure.jpa.ArtistJpaEntity artistEntity = new ar.edu.itba.paw.infrastructure.jpa.ArtistJpaEntity();
        artistEntity.setId(reviewInput.getItemId());
        entity.setArtist(artistEntity);

        if (artistRepository.hasUserReviewed(reviewInput.getUserId(), artistId)) {
            throw new UserAlreadyReviewedException(reviewInput.getUserId(), reviewInput.getItemId(),
                    ReviewType.ARTIST.toString());
        }
        mergeReviewFields(entity, reviewInput);
        Review createdReview = reviewDao.create(entity);
        notificationService.notifyNewReview(createdReview);
        LOGGER.info("Review created successfully with ID: {}", createdReview.getId());
        return createdReview;
    }

    @Override
    public Review createAlbumReview(Review reviewInput) {
        AlbumReview entity = new AlbumReview();
        entity.setUserId(reviewInput.getUserId());

        AlbumId albumId = new AlbumId(reviewInput.getItemId());
        albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumNotFoundException(reviewInput.getItemId()));

        ar.edu.itba.paw.infrastructure.jpa.AlbumJpaEntity albumEntity = new ar.edu.itba.paw.infrastructure.jpa.AlbumJpaEntity();
        albumEntity.setId(reviewInput.getItemId());
        entity.setAlbum(albumEntity);

        if (albumRepository.hasUserReviewed(reviewInput.getUserId(), albumId)) {
            throw new UserAlreadyReviewedException(reviewInput.getUserId(), reviewInput.getItemId(), "Album");
        }
        mergeReviewFields(entity, reviewInput);
        Review createdReview = reviewDao.create(entity);
        notificationService.notifyNewReview(createdReview);
        LOGGER.info("Review created successfully with ID: {}", createdReview.getId());
        return createdReview;
    }

    @Override
    public Review createSongReview(Review reviewInput) {
        SongReview entity = new SongReview();
        entity.setUserId(reviewInput.getUserId());

        ar.edu.itba.paw.domain.song.SongId songId = new ar.edu.itba.paw.domain.song.SongId(reviewInput.getItemId());
        songRepository.findById(songId)
                .orElseThrow(() -> new SongNotFoundException(reviewInput.getItemId()));

        ar.edu.itba.paw.infrastructure.jpa.SongJpaEntity songEntity = new ar.edu.itba.paw.infrastructure.jpa.SongJpaEntity();
        songEntity.setId(reviewInput.getItemId());
        entity.setSong(songEntity);

        if (songRepository.hasUserReviewed(reviewInput.getUserId(), reviewInput.getItemId())) {
            throw new UserAlreadyReviewedException(reviewInput.getUserId(), reviewInput.getItemId(), "Song");
        }
        mergeReviewFields(entity, reviewInput);
        Review createdReview = reviewDao.create(entity);
        notificationService.notifyNewReview(createdReview);
        LOGGER.info("Review created successfully with ID: {}", createdReview.getId());
        return createdReview;
    }

    private void mergeReviewFields(Review target, Review source) {
        if (source.getTitle() != null)
            target.setTitle(source.getTitle());
        if (source.getDescription() != null)
            target.setDescription(source.getDescription());
        if (source.getRating() != null)
            target.setRating(source.getRating());
        target.setCreatedAt(LocalDateTime.now());
        if (source.getLikes() != null)
            target.setLikes(source.getLikes());
        if (source.isBlocked() != null)
            target.setBlocked(source.isBlocked());
        if (source.getCommentAmount() != null)
            target.setCommentAmount(source.getCommentAmount());
    }


    private void updateArtistRating(Long artistId) {
        LOGGER.info("Updating rating for artist ID: {}", artistId);
        updateArtistRating.execute(artistId);
        LOGGER.info("Artist rating updated successfully");
    }

    private void updateAlbumRating(Long albumId) {
        LOGGER.info("Updating rating for album ID: {}", albumId);
        updateAlbumRating.execute(albumId);
        LOGGER.info("Album rating updated successfully");
    }

    private void updateSongRating(Long songId) {
        LOGGER.info("Updating rating for song ID: {}", songId);
        updateSongRating.execute(songId);
        LOGGER.info("Song rating updated successfully");
    }

    @Override
    @Transactional
    public Review update(Review reviewInput) {
        LOGGER.info("Updating review with ID: {}", reviewInput.getId());
        Review reviewEntity = reviewDao.findById(reviewInput.getId())
                .orElseThrow(() -> new ReviewNotFoundException(reviewInput.getId()));

        Boolean oldBlockedStatus = reviewEntity.isBlocked();

        mergeReviewFields(reviewEntity, reviewInput);
        Review updatedReview = reviewDao.update(reviewEntity);

        Notification.NotificationType notificationType = blockStatusChanged(oldBlockedStatus,
                updatedReview.isBlocked());
        if (notificationType != null) {
            notificationService.notifyReviewBlockStatusChange(updatedReview, notificationType);
        } else {
            notificationService.notifyNewReview(updatedReview);
        }

        updateRatingForItem(updatedReview);
        LOGGER.info("Review updated successfully");
        return updatedReview;
    }

    @Transactional
    public void updateRatingForItem(Review review) {
        switch (review.getItemType()) {
            case ARTIST:
                updateArtistRating(review.getItemId());
                break;
            case ALBUM:
                updateAlbumRating(review.getItemId());
                break;
            case SONG:
                updateSongRating(review.getItemId());
                break;
            default:
                throw new UnkownReviewTypeException(review.getItemType().toString());
        }
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        LOGGER.info("Attempting to delete review with ID: {}", id);
        Review review = reviewDao.findById(id).orElseThrow(() -> new ReviewNotFoundException(id));
        Boolean res = reviewDao.delete(id);
        updateRatingForItem(review);
        updateUserReviewCount.decrement(review.getUserId());
        if (res)
            LOGGER.info("Review with ID: {} deleted successfully", id);
        else
            LOGGER.warn("Failed to delete review with ID: {}", id);
        return res;
    }

    @Override
    public List<User> likedBy(Long reviewId, Integer pageNum, Integer pageSize) {
        return reviewDao.likedBy(reviewId, pageNum, pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public Review findArtistReviewById(Long id) {
        return reviewDao.findArtistReviewById(id).orElseThrow(() -> new ReviewNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Review findAlbumReviewById(Long id) {
        return reviewDao.findAlbumReviewById(id).orElseThrow(() -> new ReviewNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Review findArtistReviewByUserId(Long userId, Long artistId) {
        return reviewDao.findArtistReviewByUserId(userId, artistId)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Review findAlbumReviewByUserId(Long userId, Long albumId) {
        return reviewDao.findAlbumReviewByUserId(userId, albumId)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Review findSongReviewByUserId(Long userId, Long songId) {
        return reviewDao.findSongReviewByUserId(userId, songId)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Review findSongReviewById(Long id) {
        return reviewDao.findSongReviewById(id).orElseThrow(() -> new ReviewNotFoundException(id));
    }

    @Override
    @Transactional
    public Void createLike(Long userId, Long reviewId) {
        LOGGER.info("Creating like for review ID: {} by user ID: {}", reviewId, userId);

        User user = userRepository.findById(new UserId(userId)).orElseThrow(() -> new UserNotFoundException(userId));
        Review review = reviewDao.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));
        if (isLiked(userId, reviewId))
            throw new LikeAlreadyExistsException(userId, reviewId);

        reviewDao.createLike(userId, reviewId);
        reviewDao.updateLikeCount(reviewId);
        notificationService.notifyLike(review, user);

        LOGGER.info("Like created and like count incremented for review ID: {}", reviewId);
        return null;
    }

    @Override
    @Transactional
    public Void removeLike(Long userId, Long reviewId) {
        LOGGER.info("Removing like for review ID: {} by user ID: {}", reviewId, userId);
        reviewDao.deleteLike(userId, reviewId);
        reviewDao.updateLikeCount(reviewId);
        LOGGER.info("Like removed and like count decremented for review ID: {}", reviewId);
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isLiked(Long userId, Long reviewId) {
        return reviewDao.isLiked(userId, reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getLikedReviewIds(Long userId, List<Long> reviewIds) {
        return reviewDao.getLikedReviewIds(userId, reviewIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findArtistReviewsPaginated(Long artistId, Integer page, Integer pageSize) {
        return new ArrayList<>(reviewDao.findArtistReviewsPaginated(artistId, page, pageSize));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findAlbumReviewsPaginated(Long albumId, Integer page, Integer pageSize) {
        return new ArrayList<>(reviewDao.findAlbumReviewsPaginated(albumId, page, pageSize));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findSongReviewsPaginated(Long songId, Integer page, Integer pageSize) {
        return new ArrayList<>(reviewDao.findSongReviewsPaginated(songId, page, pageSize));
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isArtistReview(Long reviewId) {
        return reviewDao.isArtistReview(reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isAlbumReview(Long reviewId) {
        return reviewDao.isAlbumReview(reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isSongReview(Long reviewId) {
        return reviewDao.isSongReview(reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findReviewsByUserPaginated(Long userId, Integer page, Integer pageSize) {
        return reviewDao.findReviewsByUserPaginated(userId, page, pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsFromFollowedUsersPaginated(Integer page, Integer pageSize, Long loggedUserId) {
        return reviewDao.getReviewsFromFollowedUsersPaginated(loggedUserId, page, pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countAll() {
        return reviewDao.countAll();
    }

    @Override
    @Transactional
    public Void block(Long reviewId) {
        LOGGER.info("Blocking review with ID: {}", reviewId);

        Review review = reviewDao.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));
        User user = userRepository.findById(new UserId(review.getUserId())).orElseThrow(() -> new UserNotFoundException(review.getUserId()));
        try {
            emailService.sendReviewAcknowledgement(ReviewAcknowledgementType.BLOCKED, user, review.getTitle(),
                    review.getItemName(), review.getItemType().toString());
            LOGGER.info("Acknowledgement email sent successfully");
        } catch (MessagingException e) {
            LOGGER.error("Failed to send acknowledgement email to user: {}", user.getEmail().getValue(), e);
            throw new AcknowledgementEmailException(user.getEmail().getValue(), e);
        }

        reviewDao.block(reviewId);
        LOGGER.info("Review blocked successfully: {}", reviewId);
        return null;
    }

    @Override
    @Transactional
    public Void unblock(Long reviewId) {
        LOGGER.info("Unblocking review with ID: {}", reviewId);

        Review review = reviewDao.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));
        User user = userRepository.findById(new UserId(review.getUserId())).orElseThrow(() -> new UserNotFoundException(review.getUserId()));
        try {
            emailService.sendReviewAcknowledgement(ReviewAcknowledgementType.UNBLOCKED, user, review.getTitle(),
                    review.getItemName(), review.getItemType().toString());
            LOGGER.info("Acknowledgement email sent successfully");
        } catch (MessagingException e) {
            LOGGER.error("Failed to send acknowledgement email to user: {}", user.getEmail().getValue(), e);
            throw new AcknowledgementEmailException(user.getEmail().getValue(), e);
        }

        reviewDao.unblock(reviewId);
        LOGGER.info("Review unblocked successfully: {}", reviewId);
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Long countReviewsByUser(Long userId) {
        return reviewDao.countReviewsByUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countReviewsFromFollowedUsers(Long loggedUserId) {
        return reviewDao.countReviewsFromFollowedUsers(loggedUserId);
    }

    private Notification.NotificationType blockStatusChanged(Boolean wasBlocked, Boolean isBlocked) {
        if (wasBlocked == null || isBlocked == null || wasBlocked.equals(isBlocked)) {
            return null;
        }

        if (wasBlocked == true && isBlocked == false) {
            return Notification.NotificationType.REVIEW_UNBLOCKED;
        } else if (wasBlocked == false && isBlocked == true) {
            return Notification.NotificationType.REVIEW_BLOCKED;
        }
        return null;
    }

}
