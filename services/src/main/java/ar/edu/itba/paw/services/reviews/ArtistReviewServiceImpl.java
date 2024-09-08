package ar.edu.itba.paw.services.reviews;


import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.persistence.ArtistReviewDao;
import ar.edu.itba.paw.services.ArtistReviewService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArtistReviewServiceImpl implements ArtistReviewService {
    private final ArtistReviewDao artistReviewDao;

    public ArtistReviewServiceImpl(ArtistReviewDao artistReviewDao) {
        this.artistReviewDao = artistReviewDao;
    }

    @Override
    public Optional<ArtistReview> findById(long id) {
        return artistReviewDao.findById(id);
    }

    @Override
    public List<ArtistReview> findAll() {
        return artistReviewDao.findAll();
    }

    @Override
    public List<ArtistReview> findByArtistId(long id) {
        return artistReviewDao.findByArtistId(id);
    }

    @Override
    public int save(ArtistReview artistReview) {
        return artistReviewDao.save(artistReview);
    }


    @Override
    public int deleteById(long id) {
        return artistReviewDao.deleteById(id);
    }
}
