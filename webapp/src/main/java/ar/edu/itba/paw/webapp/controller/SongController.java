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
import org.springframework.web.bind.annotation.*;
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

    @RequestMapping("/{songId}")
    public ModelAndView song(@PathVariable(name = "songId") long songId,
                             @RequestParam(name = "pageNum", required = false) Integer pageNum,
                             @ModelAttribute("loggedUser") User loggedUser) {
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }

        final ModelAndView mav = new ModelAndView("song");
        int pageSize = 5;  // Tamaño de la página para las reseñas

        Song song = songService.find(songId).get();
        List<Artist> artists = artistService.findBySongId(songId);

        // Obtener las reseñas de la canción de manera paginada
        List<SongReview> reviews = reviewService.findSongReviewsPaginated(songId, pageNum, pageSize, loggedUser.getId());

        // Determinar si el usuario ya reseñó la canción
        boolean isReviewed = reviewService.hasUserReviewedSong(loggedUser.getId(), songId);
        Integer loggedUserRating = isReviewed ? reviewService.findSongReviewByUserId(loggedUser.getId(), songId).get().getRating() : 0;

        // Determinar si mostrar botones "Next" y "Previous"
        boolean showNext = reviews.size() == pageSize;  // Mostrar "Next" si hay más reseñas
        boolean showPrevious = pageNum > 1;  // Mostrar "Previous" si no estamos en la primera página

        // Añadir los objetos al modelo
        mav.addObject("album", song.getAlbum());
        mav.addObject("artists", artists);
        mav.addObject("song", song);
        mav.addObject("reviews", reviews);
        mav.addObject("isFavorite", userService.isSongFavorite(loggedUser.getId(), songId));
        mav.addObject("isReviewed", isReviewed);
        mav.addObject("loggedUserRating", loggedUserRating);
        mav.addObject("pageNum", pageNum);

        // Añadir los flags para mostrar los botones de navegación
        mav.addObject("showNext", showNext);
        mav.addObject("showPrevious", showPrevious);

        return mav;
    }


    @RequestMapping(value = "/{songId:\\d+}/reviews", method = RequestMethod.GET)
    public ModelAndView createForm(@ModelAttribute("reviewForm") final ReviewForm reviewForm, @PathVariable Long songId) {
        final ModelAndView mav = new ModelAndView("reviews/song_review");
        Song song = songService.find(songId).get();
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
                0,
                false
        );
        reviewService.saveSongReview(songReview);
        return new ModelAndView("redirect:/song/" + songId);
    }

    @RequestMapping(value = "/{songId:\\d+}/add-favorite", method = RequestMethod.GET)
    public ModelAndView addFavorite(@ModelAttribute("loggedUser") User loggedUser, @PathVariable Long songId) throws MessagingException {
        userService.addFavoriteSong(loggedUser.getId(), songId);
        return new ModelAndView("redirect:/song/" + songId);
    }

    @RequestMapping(value = "/{songId:\\d+}/remove-favorite", method = RequestMethod.GET)
    public ModelAndView removeFavorite(@ModelAttribute("loggedUser") User loggedUser, @PathVariable Long songId) throws MessagingException {
        userService.removeFavoriteSong(loggedUser.getId(), songId);
        return new ModelAndView("redirect:/song/" + songId);
    }
}
