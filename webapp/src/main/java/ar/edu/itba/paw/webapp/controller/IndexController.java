package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.Album;
import ar.edu.itba.paw.Artist;
import ar.edu.itba.paw.Song;
import ar.edu.itba.paw.reviews.AlbumReview;
import ar.edu.itba.paw.services.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/*
    No es un servlet.
 */

@Controller
public class IndexController {

    private final UserService userService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final SongService songService;

    private final AlbumReviewService albumReviewService;

    public IndexController(UserService userService, ArtistService artistService, AlbumService albumService, SongService songService, AlbumReviewService albumReviewService) {
        this.userService = userService;
        this.artistService = artistService;
        this.albumService = albumService;
        this.songService = songService;
        this.albumReviewService = albumReviewService;
    }

    @RequestMapping("/")
    public ModelAndView index() {
        final ModelAndView mav = new ModelAndView("index");

        List<AlbumReview> allReviews = albumReviewService.findAll();
        List<AlbumReview> albumReviews = allReviews.subList(0, Math.min(5, allReviews.size()));

        /* A cada Reseña, le agrego la imagen del album */
        Map<AlbumReview, Long> reviewsWithImg = new HashMap<>();
        for (AlbumReview review : albumReviews) {
            Long albumId = review.getAlbumId();
            Album album = albumService.findById(albumId).orElseThrow();
            Long imgId = album.getImgId();
            reviewsWithImg.put(review, imgId);
        }

        mav.addObject("artists", artistService.findAll());
        mav.addObject("reviewsWithImg", reviewsWithImg);
        return mav;
    }

    @RequestMapping("/song/{songId:\\d+}")
    public ModelAndView song(@PathVariable(name = "songId") long songId) {
        final ModelAndView mav = new ModelAndView("song");

        Song song = songService.findById(songId).get();
        List<Artist> artists = artistService.findBySongId(songId);
        Album album = albumService.findById(song.getAlbumId()).get();

        mav.addObject("album", album);
        mav.addObject("artists", artists);
        mav.addObject("song", song);
        return mav;
    }

    @RequestMapping("/review/album/{albumId:\\d+}")
    public ModelAndView reviewAlbum(@PathVariable(name = "albumId") long albumId) {
        final ModelAndView mav = new ModelAndView("reviews/album_review");
        Album album = albumService.findById(albumId).get();
        mav.addObject("album", album);
        return mav;
    }

    @RequestMapping("/review/song/{songId:\\d+}")
    public ModelAndView reviewSong(@PathVariable(name = "songId") long songId) {
        final ModelAndView mav = new ModelAndView("reviews/song_review");
        Song song = songService.findById(songId).get();
        mav.addObject("song", song);
        return mav;
    }
}
