package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import ar.edu.itba.paw.persistence.ArtistDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ArtistServiceImplTest {

    private static final long ID = 1;
    private static final String NAME = "DummyName";
    private static final String BIO = "DummyBio";
    private static final int RATING_COUNT = 0;
    private static final float AVG_RATING = 0;

    private static final long IMG_ID = 1;

    @InjectMocks
    private ArtistServiceImpl artistService;

    @Mock
    private ArtistDao mock;

    @Test
    public void test_create() {
        // 1. Pre-conditions -
//        Mockito.when(mock.create(equals())).thenReturn(new Artist(ID,NAME, BIO, IMG_ID, RATING_COUNT,AVG_RATING));

        // 2. Execute
//        Artist artist = artistService.create();

        // 3. Post-conditions
//        assertNotNull(artist);
//        assertEquals(NAME, artist.getName());
//        assertEquals(BIO, artist.getBio());
    }

    @Test
    public void test_delete() {
        // 1. Pre-conditions -


        // 2. Execute


        // 3. Post-conditions
    }
}
