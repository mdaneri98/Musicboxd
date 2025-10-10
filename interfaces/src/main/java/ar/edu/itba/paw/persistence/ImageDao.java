package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.models.Image;

import java.util.Optional;

public interface ImageDao {

    Optional<Image> findById(Long imageId);

    Image create(Image image);

    Optional<Image> update(Image image);

    Boolean delete(Long imageId);

    Boolean exists(Long imageId);

}
