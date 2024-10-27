package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "content", columnDefinition = "bytea", nullable = false)
    //@Lob
    private byte[] bytes;

    public Image() {
        // Constructor vacío necesario para JPA
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