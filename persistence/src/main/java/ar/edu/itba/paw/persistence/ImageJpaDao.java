package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Image;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class ImageJpaDao implements ImageDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Image> findById(long imageId) {
        return Optional.ofNullable(em.find(Image.class, imageId));
    }

    @Override
    public long create(byte[] bytes) {
        Image image = new Image();
        image.setBytes(bytes);
        em.persist(image);
        em.flush(); // Ensure the image is persisted and the ID is generated.
        return image.getId();
    }

    @Override
    public boolean update(long imageId, byte[] bytes) {
        Optional<Image> maybeImage = findById(imageId);
        if (maybeImage.isPresent()) {
            Image image = maybeImage.get();
            image.setBytes(bytes);
            em.merge(image);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(long imageId) {
        Optional<Image> maybeImage = findById(imageId);
        if (maybeImage.isPresent()) {
            em.remove(maybeImage.get());
            return true;
        }
        return false;
    }

    @Override
    public boolean exists(long imageId) {
        return findById(imageId).isPresent();
    }
}
