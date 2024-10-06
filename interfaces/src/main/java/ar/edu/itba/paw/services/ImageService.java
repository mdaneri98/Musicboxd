package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ImageService {

    Optional<Image> findById(long imageId);

    long save(byte[] bytes, boolean isProfile);

    long update(long imageId, byte[] bytes);

    boolean delete(long imageId);

}