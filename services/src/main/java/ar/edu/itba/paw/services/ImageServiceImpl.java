package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.persistence.ImageDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    public Optional<Image> findById(long id) {
        return imageDao.findById(id);
    }

    public long save(byte[] bytes, boolean isProfile) {
        if (bytes == null || bytes.length == 0) {
            LOGGER.debug("No image input. Default image used instead");
            if (isProfile) {
                return DEFAULT_PROFILE_IMAGE_ID;
            } else {
                return DEFAULT_IMAGE_ID;
            }
        }
        return imageDao.create(bytes);
    }

    public long update(long imageId, byte[] bytes) {
        if(bytes == null || bytes.length == 0) {
            LOGGER.debug("Image {} not updated. File empty", imageId);
            return imageId;
        }

        if(imageId == DEFAULT_IMAGE_ID || imageId == DEFAULT_PROFILE_IMAGE_ID) {
            return save(bytes, false);
        }

        if(imageDao.update(imageId, bytes)) {
            LOGGER.debug("Image {} updated", imageId);
        } else {
            LOGGER.debug("Image {} could not update", imageId);
        }

        return imageId;
    }

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

    public boolean exists(long imageId) {
        return imageDao.exists(imageId);
    }
}
