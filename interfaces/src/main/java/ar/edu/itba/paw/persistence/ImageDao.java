package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.models.Image;

import java.util.Optional;

public interface ImageDao {

    Optional<Image> findById(long imageId);

    long create(byte[] bytes);

    boolean update(long imageId, byte[] bytes);

    boolean delete(long imageId);

    boolean exists(long imageId);

}
