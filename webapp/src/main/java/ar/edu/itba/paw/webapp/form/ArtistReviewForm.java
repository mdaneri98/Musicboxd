package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class ArtistReviewForm extends ReviewForm {

    @NotNull
    private long artistId;

    public ArtistReviewForm(Long userId, String title, String description, Integer rating, long artistId) {
        super(userId, title, description, rating);
        this.artistId = artistId;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

}
