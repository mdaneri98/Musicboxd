package ar.edu.itba.paw.services;

import ar.edu.itba.paw.AlbumReview;
import ar.edu.itba.paw.persistence.AlbumReviewDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumReviewServiceImpl implements AlbumReviewService {
    /*
        FIXME: Add required `business logic`
     */
    private final AlbumReviewDao albumReviewDao;

    public AlbumReviewServiceImpl(AlbumReviewDao albumReviewDao) {
        this.albumReviewDao = albumReviewDao;
    }

    @Override
    public Optional<AlbumReview> findById(long id) {
        return albumReviewDao.findById(id);
    }

    @Override
    public List<AlbumReview> findAll() {
        return albumReviewDao.findAll();
    }

    @Override
    public int save(AlbumReview albumReview) {
        return albumReviewDao.save(albumReview);
    }

    @Override
    public int update(AlbumReview albumReview) {
        return albumReviewDao.update(albumReview);
    }

    @Override
    public int deleteById(long id) {
        return albumReviewDao.deleteById(id);
    }
}

