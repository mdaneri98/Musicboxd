package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class SongReviewForm extends ReviewForm {

    @NotNull
    private long songId;

    public SongReviewForm(Long userId, String title, String description, Integer rating, long songId) {
        super(userId, title, description, rating);
        this.songId = songId;
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }
}
