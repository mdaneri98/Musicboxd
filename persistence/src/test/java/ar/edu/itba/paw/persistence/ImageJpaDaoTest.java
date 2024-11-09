package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:image_setUp.sql")
public class ImageJpaDaoTest {

    private static final long PRE_EXISTING_IMAGE_ID = 100;
    private static final long NEW_IMAGE_ID = 1000;

    private static final byte[] PRE_EXISTING_IMAGE = new byte[] { (byte) 0xbe, (byte) 0xef };
    private static final byte[] NEW_IMAGE = new byte[] { (byte) 0xde, (byte) 0xad };

    @Autowired
    private ImageJpaDao imageDao;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void test_findById() {
        // 1. Pre-conditions - image exists

        // 2. Execute
        Optional<Image> maybeImage = imageDao.findById(PRE_EXISTING_IMAGE_ID);

        // 3. Post-conditions
        assertTrue(maybeImage.isPresent());
        assertEquals(PRE_EXISTING_IMAGE_ID, maybeImage.get().getId().longValue());
        assertArrayEquals(PRE_EXISTING_IMAGE, maybeImage.get().getBytes());
    }

    @Test(expected = NoSuchElementException.class)
    public void test_findById_NoImage() {
        // 1. Pre-conditions - image does not exists

        // 2. Execute
        Optional<Image> maybeImage = imageDao.findById(NEW_IMAGE_ID);

        // 3. Post-conditions
        assertFalse(maybeImage.isPresent());
        assertEquals(NEW_IMAGE_ID, maybeImage.get().getId().longValue());
    }

    @Test
    public void test_create() {
        // 1. Pre-conditions - none
        Image image = new Image(NEW_IMAGE);

        // 2. Execute
        Image imageCreated = imageDao.create(image);

        // 3. Post-conditions
        assertNotNull(imageCreated);
        assertEquals(imageCreated.getId(), image.getId());
        assertEquals(imageCreated.getBytes(), image.getBytes());

        // check if image is saved correctly in database
        assertEquals(1, em.createQuery("SELECT COUNT(i) FROM Image i " +
                    "WHERE i.bytes = :bytes", Long.class)
                .setParameter("bytes", NEW_IMAGE)
                .getSingleResult().intValue());
    }

    @Test
    public void test_update() {
        // 1. Pre-conditions - image exist
        Image image = new Image(PRE_EXISTING_IMAGE_ID, NEW_IMAGE);

        // 2. Execute
        Optional<Image> maybeImage = imageDao.update(image);

        // 3. Post-conditions
        assertTrue(maybeImage.isPresent());
        assertEquals(PRE_EXISTING_IMAGE_ID, maybeImage.get().getId().longValue());
        assertArrayEquals(NEW_IMAGE, maybeImage.get().getBytes());

        // check if image is saved correctly in database
        assertEquals(1, em.createQuery("SELECT COUNT(i) FROM Image i " +
                        "WHERE i.id = :imageId " +
                          "AND i.bytes = :bytes",
                        Long.class)
                .setParameter("imageId", PRE_EXISTING_IMAGE_ID)
                .setParameter("bytes", NEW_IMAGE)
                .getSingleResult().intValue());
    }

    @Test
    public void test_update_NoImage() {
        // 1. Pre-conditions - image exist
        Image image = new Image(NEW_IMAGE_ID, NEW_IMAGE);

        // 2. Execute
        Optional<Image> maybeImage = imageDao.update(image);

        // 3. Post-conditions
        assertFalse(maybeImage.isPresent());
        assertNull(em.find(Image.class, NEW_IMAGE_ID));
    }

    @Test
    public void test_delete() {
        // 1. Pre-conditions - image exist

        // 2. Execute
        boolean deleted = imageDao.delete(PRE_EXISTING_IMAGE_ID);

        // 3. Post-conditions
        assertTrue(deleted);
        assertNull(em.find(Image.class, PRE_EXISTING_IMAGE_ID));
    }

    @Test
    public void test_delete_NoImage() {
        // 1. Pre-conditions - image exist

        // 2. Execute
        boolean deleted = imageDao.delete(NEW_IMAGE_ID);

        // 3. Post-conditions
        assertFalse(deleted);
        assertNull(em.find(Image.class, NEW_IMAGE_ID));
    }

    @Test
    public void test_exists_Yes() {
        // 1. Pre-conditions - image exist

        // 2. Execute
        boolean exists = imageDao.exists(PRE_EXISTING_IMAGE_ID);

        // 3. Post-conditions
        assertTrue(exists);
    }

    @Test
    public void test_exists_No() {
        // 1. Pre-conditions - image does not exist

        // 2. Execute
        boolean exists = imageDao.exists(NEW_IMAGE_ID);

        // 3. Post-conditions
        assertFalse(exists);
    }
}
