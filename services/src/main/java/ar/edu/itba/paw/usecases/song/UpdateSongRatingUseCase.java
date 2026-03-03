package ar.edu.itba.paw.usecases.song;

import ar.edu.itba.paw.domain.song.SongRepository;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.persistence.ReviewDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UpdateSongRatingUseCase implements UpdateSongRating {

    private final SongRepository songRepository;
    private final ReviewDao reviewDao;

    @Autowired
    public UpdateSongRatingUseCase(SongRepository songRepository, ReviewDao reviewDao) {
        this.songRepository = songRepository;
        this.reviewDao = reviewDao;
    }

    @Override
    @Transactional
    public void execute(Long songId) {
        List<SongReview> reviews = reviewDao.findReviewsBySongId(songId);

        if (reviews.isEmpty()) {
            songRepository.updateRating(songId, 0.0, 0);
            return;
        }

        double avgRating = reviews.stream()
            .mapToInt(SongReview::getRating)
            .average()
            .orElse(0.0);

        songRepository.updateRating(songId, avgRating, reviews.size());
    }
}
