package ar.edu.itba.paw.models;


public class Image {

    private Long id;
    private byte[] bytes;

    Image() {

    }

    public Image(Long id, byte[] bytes) {
        this.id = id;
        this.bytes = bytes;
    }

    public Long getId() {
        return id;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}