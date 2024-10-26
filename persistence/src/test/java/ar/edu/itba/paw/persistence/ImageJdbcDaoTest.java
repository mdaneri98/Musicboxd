package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:image_setUp.sql")
public class ImageJdbcDaoTest {

    private static final long PRE_EXISTING_IMAGE_ID = 100;
    private static final long NEW_IMAGE_ID = 1000;
    private static final byte[] PRE_EXISTING_IMAGE = new byte[] {(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF};

    @Autowired
    private ImageJpaDao imageDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

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

        // 2. Execute
        long imgId = 0;// imageDao.create(PRE_EXISTING_IMAGE);

        // 3. Post-conditions
        assertTrue(imgId > 0);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"image",
                String.format("id = '%d'", imgId)));
    }

    @Test
    public void test_update() {
        // 1. Pre-conditions - image exist

        // 2. Execute
        boolean updated = false;//imageDao.update(PRE_EXISTING_IMAGE_ID, PRE_EXISTING_IMAGE);

        // 3. Post-conditions
        assertTrue(updated);
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"image",
                String.format("id = '%d'", PRE_EXISTING_IMAGE_ID)));
    }

    @Test
    public void test_update_NoImage() {
        // 1. Pre-conditions - image exist

        // 2. Execute
        boolean updated = false; //imageDao.update(NEW_IMAGE_ID, PRE_EXISTING_IMAGE);

        // 3. Post-conditions
        assertFalse(updated);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"image",
                String.format("id = '%d'", NEW_IMAGE_ID)));
    }

    @Test
    public void test_delete() {
        // 1. Pre-conditions - image exist

        // 2. Execute
        boolean deleted = imageDao.delete(PRE_EXISTING_IMAGE_ID);

        // 3. Post-conditions
        assertTrue(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"image",
                String.format("id = '%d'", PRE_EXISTING_IMAGE_ID)));
    }

    @Test
    public void test_delete_NoImage() {
        // 1. Pre-conditions - image exist

        // 2. Execute
        boolean deleted = imageDao.delete(NEW_IMAGE_ID);

        // 3. Post-conditions
        assertFalse(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"image",
                String.format("id = '%d'", NEW_IMAGE_ID)));
    }
}
