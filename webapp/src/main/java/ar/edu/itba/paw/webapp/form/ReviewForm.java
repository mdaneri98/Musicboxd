package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class ReviewForm {

    @Size(min = 8, max = 255)
    private String title;
    @Size(max = 2000)
    private String description;
    @Range(min = 0, max = 5)
    private Integer rating;

    public ReviewForm(String title, String description, Integer rating) {
        this.title = title;
        this.description = description;
        this.rating = rating;
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

}
