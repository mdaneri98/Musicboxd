package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "album")
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "album_id_seq")
    @SequenceGenerator(sequenceName = "album_id_seq", name = "album_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 50)
    private String genre;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "img_id", referencedColumnName = "id", nullable = false)
    private Image image;

    @Column(name = "rating_amount", nullable = false)
    private Integer ratingCount;

    @Column(name = "avg_rating", nullable = false)
    private Float avgRating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Song> songs = new ArrayList<>();

    public Album() {
        // Constructor vacío necesario para JPA
    }

    public Album(Long id, String title, String genre, LocalDate releaseDate, LocalDate createdAt, LocalDate updatedAt, Image image, Artist artist, Integer ratingCount, Float avgRating) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.image = image;
        this.artist = artist;
        this.ratingCount = ratingCount;
        this.avgRating = avgRating;
    }

    public Album(Long id, String title, String genre, LocalDate releaseDate, Image image, Artist artist, Integer ratingCount, Float avgRating) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.image = image;
        this.artist = artist;
        this.ratingCount = ratingCount;
        this.avgRating = avgRating;
    }

    public Album(Long id, String title, Image image, String genre, Artist artist, LocalDate releaseDate) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.artist = artist;
        this.releaseDate = releaseDate;
        this.genre = genre;
    }

    public Album(String title, String genre, LocalDate releaseDate, Image image, Artist artist) {
        this.title = title;
        this.genre = genre;
        this.image = image;
        this.artist = artist;
        this.releaseDate = releaseDate;
    }

    public Album(String title, String genre, Image image, Artist artist) {
        this.title = title;
        this.genre = genre;
        this.image = image;
        this.artist = artist;
    }

    public Album(String title, String genre) {
        this.title = title;
        this.genre = genre;
    }

    public Album(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Album album)) return false;
        return Objects.equals(id, album.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Float avgRating) {
        this.avgRating = avgRating;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    // Método para convertir a JSON
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"type\":\"").append("album").append("\",");
        json.append("\"id\":").append(id).append(",");
        json.append("\"name\":\"").append(title).append("\",");
        json.append("\"genre\":\"").append(genre).append("\",");
        json.append("\"releaseDate\":\"").append(releaseDate != null ? releaseDate.toString() : null).append("\",");
        json.append("\"createdAt\":\"").append(createdAt != null ? createdAt.toString() : null).append("\",");
        json.append("\"updatedAt\":\"").append(updatedAt != null ? updatedAt.toString() : null).append("\",");
        json.append("\"imgId\":").append(image.getId()).append(",");

        // Convertir el artista a JSON si no es nulo
        json.append("\"artist\":").append(artist != null ? artist.toJson() : null);

        json.append("}");
        return json.toString();
    }

}
