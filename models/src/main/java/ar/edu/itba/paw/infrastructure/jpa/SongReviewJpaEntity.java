package ar.edu.itba.paw.infrastructure.jpa;

import javax.persistence.*;

@Entity
@Table(name = "song_review")
@PrimaryKeyJoinColumn(name = "review_id")
public class SongReviewJpaEntity extends ReviewJpaEntity {

    @Column(name = "song_id", nullable = false)
    private Long songId;

    public SongReviewJpaEntity() {
    }

    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }

    @Override
    public String getDiscriminatorType() {
        return "SONG";
    }

    @Override
    public Long getItemId() {
        return songId;
    }
}
