package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class ArtistReviewForm extends ReviewForm {

    @NotNull
    private long artistId;

    public ArtistReviewForm(String userEmail, String title, String description, Integer rating, long artistId) {
        super(userEmail, title, description, rating);
        this.artistId = artistId;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

}
