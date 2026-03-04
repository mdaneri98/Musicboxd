package ar.edu.itba.paw.infrastructure.jpa;

import javax.persistence.*;

@Entity
@Table(name = "album_review")
@PrimaryKeyJoinColumn(name = "review_id")
public class AlbumReviewJpaEntity extends ReviewJpaEntity {

    @Column(name = "album_id", nullable = false)
    private Long albumId;

    public AlbumReviewJpaEntity() {
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    @Override
    public String getDiscriminatorType() {
        return "ALBUM";
    }

    @Override
    public Long getItemId() {
        return albumId;
    }
}
