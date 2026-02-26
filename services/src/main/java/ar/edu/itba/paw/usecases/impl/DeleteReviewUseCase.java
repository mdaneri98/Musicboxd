package ar.edu.itba.paw.usecases.impl;

import ar.edu.itba.paw.exception.not_found.ReviewNotFoundException;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.persistence.ReviewDao;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.services.AlbumService;
import ar.edu.itba.paw.services.ArtistService;
import ar.edu.itba.paw.services.SongService;
import ar.edu.itba.paw.usecases.DeleteReview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteReviewUseCase implements DeleteReview {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteReviewUseCase.class);

    private final ReviewDao reviewDao;
    private final UserDao userDao;
    private final AlbumService albumService;
    private final ArtistService artistService;
    private final SongService songService;

    @Autowired
    public DeleteReviewUseCase(
            ReviewDao reviewDao,
            UserDao userDao,
            AlbumService albumService,
            ArtistService artistService,
            SongService songService) {
        this.reviewDao = reviewDao;
        this.userDao = userDao;
        this.albumService = albumService;
        this.artistService = artistService;
        this.songService = songService;
    }

    @Override
    @Transactional
    public void execute(DeleteReviewCommand command) {
        LOGGER.info("Executing DeleteReview use case for review={}, user={}",
                    command.reviewId(), command.userId());

        Review review = reviewDao.findById(command.reviewId())
                .orElseThrow(() -> new ReviewNotFoundException(command.reviewId()));

        if (!review.getUser().getId().equals(command.userId())) {
            throw new IllegalArgumentException("User is not the owner of this review");
        }

        reviewDao.delete(command.reviewId());
        LOGGER.info("Review deleted successfully: {}", command.reviewId());

        updateItemRating(review);

        userDao.updateUserReviewAmount(command.userId());
        LOGGER.info("User review count updated for user: {}", command.userId());
    }

    private void updateItemRating(Review review) {
        switch (review.getItemType()) {
            case ALBUM:
                albumService.updateRating(review.getItemId());
                break;
            case ARTIST:
                artistService.updateRating(review.getItemId());
                break;
            case SONG:
                songService.updateRating(review.getItemId());
                break;
        }
    }
}
