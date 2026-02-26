package ar.edu.itba.paw.usecases.impl;

import ar.edu.itba.paw.exception.not_found.ReviewNotFoundException;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.persistence.ReviewDao;
import ar.edu.itba.paw.services.AlbumService;
import ar.edu.itba.paw.services.ArtistService;
import ar.edu.itba.paw.services.NotificationService;
import ar.edu.itba.paw.services.SongService;
import ar.edu.itba.paw.usecases.UpdateReview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateReviewUseCase implements UpdateReview {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateReviewUseCase.class);

    private final ReviewDao reviewDao;
    private final NotificationService notificationService;
    private final AlbumService albumService;
    private final ArtistService artistService;
    private final SongService songService;

    @Autowired
    public UpdateReviewUseCase(
            ReviewDao reviewDao,
            NotificationService notificationService,
            AlbumService albumService,
            ArtistService artistService,
            SongService songService) {
        this.reviewDao = reviewDao;
        this.notificationService = notificationService;
        this.albumService = albumService;
        this.artistService = artistService;
        this.songService = songService;
    }

    @Override
    @Transactional
    public Review execute(UpdateReviewCommand command) {
        LOGGER.info("Executing UpdateReview use case for review={}, user={}",
                    command.reviewId(), command.userId());

        Review review = reviewDao.findById(command.reviewId())
                .orElseThrow(() -> new ReviewNotFoundException(command.reviewId()));

        if (!review.getUser().getId().equals(command.userId())) {
            throw new IllegalArgumentException("User is not the owner of this review");
        }

        Integer oldRating = review.getRating();

        review.setTitle(command.title());
        review.setDescription(command.description());
        review.setRating(command.rating());

        Review updatedReview = reviewDao.update(review);
        LOGGER.info("Review updated successfully: {}", updatedReview.getId());

        if (!oldRating.equals(command.rating())) {
            updateItemRating(review);
        }

        notificationService.notifyNewReview(updatedReview);

        return updatedReview;
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
