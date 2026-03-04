package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.AlbumReview;
import java.util.List;

public interface GetReviewsByAlbumId {
    List<AlbumReview> execute(Long albumId, Integer page, Integer size);
}
