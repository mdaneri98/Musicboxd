package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.ReviewRepository;
import ar.edu.itba.paw.domain.review.SongReview;
import ar.edu.itba.paw.domain.song.SongId;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetReviewsBySongIdUseCase implements GetReviewsBySongId {

    private final ReviewRepository reviewRepository;

    @Autowired
    public GetReviewsBySongIdUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<SongReview> execute(Long songId, Integer page, Integer size) {
        return reviewRepository.findBySongId(new SongId(songId), page, size);
    }
}
