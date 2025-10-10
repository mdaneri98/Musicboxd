package ar.edu.itba.paw.api.models;

import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.ArtistReview;

/**
 * HATEOAS resource wrapper for Review entities
 */
public class ReviewResource extends Resource<Review> {

    private final Review item;

    public ReviewResource(SongReview item) {
        this.item = item;
    }

    public ReviewResource(AlbumReview item) {
        this.item = item;
    }

    public ReviewResource(ArtistReview item) {
        this.item = item;
    }

    @Override
    public Review getData() {
        return item;
    }
}
