package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.*;

public class ModSongForm {


    @Size(min = 1, max = 100, message = "{validation.song.title.size}")
    private String title;

    @Pattern(regexp = "^(?:(?:([0-9]{1,2}):)?([0-5]?[0-9]):)?([0-5][0-9])$", message = "{validation.song.duration.format}")
    private String duration;

    @Min(value = 1, message = "{validation.song.tracknumber.min}")
    @Max(value = 500, message = "{validation.song.tracknumber.max}")
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
