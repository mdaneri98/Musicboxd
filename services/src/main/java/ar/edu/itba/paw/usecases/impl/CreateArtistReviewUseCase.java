package ar.edu.itba.paw.usecases.impl;

import ar.edu.itba.paw.exception.conflict.UserAlreadyReviewedException;
import ar.edu.itba.paw.exception.not_found.ArtistNotFoundException;
import ar.edu.itba.paw.exception.not_found.UserNotFoundException;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.persistence.ArtistDao;
import ar.edu.itba.paw.persistence.ReviewDao;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.services.ArtistService;
import ar.edu.itba.paw.services.NotificationService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.usecases.CreateArtistReview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CreateArtistReviewUseCase implements CreateArtistReview {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateArtistReviewUseCase.class);

    private final ReviewDao reviewDao;
    private final UserDao userDao;
    private final ArtistDao artistDao;
    private final ArtistService artistService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public CreateArtistReviewUseCase(
            ReviewDao reviewDao,
            UserDao userDao,
            ArtistDao artistDao,
            ArtistService artistService,
            UserService userService,
            NotificationService notificationService) {
        this.reviewDao = reviewDao;
        this.userDao = userDao;
        this.artistDao = artistDao;
        this.artistService = artistService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Review execute(CreateArtistReviewCommand command) {
        LOGGER.info("Executing CreateArtistReview use case for user={}, artist={}",
                    command.userId(), command.artistId());

        User user = userDao.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        Artist artist = artistDao.findById(command.artistId())
                .orElseThrow(() -> new ArtistNotFoundException(command.artistId()));

        if (artistService.hasUserReviewed(command.userId(), command.artistId())) {
            throw new UserAlreadyReviewedException(command.userId(), command.artistId(), "Artist");
        }

        ArtistReview review = new ArtistReview();
        review.setUser(user);
        review.setArtist(artist);
        review.setTitle(command.title());
        review.setDescription(command.description());
        review.setRating(command.rating());
        review.setLikes(0);
        review.setBlocked(false);
        review.setCreatedAt(LocalDateTime.now());

        Review createdReview = reviewDao.create(review);
        LOGGER.info("Review created successfully with ID: {}", createdReview.getId());

        userService.updateUserReviewAmount(command.userId());
        artistService.updateRating(command.artistId());
        notificationService.notifyNewReview(createdReview);

        return createdReview;
    }
}
