package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.ReviewRepository;
import ar.edu.itba.paw.domain.review.SongReview;
import ar.edu.itba.paw.domain.song.SongId;
import ar.edu.itba.paw.domain.user.UserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class GetReviewByUserAndSongUseCase implements GetReviewByUserAndSong {

    private final ReviewRepository reviewRepository;

    @Autowired
    public GetReviewByUserAndSongUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Optional<SongReview> execute(Long userId, Long songId) {
        return reviewRepository.findSongReviewByUserAndSong(
            new UserId(userId),
            new SongId(songId)
        );
    }
}
