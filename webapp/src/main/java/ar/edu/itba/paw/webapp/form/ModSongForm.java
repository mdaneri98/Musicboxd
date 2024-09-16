package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

public class ModSongForm {

    @Size(min = 1, max = 255)
    private String title;
    @Pattern(regexp = "^([0-5]?[0-9]):([0-5][0-9])$", message = "Duration must be in the format MM:SS - Example: 10:24 or 3:15")
    private String duration;
    @Positive
    private Number trackNumber;

    public ModSongForm(String title, String duration, Number trackNumber) {
        this.title = title;
        this.duration = duration;
        this.trackNumber = trackNumber;
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

    public Number getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(Number trackNumber) {
        this.trackNumber = trackNumber;
    }
}
