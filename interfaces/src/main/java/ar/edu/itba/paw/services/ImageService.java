package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ImageService {

    Optional<Image> findById(long imageId);

    Image create(Image image);

    Optional<Image> update(Image image);

    boolean delete(long imageId);

    long getDefaultImgId();

    long getDefaultProfileImgId();

}