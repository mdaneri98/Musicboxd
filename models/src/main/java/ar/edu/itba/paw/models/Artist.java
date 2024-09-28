package ar.edu.itba.paw.models;



import java.time.LocalDate;

public class Artist {
    private Long id;
    private String name;
    private String bio;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long imgId;
    private Integer ratingCount;
    private Float avgRating;

    public Artist(Long id, String name, String bio, LocalDate createdAt, LocalDate updatedAt, Long imgId, Integer ratingCount, Float avgRating) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.imgId = imgId;
        this.ratingCount = ratingCount;
        this.avgRating = avgRating;
    }

    public Artist(Long id, String name, Long imgId) {
        this.id = id;
        this.name = name;
        this.imgId = imgId;
    }

    public Artist(String name, String bio, Long imgId) {
        this.name = name;
        this.bio = bio;
        this.imgId = imgId;
    }

    public Artist(Long id) {
        this.id = id;
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

    public Long getImgId() {
        return imgId;
    }

    public void setImgId(Long imgId) {
        this.imgId = imgId;
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
        json.append("\"imgId\":").append(imgId);
        json.append("}");
        return json.toString();
    }

}
