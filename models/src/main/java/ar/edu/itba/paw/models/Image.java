package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "images_image_id_seq")
    @SequenceGenerator(sequenceName = "images_image_id_seq", name = "images_image_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "content", nullable = false)
    private byte[] bytes;

    public Image() {
        // Constructor vacío necesario para JPA
    }

    public Image(byte[] bytes) {
        this.bytes = bytes;
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