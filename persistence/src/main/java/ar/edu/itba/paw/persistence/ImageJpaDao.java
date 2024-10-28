package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Image;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;


@Primary
@Repository
public class ImageJpaDao implements ImageDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Image> findById(long imageId) {
        return Optional.ofNullable(em.find(Image.class, imageId));
    } 

    @Override
    public Image create(Image image) {
        em.persist(image);
        return image;
    }

    @Override
    public Optional<Image> update(Image image) {
        if (em.find(Image.class, image.getId()) == null) {
            return Optional.empty();
        }
        return Optional.of(em.merge(image));
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
