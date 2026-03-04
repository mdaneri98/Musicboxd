package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.album.AlbumId;
import ar.edu.itba.paw.domain.review.AlbumReview;
import ar.edu.itba.paw.domain.review.ReviewRepository;
import ar.edu.itba.paw.domain.user.UserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class GetReviewByUserAndAlbumUseCase implements GetReviewByUserAndAlbum {

    private final ReviewRepository reviewRepository;

    @Autowired
    public GetReviewByUserAndAlbumUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Optional<AlbumReview> execute(Long userId, Long albumId) {
        return reviewRepository.findAlbumReviewByUserAndAlbum(
            new UserId(userId),
            new AlbumId(albumId)
        );
    }
}
