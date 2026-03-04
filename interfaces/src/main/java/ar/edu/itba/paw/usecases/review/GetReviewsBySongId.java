package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.SongReview;
import java.util.List;

public interface GetReviewsBySongId {
    List<SongReview> execute(Long songId, Integer page, Integer size);
}
