package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;

import java.util.Optional;

public interface ImageService {

    Optional<Image> findById(long imageId);

    Image create(byte[] bytes);

    Optional<Image> update(Image image);

    boolean delete(long imageId);

    long getDefaultImgId();

    long getDefaultProfileImgId();

}