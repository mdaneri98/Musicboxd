package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.persistence.ImageDao;
import ar.edu.itba.paw.services.exception.not_found.ImageNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ImageServiceImpl implements ImageService {
    private final ImageDao imageDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageServiceImpl.class);

    private final Long DEFAULT_IMAGE_ID = 1L;
    private final Long DEFAULT_PROFILE_IMAGE_ID = 2L;

    public ImageServiceImpl(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    @Transactional(readOnly = true)
    public Image findById(Long id) {
        return imageDao.findById(id).orElseThrow(() -> new ImageNotFoundException(id));
    }

    @Transactional
    public Image create(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            LOGGER.debug("No image value for save!.");
        }
        return imageDao.create(new Image(bytes));
    }

    @Transactional
    public Image update(Image image) {
        if(image.getBytes() == null || image.getBytes().length == 0) {
            LOGGER.debug("Image {} not updated. File empty", image.getId());
            return findById(image.getId());
        }

        if(image.getId() == DEFAULT_IMAGE_ID || image.getId() == DEFAULT_PROFILE_IMAGE_ID) {
            LOGGER.debug("Image {} not updated. Cannot update default image", image.getId());
            return create(image.getBytes());
        }

        Image imageUpdated = imageDao.update(image).orElseThrow(() -> new ImageNotFoundException(image.getId()));

        return imageUpdated;
    }

    @Transactional
    public Boolean delete(Long imageId) {
        if(imageId == DEFAULT_IMAGE_ID || imageId == DEFAULT_PROFILE_IMAGE_ID) {
            LOGGER.debug("Image {} not deleted. Image is default", imageId);
            return false;
        }
        return imageDao.delete(imageId);
    }

    public Long getDefaultImgId() {
        return DEFAULT_IMAGE_ID;
    }

    public Long getDefaultProfileImgId() {
        return DEFAULT_PROFILE_IMAGE_ID;
    }

    @Transactional(readOnly = true)
    public Boolean exists(Long imageId) {
        return imageDao.exists(imageId);
    }
}
