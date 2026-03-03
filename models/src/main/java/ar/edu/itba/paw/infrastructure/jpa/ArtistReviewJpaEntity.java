package ar.edu.itba.paw.infrastructure.jpa;

import javax.persistence.*;

@Entity
@Table(name = "artist_review")
@PrimaryKeyJoinColumn(name = "review_id")
public class ArtistReviewJpaEntity extends ReviewJpaEntity {

    @Column(name = "artist_id", nullable = false)
    private Long artistId;

    public ArtistReviewJpaEntity() {
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    @Override
    public String getDiscriminatorType() {
        return "ARTIST";
    }

    @Override
    public Long getItemId() {
        return artistId;
    }
}
