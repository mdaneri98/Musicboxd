package ar.edu.itba.paw.services;

import ar.edu.itba.paw.Image;
import ar.edu.itba.paw.persistence.ImageDao;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {
    private final ImageDao imageDao;

    public ImageServiceImpl(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    public Optional<Image> findById(Long id) {
        return imageDao.findById(id);
    }

}
