package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.form.ReviewForm;
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

@RequestMapping("/song")
@Controller
public class SongController {

    private final UserService userService;
    private final ArtistService artistService;
    private final SongService songService;
    private final ReviewService reviewService;
    private final MessageSource messageSource;


    public SongController(UserService userService, ArtistService artistService, SongService songService, ReviewService reviewService, MessageSource messageSource) {
        this.userService = userService;
        this.artistService = artistService;
        this.songService = songService;
        this.reviewService = reviewService;
        this.messageSource = messageSource;
    }

    @RequestMapping("/")
    public ModelAndView findAll() {
        return new ModelAndView("redirect:/");
    }

    @RequestMapping("/{songId}")
    public ModelAndView song(@PathVariable(name = "songId") long songId,
                             @RequestParam(name = "pageNum", required = false) Integer pageNum,
                             @ModelAttribute("loggedUser") User loggedUser,
                             @RequestParam(name="error", required = false) String error) {
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }

        final ModelAndView mav = new ModelAndView("song");
        int pageSize = 5; 

        Song song = songService.find(songId).get();
        List<Artist> artists = artistService.findBySongId(songId);
        List<SongReview> reviews = reviewService.findSongReviewsPaginated(songId, pageNum, pageSize, loggedUser.getId());

        boolean isReviewed = reviewService.hasUserReviewedSong(loggedUser.getId(), songId);
        Integer loggedUserRating = isReviewed ? reviewService.findSongReviewByUserId(loggedUser.getId(), songId, loggedUser.getId()).get().getRating() : 0;

        boolean showNext = reviews.size() == pageSize;
        boolean showPrevious = pageNum > 1;

        mav.addObject("album", song.getAlbum());
        mav.addObject("artists", artists);
        mav.addObject("song", song);
        mav.addObject("reviews", reviews);
        mav.addObject("isFavorite", userService.isSongFavorite(loggedUser.getId(), songId));
        mav.addObject("isReviewed", isReviewed);
        mav.addObject("loggedUserRating", loggedUserRating);
        mav.addObject("pageNum", pageNum);
        mav.addObject("error", error);

        mav.addObject("showNext", showNext);
        mav.addObject("showPrevious", showPrevious);

        return mav;
    }


    @RequestMapping(value = "/{songId:\\d+}/reviews", method = RequestMethod.GET)
    public ModelAndView createForm(@ModelAttribute("loggedUser") User loggedUser, @ModelAttribute("reviewForm") final ReviewForm reviewForm, @PathVariable Long songId) {
        Optional<SongReview> reviewOptional = reviewService.findSongReviewByUserId(loggedUser.getId(), songId, loggedUser.getId());
        if (reviewOptional.isPresent())
            return new ModelAndView("redirect:/song/" + songId);

        final ModelAndView mav = new ModelAndView("reviews/song_review");
        Song song = songService.find(songId).get();
        mav.addObject("song", song);
        mav.addObject("album", song.getAlbum());
        mav.addObject("edit", false);

        return mav;
    }

    @RequestMapping(value = "/{songId:\\d+}/edit-review", method = RequestMethod.GET)
    public ModelAndView editSongReview(@ModelAttribute("loggedUser") User loggedUser, @ModelAttribute("reviewForm") final ReviewForm reviewForm, @PathVariable Long songId) {
        Optional<SongReview> reviewOptional = reviewService.findSongReviewByUserId(loggedUser.getId(), songId, loggedUser.getId());
        if (reviewOptional.isEmpty())
            return createForm(loggedUser, reviewForm, songId);

        SongReview review = reviewOptional.get();
        reviewForm.setTitle(review.getTitle());
        reviewForm.setDescription(review.getDescription());
        reviewForm.setRating(review.getRating());

        ModelAndView mav = new ModelAndView("reviews/song_review");
        mav.addObject("song", review.getSong());
        mav.addObject("album", review.getSong().getAlbum());
        mav.addObject("reviewForm", reviewForm);
        mav.addObject("edit", true);

        return mav;
    }

    @RequestMapping(value = "/{songId:\\d+}/edit-review", method = RequestMethod.POST)
    public ModelAndView editSongReview(@Valid @ModelAttribute("reviewForm") final ReviewForm reviewForm, final BindingResult errors, @ModelAttribute("loggedUser") User loggedUser, @PathVariable Long songId, Model model) throws MessagingException {
        Optional<SongReview> reviewOptional = reviewService.findSongReviewByUserId(loggedUser.getId(), songId, loggedUser.getId());
        if (errors.hasErrors()) {
            return createForm(loggedUser, reviewForm, songId);
        }

        SongReview review = reviewOptional.get();
        review.setTitle(reviewForm.getTitle());
        review.setDescription(reviewForm.getDescription());
        review.setRating(reviewForm.getRating());
        review.setCreatedAt(LocalDateTime.now());
        reviewService.updateSongReview(review);
        return new ModelAndView("redirect:/song/" + songId);
    }


    @RequestMapping(value = "/{songId:\\d+}/reviews", method = RequestMethod.POST)
    public ModelAndView create(@Valid @ModelAttribute("reviewForm") final ReviewForm reviewForm, final BindingResult errors, @ModelAttribute("loggedUser") User loggedUser, @PathVariable Long songId, Model model) throws MessagingException {
        if (errors.hasErrors()) {
            return createForm(loggedUser, reviewForm, songId);
        }

        SongReview songReview = new SongReview(
                loggedUser,
                new Song(songId),
                reviewForm.getTitle(),
                reviewForm.getDescription(),
                reviewForm.getRating(),
                LocalDateTime.now(),
                0,
                false,
                0
        );
        reviewService.saveSongReview(songReview);
        return new ModelAndView("redirect:/song/" + songId);
    }

    @RequestMapping(value = "/{songId:\\d+}/delete-review", method = RequestMethod.GET)
    public ModelAndView delete(@Valid @ModelAttribute("reviewForm") final ReviewForm reviewForm, final BindingResult errors, @ModelAttribute("loggedUser") User loggedUser, @PathVariable Long songId, Model model) throws MessagingException {
        Optional<SongReview> reviewOptional = reviewService.findSongReviewByUserId(loggedUser.getId(), songId, loggedUser.getId());
        reviewService.delete(reviewOptional.get().getId());
        return new ModelAndView("redirect:/song/" + songId);
    }

    @RequestMapping(value = "/{songId:\\d+}/add-favorite", method = RequestMethod.GET)
    public ModelAndView addFavorite(@ModelAttribute("loggedUser") User loggedUser, @PathVariable Long songId) throws MessagingException {
        if(!userService.addFavoriteSong(loggedUser.getId(), songId)) {
            String errorMessage = messageSource.getMessage("error.too.many.favorites.song", null, LocaleContextHolder.getLocale());
            return new ModelAndView("redirect:/song/" + songId + "?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        }
        return new ModelAndView("redirect:/song/" + songId);
    }

    @RequestMapping(value = "/{songId:\\d+}/remove-favorite", method = RequestMethod.GET)
    public ModelAndView removeFavorite(@ModelAttribute("loggedUser") User loggedUser, @PathVariable Long songId) throws MessagingException {
        userService.removeFavoriteSong(loggedUser.getId(), songId);
        return new ModelAndView("redirect:/song/" + songId); 
    }
}
