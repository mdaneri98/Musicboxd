package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.album.AlbumId;
import ar.edu.itba.paw.domain.review.AlbumReview;
import ar.edu.itba.paw.domain.review.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetReviewsByAlbumIdUseCase implements GetReviewsByAlbumId {

    private final ReviewRepository reviewRepository;

    @Autowired
    public GetReviewsByAlbumIdUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<AlbumReview> execute(Long albumId, Integer page, Integer size) {
        return reviewRepository.findByAlbumId(new AlbumId(albumId), page, size);
    }
}
