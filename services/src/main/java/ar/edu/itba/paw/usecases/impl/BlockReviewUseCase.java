package ar.edu.itba.paw.usecases.impl;

import ar.edu.itba.paw.exception.email.AcknowledgementEmailException;
import ar.edu.itba.paw.exception.not_found.ReviewNotFoundException;
import ar.edu.itba.paw.models.ReviewAcknowledgementType;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.ReviewType;
import ar.edu.itba.paw.persistence.ReviewDao;
import ar.edu.itba.paw.ports.output.EmailSender;
import ar.edu.itba.paw.services.AlbumService;
import ar.edu.itba.paw.services.ArtistService;
import ar.edu.itba.paw.services.SongService;
import ar.edu.itba.paw.usecases.BlockReview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;

@Service
public class BlockReviewUseCase implements BlockReview {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockReviewUseCase.class);

    private final ReviewDao reviewDao;
    private final EmailSender emailSender;
    private final AlbumService albumService;
    private final ArtistService artistService;
    private final SongService songService;

    @Autowired
    public BlockReviewUseCase(
            ReviewDao reviewDao,
            EmailSender emailSender,
            AlbumService albumService,
            ArtistService artistService,
            SongService songService) {
        this.reviewDao = reviewDao;
        this.emailSender = emailSender;
        this.albumService = albumService;
        this.artistService = artistService;
        this.songService = songService;
    }

    @Override
    @Transactional
    public Review execute(BlockReviewCommand command) {
        LOGGER.info("Executing BlockReview use case for review={}, moderator={}, block={}",
                    command.reviewId(), command.moderatorId(), command.block());

        Review review = reviewDao.findById(command.reviewId())
                .orElseThrow(() -> new ReviewNotFoundException(command.reviewId()));

        User user = review.getUser();
        ReviewType itemType = review.getItemType();
        Long itemId = review.getItemId();

        try {
            ReviewAcknowledgementType emailType = command.block()
                ? ReviewAcknowledgementType.BLOCKED
                : ReviewAcknowledgementType.UNBLOCKED;

            emailSender.sendReviewAcknowledgement(
                emailType,
                user,
                review.getTitle(),
                review.getItemName(),
                itemType.toString()
            );
            LOGGER.info("Acknowledgement email sent successfully");
        } catch (MessagingException e) {
            LOGGER.error("Failed to send acknowledgement email to user: {}", user.getEmail(), e);
            throw new AcknowledgementEmailException(user.getEmail(), e);
        }

        if (command.block()) {
            reviewDao.block(command.reviewId());
            LOGGER.info("Review blocked successfully: {}", command.reviewId());
        } else {
            reviewDao.unblock(command.reviewId());
            LOGGER.info("Review unblocked successfully: {}", command.reviewId());
        }

        updateItemRating(itemType, itemId);

        return reviewDao.findById(command.reviewId())
                .orElseThrow(() -> new ReviewNotFoundException(command.reviewId()));
    }

    private void updateItemRating(ReviewType itemType, Long itemId) {
        switch (itemType) {
            case ALBUM:
                albumService.updateRating(itemId);
                break;
            case ARTIST:
                artistService.updateRating(itemId);
                break;
            case SONG:
                songService.updateRating(itemId);
                break;
        }
    }
}
