package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "song")
public class Song {
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

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @Column(name = "rating_amount", nullable = false)
    private Integer ratingCount;

    @Column(name = "avg_rating", nullable = false)
    private Double avgRating;

    @ManyToMany
    @JoinTable(
            name = "song_artist",
            joinColumns = @JoinColumn(name = "song_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private List<Artist> artists = new ArrayList<>();

    @Transient
    private Boolean isReviewed;

    @Transient
    private Boolean isFavorite;

    public Song() {
        // Constructor vacío necesario para JPA
    }

    public Song(Long id, String title, String duration, Integer trackNumber, LocalDateTime createdAt, LocalDateTime updatedAt, Album album, Integer ratingCount, Double avgRating) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.trackNumber = trackNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.album = album;
        this.ratingCount = ratingCount;
        this.avgRating = avgRating;
    }

    public Song(Long id, String title, String duration, Integer trackNumber, Album album, Integer ratingCount, Double avgRating) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.trackNumber = trackNumber;
        this.album = album;
        this.ratingCount = ratingCount;
        this.avgRating = avgRating;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Song(Long id, String title, String duration, Integer trackNumber, Album album) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.trackNumber = trackNumber;
        this.album = album;
    }

    public Song(Long id, String title, String duration, Album album) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.album = album;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.ratingCount = 0;
        this.avgRating = 0.0;
    }

    public Song(String title, String duration, Integer trackNumber, Album album, Integer ratingCount, Double avgRating) {
        this.title = title;
        this.duration = duration;
        this.trackNumber = trackNumber;
        this.album = album;
        this.ratingCount = ratingCount;
        this.avgRating = avgRating;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Song(String title, String duration, Integer trackNumber, Album album) {
        this.title = title;
        this.duration = duration;
        this.trackNumber = trackNumber;
        this.album = album;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.ratingCount = 0;
        this.avgRating = 0.0;
    }

    public Song(Long id) {
        this.id = id;
    }

    // Getters y setters
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

    public Album getAlbum() {
        return album;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public Boolean getIsReviewed() {
        return isReviewed;
    }

    public void setIsReviewed(Boolean isReviewed) {
        this.isReviewed = isReviewed;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }
}
