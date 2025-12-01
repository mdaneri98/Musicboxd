package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;


public interface ImageService {

    Image findById(Long imageId);

    Image create(byte[] bytes);

    Image update(Image image);

    Boolean delete(Long imageId);

    Long getDefaultImgId();

    Long getDefaultProfileImgId();

}