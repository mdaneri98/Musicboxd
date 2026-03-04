package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.ArtistReview;
import java.util.List;

public interface GetReviewsByArtistId {
    List<ArtistReview> execute(Long artistId, Integer page, Integer size);
}
