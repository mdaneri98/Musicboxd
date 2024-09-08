package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.persistence.ImageDao;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {
    private final ImageDao imageDao;

    public ImageServiceImpl(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    public Optional<Image> findById(long id) {
        return imageDao.findById(id);
    }

    public Image save(byte[] bytes) {
        return imageDao.save(bytes);
    }

    public boolean update(long imageId, byte[] bytes) {
        return imageDao.update(imageId, bytes);
    }

    public boolean delete(long imageId) {
        return imageDao.delete(imageId);
    }

    public boolean exists(long imageId) {
        return imageDao.exists(imageId);
    }
}
