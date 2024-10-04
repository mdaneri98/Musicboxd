package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ImageService {

    Optional<Image> findById(long imageId);

    long save(byte[] bytes, boolean isProfile);

    long save(MultipartFile imageFile, boolean isProfile);

    long update(long imageId, byte[] bytes);

    long update(long imageId, MultipartFile imageFile);

    boolean delete(long imageId);

}