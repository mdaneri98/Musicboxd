package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.models.Image;

import java.util.Optional;

public interface ImageDao {

    Optional<Image> findById(long imageId);

    Image create(Image image);

    Optional<Image> update(Image image);

    boolean delete(long imageId);

    boolean exists(long imageId);

}
