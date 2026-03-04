package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.*;
import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.artist.ArtistId;
import ar.edu.itba.paw.domain.album.AlbumId;
import ar.edu.itba.paw.domain.song.SongId;
import ar.edu.itba.paw.domain.rating.Rating;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateReviewUseCase implements CreateReview {

    private final ReviewRepository reviewRepository;

    @Autowired
    public CreateReviewUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Review execute(CreateReviewCommand command) {
        Review review = switch (command.reviewType()) {
            case ARTIST -> ArtistReview.create(
                    new UserId(command.userId()),
                    new ArtistId(command.itemId()),
                    command.title(),
                    command.description(),
                    Rating.of(command.rating())
            );
            case ALBUM -> AlbumReview.create(
                    new UserId(command.userId()),
                    new AlbumId(command.itemId()),
                    command.title(),
                    command.description(),
                    Rating.of(command.rating())
            );
            case SONG -> SongReview.create(
                    new UserId(command.userId()),
                    new SongId(command.itemId()),
                    command.title(),
                    command.description(),
                    Rating.of(command.rating())
            );
        };

        return reviewRepository.save(review);
    }
}
