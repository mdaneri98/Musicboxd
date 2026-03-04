package ar.edu.itba.paw.domain.review;

import ar.edu.itba.paw.domain.artist.ArtistId;
import ar.edu.itba.paw.domain.rating.Rating;
import ar.edu.itba.paw.domain.user.UserId;

import java.time.LocalDateTime;

public class ArtistReview extends Review {
    private final ArtistId artistId;

    private ArtistReview(ReviewId id, UserId userId, ArtistId artistId,
                         String title, String description, Rating rating,
                         LocalDateTime createdAt, int likes, boolean blocked,
                         int commentAmount) {
        super(id, userId, title, description, rating, createdAt, likes, blocked, commentAmount);
        this.artistId = artistId;
    }

    public static ArtistReview create(UserId userId, ArtistId artistId,
                                      String title, String description, Rating rating) {
        validateTitle(title);
        validateDescription(description);

        return new ArtistReview(
            null,
            userId,
            artistId,
            title,
            description,
            rating,
            LocalDateTime.now(),
            0,
            false,
            0
        );
    }

    public static ArtistReview reconstitute(ReviewId id, UserId userId, ArtistId artistId,
                                            String title, String description, Rating rating,
                                            LocalDateTime createdAt, int likes, boolean blocked,
                                            int commentAmount) {
        if (id == null) {
            throw new IllegalArgumentException("ID required for reconstitution");
        }
        return new ArtistReview(id, userId, artistId, title, description, rating,
                                createdAt, likes, blocked, commentAmount);
    }

    @Override
    public ReviewType getType() {
        return ReviewType.ARTIST;
    }

    @Override
    public Long getItemId() {
        return artistId.getValue();
    }

    public ArtistId getArtistId() {
        return artistId;
    }
}
