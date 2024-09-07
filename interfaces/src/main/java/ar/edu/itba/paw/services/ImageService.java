package ar.edu.itba.paw.services;

import ar.edu.itba.paw.Image;
import ar.edu.itba.paw.persistence.ImageDao;

import java.util.Optional;

public interface ImageService {

    Optional<Image> findById(long imageId);

    Image save(byte[] bytes);

    boolean update(long imageId, byte[] bytes);

    boolean delete(long imageId);

    boolean exists(long imageId);

}