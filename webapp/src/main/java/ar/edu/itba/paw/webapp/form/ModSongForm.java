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
    private Integer trackNumber;

    // Hidden inputs
    private long id;
    private long albumId;
    private boolean deleted = false;

    public ModSongForm() {}

    public ModSongForm(long id, String title, String duration, Integer trackNumber, long albumId, boolean deleted) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.trackNumber = trackNumber;
        this.albumId = albumId;
        this.deleted = deleted;
    }

    public long getId() {return id;}

    public void setId(long id) {this.id = id;}

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

    public long getAlbumId() { return albumId; }

    public void setAlbumId(long albumId) { this.albumId = albumId; }

    public boolean isDeleted() {return deleted;}

    public void setDeleted(boolean deleted) {this.deleted = deleted;}
}
