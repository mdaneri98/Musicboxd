package ar.edu.itba.paw.domain.review;

import ar.edu.itba.paw.domain.rating.Rating;
import ar.edu.itba.paw.domain.song.SongId;
import ar.edu.itba.paw.domain.user.UserId;

import java.time.LocalDateTime;

public class SongReview extends Review {
    private final SongId songId;

    private SongReview(ReviewId id, UserId userId, SongId songId,
                       String title, String description, Rating rating,
                       LocalDateTime createdAt, int likes, boolean blocked,
                       int commentAmount) {
        super(id, userId, title, description, rating, createdAt, likes, blocked, commentAmount);
        this.songId = songId;
    }

    public static SongReview create(UserId userId, SongId songId,
                                    String title, String description, Rating rating) {
        validateTitle(title);
        validateDescription(description);

        return new SongReview(
            null,
            userId,
            songId,
            title,
            description,
            rating,
            LocalDateTime.now(),
            0,
            false,
            0
        );
    }

    public static SongReview reconstitute(ReviewId id, UserId userId, SongId songId,
                                          String title, String description, Rating rating,
                                          LocalDateTime createdAt, int likes, boolean blocked,
                                          int commentAmount) {
        if (id == null) {
            throw new IllegalArgumentException("ID required for reconstitution");
        }
        return new SongReview(id, userId, songId, title, description, rating,
                              createdAt, likes, blocked, commentAmount);
    }

    @Override
    public ReviewType getType() {
        return ReviewType.SONG;
    }

    @Override
    public Long getItemId() {
        return songId.getValue();
    }

    public SongId getSongId() {
        return songId;
    }
}
