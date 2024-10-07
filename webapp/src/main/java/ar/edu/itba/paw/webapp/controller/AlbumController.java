package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.form.ReviewForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequestMapping("/album")
@Controller
public class AlbumController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlbumController.class);

    private final UserService userService;
    private final AlbumService albumService;
    private final SongService songService;
    private final ReviewService reviewService;
    private final MessageSource messageSource;

    public AlbumController(UserService userService, AlbumService albumService, SongService songService, ReviewService reviewService, MessageSource messageSource) {
        this.userService = userService;
        this.albumService = albumService;
        this.songService = songService;
        this.reviewService = reviewService;
        this.messageSource = messageSource;
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

        Optional<Album> albumOptional = albumService.find(albumId);
        if (albumOptional.isEmpty()) {
            String errorMessage = messageSource.getMessage("error.album.find", null, LocaleContextHolder.getLocale());
            return new ModelAndView("redirect:/?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        }

        Album album = albumOptional.get();
        List<Song> songs = songService.findByAlbumId(albumId);

        List<AlbumReview> reviews = reviewService.findAlbumReviewsPaginated(albumId, pageNum, pageSize, loggedUser.getId());

        boolean isReviewed = reviewService.hasUserReviewedAlbum(loggedUser.getId(), albumId);
        Integer loggedUserRating = isReviewed ? reviewService.findAlbumReviewByUserId(loggedUser.getId(), albumId).get().getRating() : 0;

        boolean showNext = reviews.size() == pageSize;
        boolean showPrevious = pageNum > 1;

        mav.addObject("album", album);
        mav.addObject("songs", songs);
        mav.addObject("artist", album.getArtist());
        mav.addObject("reviews", reviews);
        mav.addObject("isFavorite", userService.isAlbumFavorite(loggedUser.getId(), albumId));
        mav.addObject("isReviewed", isReviewed);
        mav.addObject("loggedUserRating", loggedUserRating);
        mav.addObject("pageNum", pageNum);

        mav.addObject("showNext", showNext);
        mav.addObject("showPrevious", showPrevious);

        return mav;
    }

    @RequestMapping(value = "/{albumId:\\d+}/reviews", method = RequestMethod.GET)
    public ModelAndView createForm(@ModelAttribute("loggedUser") User loggedUser, @ModelAttribute("reviewForm") final ReviewForm reviewForm, @PathVariable Long albumId) {
        if (!reviewService.findAlbumReviewByUserId(loggedUser.getId(), albumId).isEmpty())
            return new ModelAndView("redirect:/album/" + albumId);

        Album album = albumService.find(albumId).orElseThrow();

        ModelAndView mav = new ModelAndView("reviews/album_review");
        mav.addObject("album", album);
        mav.addObject("edit", false);


        return mav;
    }

    @RequestMapping(value = "/{albumId:\\d+}/edit-review", method = RequestMethod.GET)
    public ModelAndView editAlbumReview(@ModelAttribute("loggedUser") User loggedUser, @ModelAttribute("reviewForm") final ReviewForm reviewForm, @PathVariable Long albumId) {
        if (reviewService.findAlbumReviewByUserId(loggedUser.getId(), albumId).isEmpty())
            return createForm(loggedUser, reviewForm, albumId);

        AlbumReview review = reviewService.findAlbumReviewByUserId(loggedUser.getId(), albumId).get();

        reviewForm.setTitle(review.getTitle());
        reviewForm.setDescription(review.getDescription());
        reviewForm.setRating(review.getRating());

        ModelAndView mav = new ModelAndView("reviews/album_review");
        mav.addObject("album", review.getAlbum());
        mav.addObject("reviewForm", reviewForm);
        mav.addObject("edit", true);


        return mav;
    }

    @RequestMapping(value = "/{albumId:\\d+}/edit-review", method = RequestMethod.POST)
    public ModelAndView editAlbumReview(@Valid @ModelAttribute("reviewForm") final ReviewForm reviewForm, final BindingResult errors, @ModelAttribute("loggedUser") User loggedUser, @PathVariable Long albumId, Model model) throws MessagingException {
        if (errors.hasErrors()) {
            return createForm(loggedUser, reviewForm, albumId);
        }
        AlbumReview review = reviewService.findAlbumReviewByUserId(loggedUser.getId(), albumId).get();

        AlbumReview albumReview = new AlbumReview(
                review.getId(),
                loggedUser,
                new Album(albumId),
                reviewForm.getTitle(),
                reviewForm.getDescription(),
                reviewForm.getRating(),
                LocalDateTime.now(),
                review.getLikes(),
                review.isBlocked()
        );
        reviewService.updateAlbumReview(albumReview);
        LOGGER.info("Album review updated for album ID {} by user ID {}", albumId, loggedUser.getId());
        return new ModelAndView("redirect:/album/" + albumId);
    }

    @RequestMapping(value = "/{albumId:\\d+}/reviews", method = RequestMethod.POST)
    public ModelAndView create(@Valid @ModelAttribute("reviewForm") final ReviewForm reviewForm, final BindingResult errors, @ModelAttribute("loggedUser") User loggedUser, @PathVariable Long albumId) throws MessagingException {
        if (errors.hasErrors()) {
            return createForm(loggedUser, reviewForm, albumId);
        }

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
        LOGGER.info("New album review created for album ID {} by user ID {}", albumId, loggedUser.getId());
        return new ModelAndView("redirect:/album/" + albumId);
    }

    @RequestMapping(value = "/{albumId:\\d+}/delete-review", method = RequestMethod.GET)
    public ModelAndView delete(@Valid @ModelAttribute("reviewForm") final ReviewForm reviewForm, final BindingResult errors, @ModelAttribute("loggedUser") User loggedUser, @PathVariable Long albumId) throws MessagingException {
        AlbumReview review = reviewService.findAlbumReviewByUserId(loggedUser.getId(), albumId).get();
        reviewService.deleteReview(review, loggedUser.getId());
        LOGGER.info("Album review deleted for album ID {} by user ID {}", albumId, loggedUser.getId());
        return new ModelAndView("redirect:/album/" + albumId);
    }

    @RequestMapping(value = "/{albumId:\\d+}/add-favorite", method = RequestMethod.GET)
    public ModelAndView addFavorite(@ModelAttribute("loggedUser") User loggedUser, @PathVariable Long albumId) throws MessagingException {
        userService.addFavoriteAlbum(loggedUser.getId(), albumId);
        LOGGER.info("Album ID {} added to favorites by user ID {}", albumId, loggedUser.getId());
        return new ModelAndView("redirect:/album/" + albumId);
    }

    @RequestMapping(value = "/{albumId:\\d+}/remove-favorite", method = RequestMethod.GET)
    public ModelAndView removeFavorite(@ModelAttribute("loggedUser") User loggedUser, @PathVariable Long albumId) throws MessagingException {
        userService.removeFavoriteAlbum(loggedUser.getId(), albumId);
        LOGGER.info("Album ID {} removed from favorites by user ID {}", albumId, loggedUser.getId());
        return new ModelAndView("redirect:/album/" + albumId);
    }
}
