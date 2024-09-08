package ar.edu.itba.paw.services.reviews;

import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.persistence.SongReviewDao;
import ar.edu.itba.paw.services.SongReviewService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SongReviewServiceImpl implements SongReviewService {
    /*
        FIXME: Add required `business logic`
     */
    private final SongReviewDao songReviewDao;

    public SongReviewServiceImpl(SongReviewDao songReviewDao) {
        this.songReviewDao = songReviewDao;
    }

    @Override
    public Optional<SongReview> findById(long id) {
        return songReviewDao.findById(id);
    }

    @Override
    public List<SongReview> findAll() {
        return songReviewDao.findAll();
    }

    @Override
    public int save(SongReview songReview) {
        return songReviewDao.save(songReview);
    }

    @Override
    public int deleteById(long id) {
        return songReviewDao.deleteById(id);
    }
}
