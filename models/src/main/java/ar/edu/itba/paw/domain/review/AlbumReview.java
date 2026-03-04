package ar.edu.itba.paw.domain.review;

import ar.edu.itba.paw.domain.album.AlbumId;
import ar.edu.itba.paw.domain.rating.Rating;
import ar.edu.itba.paw.domain.user.UserId;

import java.time.LocalDateTime;

public class AlbumReview extends Review {
    private final AlbumId albumId;

    private AlbumReview(ReviewId id, UserId userId, AlbumId albumId,
                        String title, String description, Rating rating,
                        LocalDateTime createdAt, int likes, boolean blocked,
                        int commentAmount) {
        super(id, userId, title, description, rating, createdAt, likes, blocked, commentAmount);
        this.albumId = albumId;
    }

    public static AlbumReview create(UserId userId, AlbumId albumId,
                                     String title, String description, Rating rating) {
        validateTitle(title);
        validateDescription(description);

        return new AlbumReview(
            null,
            userId,
            albumId,
            title,
            description,
            rating,
            LocalDateTime.now(),
            0,
            false,
            0
        );
    }

    public static AlbumReview reconstitute(ReviewId id, UserId userId, AlbumId albumId,
                                           String title, String description, Rating rating,
                                           LocalDateTime createdAt, int likes, boolean blocked,
                                           int commentAmount) {
        if (id == null) {
            throw new IllegalArgumentException("ID required for reconstitution");
        }
        return new AlbumReview(id, userId, albumId, title, description, rating,
                               createdAt, likes, blocked, commentAmount);
    }

    @Override
    public ReviewType getType() {
        return ReviewType.ALBUM;
    }

    @Override
    public Long getItemId() {
        return albumId.getValue();
    }

    public AlbumId getAlbumId() {
        return albumId;
    }
}
