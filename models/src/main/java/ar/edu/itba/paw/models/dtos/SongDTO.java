package ar.edu.itba.paw.models.dtos;

public class SongDTO {

    private long id;
    private String title;
    private String duration;
    private Integer trackNumber;
    private boolean deleted = false;

    public SongDTO() {}

    public SongDTO(long id, String title, String duration, Integer trackNumber, boolean deleted) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.trackNumber = trackNumber;
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

    public boolean isDeleted() {return deleted;}

    public void setDeleted(boolean deleted) {this.deleted = deleted;}
}
