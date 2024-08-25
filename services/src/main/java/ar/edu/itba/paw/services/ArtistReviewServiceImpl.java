package ar.edu.itba.paw.services;


import ar.edu.itba.paw.ArtistReview;
import ar.edu.itba.paw.persistence.ArtistReviewDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArtistReviewServiceImpl implements ArtistReviewService {
    /*
        FIXME: Add required `business logic`
     */
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
    public int save(ArtistReview artistReview) {
        return artistReviewDao.save(artistReview);
    }

    @Override
    public int update(ArtistReview artistReview) {
        return artistReviewDao.update(artistReview);
    }

    @Override
    public int deleteById(long id) {
        return artistReviewDao.deleteById(id);
    }
}
