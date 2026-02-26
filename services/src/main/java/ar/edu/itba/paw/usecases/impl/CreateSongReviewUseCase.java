package ar.edu.itba.paw.usecases.impl;

import ar.edu.itba.paw.exception.conflict.UserAlreadyReviewedException;
import ar.edu.itba.paw.exception.not_found.SongNotFoundException;
import ar.edu.itba.paw.exception.not_found.UserNotFoundException;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.persistence.ReviewDao;
import ar.edu.itba.paw.persistence.SongDao;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.services.NotificationService;
import ar.edu.itba.paw.services.SongService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.usecases.CreateSongReview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CreateSongReviewUseCase implements CreateSongReview {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateSongReviewUseCase.class);

    private final ReviewDao reviewDao;
    private final UserDao userDao;
    private final SongDao songDao;
    private final SongService songService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public CreateSongReviewUseCase(
            ReviewDao reviewDao,
            UserDao userDao,
            SongDao songDao,
            SongService songService,
            UserService userService,
            NotificationService notificationService) {
        this.reviewDao = reviewDao;
        this.userDao = userDao;
        this.songDao = songDao;
        this.songService = songService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Review execute(CreateSongReviewCommand command) {
        LOGGER.info("Executing CreateSongReview use case for user={}, song={}",
                    command.userId(), command.songId());

        User user = userDao.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        Song song = songDao.findById(command.songId())
                .orElseThrow(() -> new SongNotFoundException(command.songId()));

        if (songService.hasUserReviewed(command.userId(), command.songId())) {
            throw new UserAlreadyReviewedException(command.userId(), command.songId(), "Song");
        }

        SongReview review = new SongReview();
        review.setUser(user);
        review.setSong(song);
        review.setTitle(command.title());
        review.setDescription(command.description());
        review.setRating(command.rating());
        review.setLikes(0);
        review.setBlocked(false);
        review.setCreatedAt(LocalDateTime.now());

        Review createdReview = reviewDao.create(review);
        LOGGER.info("Review created successfully with ID: {}", createdReview.getId());

        userService.updateUserReviewAmount(command.userId());
        songService.updateRating(command.songId());
        notificationService.notifyNewReview(createdReview);

        return createdReview;
    }
}
