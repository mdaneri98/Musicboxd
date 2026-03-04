package ar.edu.itba.paw.persistence.mappers;

import ar.edu.itba.paw.domain.album.AlbumId;
import ar.edu.itba.paw.domain.artist.ArtistId;
import ar.edu.itba.paw.domain.rating.Rating;
import ar.edu.itba.paw.domain.review.*;
import ar.edu.itba.paw.domain.song.SongId;
import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.infrastructure.jpa.*;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public Review toDomain(ReviewJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        ReviewId reviewId = entity.getId() != null ? new ReviewId(entity.getId()) : null;
        UserId userId = new UserId(entity.getUserId());
        Rating rating = Rating.of(entity.getRating());

        if (entity instanceof ArtistReviewJpaEntity) {
            ArtistReviewJpaEntity artistEntity = (ArtistReviewJpaEntity) entity;
            ArtistId artistId = new ArtistId(artistEntity.getArtistId());

            return ArtistReview.reconstitute(
                reviewId,
                userId,
                artistId,
                entity.getTitle(),
                entity.getDescription(),
                rating,
                entity.getCreatedAt(),
                entity.getLikes(),
                entity.getIsBlocked() != null ? entity.getIsBlocked() : false,
                entity.getCommentAmount() != null ? entity.getCommentAmount() : 0
            );
        } else if (entity instanceof AlbumReviewJpaEntity) {
            AlbumReviewJpaEntity albumEntity = (AlbumReviewJpaEntity) entity;
            AlbumId albumId = new AlbumId(albumEntity.getAlbumId());

            return AlbumReview.reconstitute(
                reviewId,
                userId,
                albumId,
                entity.getTitle(),
                entity.getDescription(),
                rating,
                entity.getCreatedAt(),
                entity.getLikes(),
                entity.getIsBlocked() != null ? entity.getIsBlocked() : false,
                entity.getCommentAmount() != null ? entity.getCommentAmount() : 0
            );
        } else if (entity instanceof SongReviewJpaEntity) {
            SongReviewJpaEntity songEntity = (SongReviewJpaEntity) entity;
            SongId songId = new SongId(songEntity.getSongId());

            return SongReview.reconstitute(
                reviewId,
                userId,
                songId,
                entity.getTitle(),
                entity.getDescription(),
                rating,
                entity.getCreatedAt(),
                entity.getLikes(),
                entity.getIsBlocked() != null ? entity.getIsBlocked() : false,
                entity.getCommentAmount() != null ? entity.getCommentAmount() : 0
            );
        }

        throw new IllegalArgumentException("Unknown review entity type: " + entity.getClass());
    }

    public ReviewJpaEntity toEntity(Review domain) {
        if (domain == null) {
            return null;
        }

        ReviewJpaEntity entity;

        if (domain instanceof ArtistReview) {
            ArtistReviewJpaEntity artistEntity = new ArtistReviewJpaEntity();
            artistEntity.setArtistId(((ArtistReview) domain).getArtistId().getValue());
            entity = artistEntity;
        } else if (domain instanceof AlbumReview) {
            AlbumReviewJpaEntity albumEntity = new AlbumReviewJpaEntity();
            albumEntity.setAlbumId(((AlbumReview) domain).getAlbumId().getValue());
            entity = albumEntity;
        } else if (domain instanceof SongReview) {
            SongReviewJpaEntity songEntity = new SongReviewJpaEntity();
            songEntity.setSongId(((SongReview) domain).getSongId().getValue());
            entity = songEntity;
        } else {
            throw new IllegalArgumentException("Unknown review domain type: " + domain.getClass());
        }

        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setUserId(domain.getUserId().getValue());
        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setRating(domain.getRating().getValue());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setLikes(domain.getLikes());
        entity.setIsBlocked(domain.isBlocked());
        entity.setCommentAmount(domain.getCommentAmount());

        return entity;
    }
}
