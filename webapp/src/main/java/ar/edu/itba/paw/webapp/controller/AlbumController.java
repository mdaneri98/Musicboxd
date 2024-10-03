package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.AlbumReview;
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
import java.util.logging.Logger;

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
    public ModelAndView redirect() {
        return new ModelAndView("redirect:/");
    }

    @RequestMapping("/{albumId:\\d+}")
    public ModelAndView album(@PathVariable(name = "albumId") long albumId,
                              @RequestParam(name = "pageNum", required = false) Integer pageNum,
                              @ModelAttribute("loggedUser") User loggedUser) {
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }

        final ModelAndView mav = new ModelAndView("album");
        int pageSize = 5;

        Album album = albumService.findById(albumId).orElseThrow();
        List<Song> songs = songService.findByAlbumId(albumId);

        List<AlbumReview> reviews = reviewService.findAlbumReviewsPaginated(albumId, pageNum, pageSize, loggedUser.getId());

        // Determinar si el usuario ya reseñó el álbum
        boolean isReviewed = reviewService.hasUserReviewedAlbum(loggedUser.getId(), albumId);
        Integer loggedUserRating = isReviewed ? reviewService.findAlbumReviewByUserId(loggedUser.getId(), albumId).get().getRating() : 0;

        // Determinar si mostrar botones "Next" y "Previous"
        boolean showNext = reviews.size() == pageSize;  // Mostrar "Next" si hay más reseñas
        boolean showPrevious = pageNum > 1;  // Mostrar "Previous" si no estamos en la primera página

        // Añadir los objetos al modelo
        mav.addObject("album", album);
        mav.addObject("songs", songs);
        mav.addObject("artist", album.getArtist());
        mav.addObject("reviews", reviews);
        mav.addObject("isFavorite", userService.isAlbumFavorite(loggedUser.getId(), albumId));
        mav.addObject("isReviewed", isReviewed);
        mav.addObject("loggedUserRating", loggedUserRating);
        mav.addObject("pageNum", pageNum);

        // Añadir los flags para mostrar los botones de navegación
        mav.addObject("showNext", showNext);
        mav.addObject("showPrevious", showPrevious);

        return mav;
    }

    @RequestMapping(value = "/{albumId:\\d+}/reviews", method = RequestMethod.GET)
    public ModelAndView createForm(@ModelAttribute("reviewForm") final ReviewForm reviewForm, @PathVariable Long albumId) {
        Album album = albumService.findById(albumId).orElseThrow();


        ModelAndView modelAndView = new ModelAndView("reviews/album_review");
        modelAndView.addObject("album", album);

        return modelAndView;
    }

    @RequestMapping(value = "/{albumId:\\d+}/reviews", method = RequestMethod.POST)
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
                0,
                false
        );
        reviewService.saveAlbumReview(albumReview);
        return new ModelAndView("redirect:/album/" + albumId);
    }

    @RequestMapping(value = "/{albumId:\\d+}/add-favorite", method = RequestMethod.GET)
    public ModelAndView addFavorite(@ModelAttribute("loggedUser") User loggedUser, @PathVariable Long albumId) throws MessagingException {
        userService.addFavoriteAlbum(loggedUser.getId(), albumId);
        return new ModelAndView("redirect:/album/" + albumId);
    }

    @RequestMapping(value = "/{albumId:\\d+}/remove-favorite", method = RequestMethod.GET)
    public ModelAndView removeFavorite(@ModelAttribute("loggedUser") User loggedUser, @PathVariable Long albumId) throws MessagingException {
        userService.removeFavoriteAlbum(loggedUser.getId(), albumId);
        return new ModelAndView("redirect:/album/" + albumId);
    }
}
