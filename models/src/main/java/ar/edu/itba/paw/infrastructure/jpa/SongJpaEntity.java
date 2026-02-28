package ar.edu.itba.paw.infrastructure.jpa;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "song")
public class SongJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "song_id_seq")
    @SequenceGenerator(sequenceName = "song_id_seq", name = "song_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 10)
    private String duration;

    @Column(name = "track_number")
    private Integer trackNumber;

    @Column(name = "album_id", nullable = false)
    private Long albumId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "song_artist", joinColumns = @JoinColumn(name = "song_id"))
    @Column(name = "artist_id")
    private List<Long> artistIds = new ArrayList<>();

    @Column(name = "rating_amount", nullable = false)
    private Integer ratingCount;

    @Column(name = "avg_rating", nullable = false)
    private Double avgRating;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public SongJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(Integer trackNumber) {
        this.trackNumber = trackNumber;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public List<Long> getArtistIds() {
        return artistIds;
    }

    public void setArtistIds(List<Long> artistIds) {
        this.artistIds = artistIds;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
