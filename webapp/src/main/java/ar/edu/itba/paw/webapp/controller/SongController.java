package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.form.ReviewForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

@RequestMapping("/song")
@Controller
public class SongController {

    private final UserService userService;
    private final ArtistService artistService;
    private final SongService songService;
    private final ReviewService reviewService;


    public SongController(UserService userService, ArtistService artistService, SongService songService, ReviewService reviewService) {
        this.userService = userService;
        this.artistService = artistService;
        this.songService = songService;
        this.reviewService = reviewService;
    }

    @RequestMapping("/")
    public ModelAndView findAll() {
        //TODO: Redirigir a algun artista.
        return new ModelAndView("redirect:/");
    }

    @RequestMapping("/{songId:\\d+}")
    public ModelAndView song(@PathVariable(name = "songId") long songId) {
        final ModelAndView mav = new ModelAndView("song");

        Song song = songService.findById(songId).get();
        List<Artist> artists = artistService.findBySongId(songId);
        List<SongReview> reviews = reviewService.findReviewsBySongId(songId);

        mav.addObject("album", song.getAlbum());
        mav.addObject("artists", artists);
        mav.addObject("song", song);
        mav.addObject("reviews", reviews);
        return mav;
    }

    @RequestMapping(value = "/{songId:\\d+}/reviews", method = RequestMethod.GET)
    public ModelAndView createForm(@ModelAttribute("reviewForm") final ReviewForm reviewForm, @PathVariable Long songId) {
        final ModelAndView mav = new ModelAndView("reviews/song_review");
        Song song = songService.findById(songId).get();
        mav.addObject("song", song);
        mav.addObject("album", song.getAlbum());
        return mav;
    }


    @RequestMapping(value = "/{songId:\\d+}/reviews", method = RequestMethod.POST)
    public ModelAndView create(@Valid @ModelAttribute("reviewForm") final ReviewForm reviewForm, @ModelAttribute("loggedUser") User loggedUser, final BindingResult errors, @PathVariable Long songId, Model model) throws MessagingException {
        if (errors.hasErrors()) {
            return createForm(reviewForm, songId);
        }

        userService.incrementReviewAmount(loggedUser);
        SongReview songReview = new SongReview(
                loggedUser,
                new Song(songId),
                reviewForm.getTitle(),
                reviewForm.getDescription(),
                reviewForm.getRating(),
                LocalDateTime.now(),
                0
        );
        reviewService.saveSongReview(songReview);
        return new ModelAndView("redirect:/song/" + songId);
    }

    @RequestMapping(value = "/{songId:\\d}/add-favorite", method = RequestMethod.GET)
    public ModelAndView addFavorite(@ModelAttribute("loggedUser") User loggedUser, @PathVariable Long songId) throws MessagingException {
        userService.addFavoriteSong(loggedUser.getId(), songId);
        return new ModelAndView("redirect:/album/" + songId);
    }

    @RequestMapping(value = "/{songId:\\d}/remove-favorite", method = RequestMethod.GET)
    public ModelAndView removeFavorite(@ModelAttribute("loggedUser") User loggedUser, @PathVariable Long songId) throws MessagingException {
        userService.removeFavoriteSong(loggedUser.getId(), songId);
        return new ModelAndView("redirect:/song/" + songId);
    }
}
