package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "artist")
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "artist_id_seq")
    @SequenceGenerator(sequenceName = "artist_id_seq", name = "artist_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 2048)
    private String bio;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "img_id", referencedColumnName = "id", nullable = false)
    private Image image;

    @Column(name = "rating_amount", nullable = false)
    private Integer ratingCount;

    @Column(name = "avg_rating", nullable = false)
    private Double avgRating;

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Album> albums = new ArrayList<>();

    @ManyToMany(mappedBy = "artists")
    private List<Song> songs = new ArrayList<>();

    public Artist() {
        // Constructor vacío necesario para JPA
    }

    public Artist(Long id, String name, String bio, LocalDateTime createdAt, LocalDateTime updatedAt, Image image, Integer ratingCount, Double avgRating) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.image = image;
        this.ratingCount = ratingCount;
        this.avgRating = avgRating;
    }

    public Artist(Long id, String name, String bio, Image image, Integer ratingCount, Double avgRating) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.image = image;
        this.ratingCount = ratingCount;
        this.avgRating = avgRating;
    }

    public Artist(Long id, String name, String bio, Image image) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.image = image;
    }

    public Artist(Long id, String name, Image image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public Artist(String name, String bio, Image image, Integer ratingCount, Double avgRating) {
        this.name = name;
        this.bio = bio;
        this.image = image;
        this.ratingCount = ratingCount;
        this.avgRating = avgRating;
    }

    public Artist(String name, String bio, Image image) {
        this.name = name;
        this.bio = bio;
        this.image = image;
    }

    public Artist(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Artist artist)) return false;
        return Objects.equals(id, artist.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
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

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public List<Song> getSongs() {
        return songs;
    }

    // Método para convertir a JSON
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"type\":\"").append("artist").append("\",");
        json.append("\"id\":").append(id).append(",");
        json.append("\"name\":\"").append(name).append("\",");
        json.append("\"bio\":\"").append(bio).append("\",");
        json.append("\"createdAt\":\"").append(createdAt != null ? createdAt.toString() : null).append("\",");
        json.append("\"updatedAt\":\"").append(updatedAt != null ? updatedAt.toString() : null).append("\",");
        json.append("\"imgId\":").append(this.image.getId());
        json.append("}");
        return json.toString();
    }

}
