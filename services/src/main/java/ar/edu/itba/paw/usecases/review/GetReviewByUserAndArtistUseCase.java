package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.artist.ArtistId;
import ar.edu.itba.paw.domain.review.ArtistReview;
import ar.edu.itba.paw.domain.review.ReviewRepository;
import ar.edu.itba.paw.domain.user.UserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class GetReviewByUserAndArtistUseCase implements GetReviewByUserAndArtist {

    private final ReviewRepository reviewRepository;

    @Autowired
    public GetReviewByUserAndArtistUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Optional<ArtistReview> execute(Long userId, Long artistId) {
        return reviewRepository.findArtistReviewByUserAndArtist(
            new UserId(userId),
            new ArtistId(artistId)
        );
    }
}
