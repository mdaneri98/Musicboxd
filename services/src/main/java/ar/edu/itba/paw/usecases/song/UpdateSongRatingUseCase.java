package ar.edu.itba.paw.usecases.song;

import ar.edu.itba.paw.domain.rating.RatingCalculator;
import ar.edu.itba.paw.domain.review.ReviewRepository;
import ar.edu.itba.paw.domain.review.SongReview;
import ar.edu.itba.paw.domain.song.SongId;
import ar.edu.itba.paw.domain.song.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UpdateSongRatingUseCase implements UpdateSongRating {

    private final SongRepository songRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public UpdateSongRatingUseCase(SongRepository songRepository, ReviewRepository reviewRepository) {
        this.songRepository = songRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional
    public void execute(Long songId) {
        SongId id = new SongId(songId);
        List<SongReview> reviews = reviewRepository.findBySongId(id, null, null);

        List<Integer> ratingValues = reviews.stream()
            .map(review -> review.getRating().getValue())
            .collect(Collectors.toList());

        double avgRating = RatingCalculator.calculateAverageFromIntegers(ratingValues);
        int ratingCount = reviews.size();

        songRepository.updateRating(songId, avgRating, ratingCount);
    }
}
