package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.artist.ArtistId;
import ar.edu.itba.paw.domain.review.ArtistReview;
import ar.edu.itba.paw.domain.review.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetReviewsByArtistIdUseCase implements GetReviewsByArtistId {

    private final ReviewRepository reviewRepository;

    @Autowired
    public GetReviewsByArtistIdUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<ArtistReview> execute(Long artistId, Integer page, Integer size) {
        return reviewRepository.findByArtistId(new ArtistId(artistId), page, size);
    }
}
