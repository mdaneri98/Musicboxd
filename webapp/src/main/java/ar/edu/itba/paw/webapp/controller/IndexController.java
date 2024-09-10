package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.services.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    No es un servlet.
 */

@Controller
public class IndexController {
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final SongService songService;

    private final AlbumReviewService albumReviewService;

    public IndexController(ArtistService artistService, AlbumService albumService, SongService songService, AlbumReviewService albumReviewService) {
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
}
