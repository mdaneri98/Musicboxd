package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.SongReview;

import java.util.List;
import java.util.Optional;

public interface SongReviewDao {

    Optional<SongReview> findById(long id);
    List<SongReview> findAll();
    int save(SongReview songReview);
    int update(SongReview songReview);
    int deleteById(long id);
}

