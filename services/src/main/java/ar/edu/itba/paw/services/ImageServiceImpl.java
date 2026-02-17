package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.persistence.ImageDao;
import ar.edu.itba.paw.exception.not_found.ImageNotFoundException;
import ar.edu.itba.paw.services.utils.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


@Service
public class ImageServiceImpl implements ImageService {
    private final ImageDao imageDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageServiceImpl.class);

    private final Long DEFAULT_IMAGE_ID = 1L;
    private final Long DEFAULT_PROFILE_IMAGE_ID = 2L;

    private static final int MAX_WIDTH = 800;
    private static final int MAX_HEIGHT = 800;

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
        
        byte[] resizedBytes = ImageUtils.resizeImage(bytes, MAX_WIDTH, MAX_HEIGHT);
        return imageDao.create(new Image(resizedBytes));
    }

    @Transactional
    public Image update(Image image) {
        if(image.getBytes() == null || image.getBytes().length == 0) {
            LOGGER.debug("Image {} not updated. File empty", image.getId());
            return findById(image.getId());
        }

        byte[] resizedBytes = ImageUtils.resizeImage(image.getBytes(), MAX_WIDTH, MAX_HEIGHT);
        image.setBytes(resizedBytes);

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

    public Long handleImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            LOGGER.error("Error getting bytes from multipart file", e);
            return null;
        }
        return create(bytes).getId();
    }
}
