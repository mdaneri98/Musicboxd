package ar.edu.itba.paw.usecases.impl;

import ar.edu.itba.paw.exception.conflict.UserAlreadyReviewedException;
import ar.edu.itba.paw.exception.not_found.AlbumNotFoundException;
import ar.edu.itba.paw.exception.not_found.UserNotFoundException;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.ReviewType;
import ar.edu.itba.paw.persistence.AlbumDao;
import ar.edu.itba.paw.persistence.ReviewDao;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.services.AlbumService;
import ar.edu.itba.paw.services.NotificationService;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.usecases.CreateAlbumReview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CreateAlbumReviewUseCase implements CreateAlbumReview {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateAlbumReviewUseCase.class);

    private final ReviewDao reviewDao;
    private final UserDao userDao;
    private final AlbumDao albumDao;
    private final AlbumService albumService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public CreateAlbumReviewUseCase(
            ReviewDao reviewDao,
            UserDao userDao,
            AlbumDao albumDao,
            AlbumService albumService,
            UserService userService,
            NotificationService notificationService) {
        this.reviewDao = reviewDao;
        this.userDao = userDao;
        this.albumDao = albumDao;
        this.albumService = albumService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Review execute(CreateAlbumReviewCommand command) {
        LOGGER.info("Executing CreateAlbumReview use case for user={}, album={}",
                    command.userId(), command.albumId());

        // 1. Validate user exists
        User user = userDao.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        // 2. Validate album exists
        Album album = albumDao.findById(command.albumId())
                .orElseThrow(() -> new AlbumNotFoundException(command.albumId()));

        // 3. Check for duplicate review
        if (albumService.hasUserReviewed(command.userId(), command.albumId())) {
            throw new UserAlreadyReviewedException(command.userId(), command.albumId(), "Album");
        }

        // 4. Create review entity
        AlbumReview review = new AlbumReview();
        review.setUser(user);
        review.setAlbum(album);
        review.setTitle(command.title());
        review.setDescription(command.description());
        review.setRating(command.rating());
        review.setLikes(0);
        review.setBlocked(false);
        review.setCreatedAt(LocalDateTime.now());

        // 5. Persist review
        Review createdReview = reviewDao.create(review);
        LOGGER.info("Review created successfully with ID: {}", createdReview.getId());

        // 6. Side effects - Update user review count
        userService.updateUserReviewAmount(command.userId());

        // 7. Side effects - Update album rating
        albumService.updateRating(command.albumId());

        // 8. Side effects - Notify followers
        notificationService.notifyNewReview(createdReview);

        return createdReview;
    }
}
