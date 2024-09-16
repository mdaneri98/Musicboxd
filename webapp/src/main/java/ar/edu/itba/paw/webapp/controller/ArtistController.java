package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.form.ReviewForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RequestMapping ("/artist")
@Controller
public class ArtistController {

    private final UserService userService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final SongService songService;
    private final ReviewService reviewService;

    public ArtistController(UserService userService, ArtistService artistService, AlbumService albumService, SongService songService, ReviewService reviewService) {
        this.userService = userService;
        this.artistService = artistService;
        this.albumService = albumService;
        this.songService = songService;
        this.reviewService = reviewService;
    }

    @RequestMapping("/")
    public ModelAndView findAll() {
        //TODO: Redirigir a algun artista.
        return new ModelAndView("redirect:/");
    }

    @RequestMapping("/{artistId:\\d+}")
    public ModelAndView artist(@PathVariable(name = "artistId") long artistId, @ModelAttribute("loggedUser") User loggedUser) {
        final ModelAndView mav = new ModelAndView("artist");

        Artist artist = artistService.findById(artistId).get();
        List<Album> albums = albumService.findByArtistId(artistId);
        List<Song> songs = songService.findByArtistId(artistId);
        List<ArtistReview> reviews = reviewService.findReviewsByArtistId(artistId);

        mav.addObject("artist", artist);
        mav.addObject("albums", albums);
        mav.addObject("songs", songs);
        mav.addObject("reviews", reviews);
        mav.addObject("isFavorite", userService.isArtistFavorite(loggedUser.getId(), artistId));
        return mav;
    }

    @RequestMapping(value = "/{artistId:\\d}/reviews", method = RequestMethod.GET)
    public ModelAndView createForm(@ModelAttribute("reviewForm") final ReviewForm reviewForm, @PathVariable Long artistId) {
        Optional<Artist> artistOptional = artistService.findById(artistId);
        if (artistOptional.isEmpty())
            return null;

        Artist artist = artistOptional.get();

        ModelAndView modelAndView = new ModelAndView("reviews/artist_review");
        modelAndView.addObject("artist", artist);

        return modelAndView;
    }

    @RequestMapping(value = "/{artistId:\\d}/reviews", method = RequestMethod.POST)
    public ModelAndView create(@Valid @ModelAttribute("reviewForm") final ReviewForm reviewForm, @ModelAttribute("loggedUser") User loggedUser, final BindingResult errors, @PathVariable Long artistId) throws MessagingException {
        if (errors.hasErrors()) {
            return createForm(reviewForm, artistId);
        }


        userService.incrementReviewAmount(loggedUser);
        ArtistReview artistReview = new ArtistReview(
                loggedUser,
                new Artist(artistId),
                reviewForm.getTitle(),
                reviewForm.getDescription(),
                reviewForm.getRating(),
                LocalDateTime.now(),
                0
        );
        reviewService.saveArtistReview(artistReview);
        return new ModelAndView("redirect:/artist/" + artistId);
    }

    @RequestMapping(value = "/{artistId:\\d}/add-favorite", method = RequestMethod.GET)
    public ModelAndView addFavorite(@ModelAttribute("loggedUser") User loggedUser, @PathVariable Long artistId) throws MessagingException {
        userService.addFavoriteArtist(loggedUser.getId(), artistId);
        return new ModelAndView("redirect:/artist/" + artistId);
    }

    @RequestMapping(value = "/{artistId:\\d}/remove-favorite", method = RequestMethod.GET)
    public ModelAndView removeFavorite(@ModelAttribute("loggedUser") User loggedUser, @PathVariable Long artistId) throws MessagingException {
        userService.removeFavoriteArtist(loggedUser.getId(), artistId);
        return new ModelAndView("redirect:/artist/" + artistId);
    }
}
