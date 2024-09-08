package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.Image;

import java.util.Optional;

public interface ImageDao {

    Optional<Image> findById(long imageId);

    Image save(byte[] bytes);

    boolean update(long imageId, byte[] bytes);

    boolean delete(long imageId);

    boolean exists(long imageId);

}
