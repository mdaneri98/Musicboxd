package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.form.ReviewForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/album")
@Controller
public class AlbumController {

    private final UserService userService;
    private final AlbumService albumService;
    private final SongService songService;
    private final ReviewService reviewService;

    public AlbumController(UserService userService, AlbumService albumService, SongService songService, ReviewService reviewService) {
        this.userService = userService;
        this.albumService = albumService;
        this.songService = songService;
        this.reviewService = reviewService;
    }

    @RequestMapping("/")
    public ModelAndView findAll() {
        //TODO: Redirigir a algun artista.
        return new ModelAndView("redirect:/");
    }

    @RequestMapping("/{albumId:\\d+}")
    public ModelAndView album(@PathVariable(name = "albumId") long albumId) {
        final ModelAndView mav = new ModelAndView("album");

        Album album = albumService.findById(albumId).orElseThrow();
        List<Song> songs = songService.findByAlbumId(albumId);
        List<AlbumReview> reviews = reviewService.findReviewsByAlbumId(albumId);

        mav.addObject("album", album);
        mav.addObject("songs", songs);
        mav.addObject("artist", album.getArtist());
        mav.addObject("reviews", reviews);

        return mav;
    }

    @RequestMapping(value = "/{albumId}/reviews", method = RequestMethod.GET)
    public ModelAndView createForm(@ModelAttribute("reviewForm") final ReviewForm reviewForm, @PathVariable Long albumId) {
        Album album = albumService.findById(albumId).orElseThrow();


        ModelAndView modelAndView = new ModelAndView("reviews/album_review");
        modelAndView.addObject("album", album);

        return modelAndView;
    }

    @RequestMapping(value = "/{albumId}/reviews", method = RequestMethod.POST)
    public ModelAndView create(@Valid @ModelAttribute("reviewForm") final ReviewForm reviewForm, @ModelAttribute("loggedUser") User loggedUser, final BindingResult errors, @PathVariable Long albumId) throws MessagingException {
        if (errors.hasErrors()) {
            return createForm(reviewForm, albumId);
        }

        userService.incrementReviewAmount(loggedUser);
        AlbumReview albumReview = new AlbumReview(
                loggedUser,
                new Album(albumId),
                reviewForm.getTitle(),
                reviewForm.getDescription(),
                reviewForm.getRating(),
                LocalDateTime.now(),
                0
        );
        reviewService.saveAlbumReview(albumReview);
        return new ModelAndView("redirect:/");
    }

}
