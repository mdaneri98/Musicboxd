package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Size;

public class ReviewForm {

    @Size(min = 2, max = 50, message = "{validation.review.title.size}")
    private String title;
    @Size(max = 2000, message = "{validation.review.description.size}")
    private String description;
    @Range(min = 0, max = 5, message = "{validation.review.rating.range}")
    private Integer rating;

    private Boolean isBlocked;
    private Integer itemId;
    private String itemType;

    public ReviewForm() {}

    public ReviewForm(String title, String description, Integer rating, Boolean isBlocked) {
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.isBlocked = isBlocked;
        if (rating == null) this.rating = 0;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getItemId() {
        return itemId;
    }

    public String getItemType() {
        return itemType;
    }

    public Boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }
}
