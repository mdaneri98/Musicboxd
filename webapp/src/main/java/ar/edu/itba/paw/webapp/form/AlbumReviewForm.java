package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class AlbumReviewForm extends ReviewForm {

    @NotNull
    private long albumId;

    public AlbumReviewForm(Long userId, String title, String description, Integer rating, long albumId) {
        super(userId, title, description, rating);
        this.albumId = albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getAlbumId() {
        return albumId;
    }

}
