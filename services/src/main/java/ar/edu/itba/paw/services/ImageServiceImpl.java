package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.persistence.ImageDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {
    private final ImageDao imageDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageServiceImpl.class);

    private final long DEFAULT_IMAGE_ID = 1;
    private final long DEFAULT_PROFILE_IMAGE_ID = 2;

    public ImageServiceImpl(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    @Transactional(readOnly = true)
    public Optional<Image> findById(long id) {
        return imageDao.findById(id);
    }

    @Transactional
    public Image create(Image image) {
        if (image.getBytes() == null || image.getBytes().length == 0) {
            LOGGER.debug("No image value for save!.");
        }
        return imageDao.create(image);
    }

    @Transactional
    public Optional<Image> update(Image image) {
        if(image.getBytes() == null || image.getBytes().length == 0) {
            LOGGER.debug("Image {} not updated. File empty", image.getId());
            return Optional.empty();
        }

        /*
        if(imageId == DEFAULT_IMAGE_ID || imageId == DEFAULT_PROFILE_IMAGE_ID) {
            return save(bytes, false);
        }
        */
        Optional<Image> imageOptional = imageDao.update(image);
        if (imageOptional.isPresent())
            LOGGER.debug("Image {} updated", image.getId());
        else
            LOGGER.debug("Image {} could not update", image.getId());

        return imageOptional;
    }

    @Transactional
    public boolean delete(long imageId) {
        if(imageId == DEFAULT_IMAGE_ID || imageId == DEFAULT_PROFILE_IMAGE_ID) {
            LOGGER.debug("Image {} not deleted. Image is default", imageId);
            return false;
        }
        return imageDao.delete(imageId);
    }

    public long getDefaultImgId() {
        return DEFAULT_IMAGE_ID;
    }

    public long getDefaultProfileImgId() {
        return DEFAULT_PROFILE_IMAGE_ID;
    }

    @Transactional(readOnly = true)
    public boolean exists(long imageId) {
        return imageDao.exists(imageId);
    }
}
