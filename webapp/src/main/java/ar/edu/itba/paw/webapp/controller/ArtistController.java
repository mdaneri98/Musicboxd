package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.ArtistReview;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RequestMapping ("/artist")
@Controller
public class ArtistController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArtistController.class);

    private final UserService userService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final SongService songService;
    private final ReviewService reviewService;
    private final MessageSource messageSource;

    public ArtistController(UserService userService, ArtistService artistService, AlbumService albumService, SongService songService, ReviewService reviewService, MessageSource messageSource) {
        this.userService = userService;
        this.artistService = artistService;
        this.albumService = albumService;
        this.songService = songService;
        this.reviewService = reviewService;
        this.messageSource = messageSource;
    }

    @RequestMapping("/")
    public ModelAndView findAll() {
        return new ModelAndView("redirect:/");
    }

    @RequestMapping("/{artistId:\\d+}")
    public ModelAndView artist(@PathVariable(name = "artistId") long artistId,
                               @RequestParam(name = "pageNum", required = false) Integer pageNum ,
                               @ModelAttribute("loggedUser") User loggedUser,
                               @RequestParam(name = "error", required = false) String error) {
        int pageSize = 5;
        final ModelAndView mav = new ModelAndView("artist");

        if (pageNum == null || pageNum <= 0) pageNum = 1;

        Optional<Artist> artistOptional = artistService.find(artistId);
        if (artistOptional.isEmpty()) {
            String errorMessage = messageSource.getMessage("error.artist.find", null, LocaleContextHolder.getLocale());
            return new ModelAndView("redirect:/?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        }

        Artist artist = artistOptional.get();
        List<Album> albums = albumService.findByArtistId(artistId);
        List<Song> songs = songService.findByArtistId(artistId);
        List<ArtistReview> reviews = reviewService.findArtistReviewsPaginated(artistId,pageNum,5, loggedUser.getId());
        boolean isReviewed = reviewService.hasUserReviewedArtist(loggedUser.getId(), artistId);
        Integer loggedUserRating = isReviewed ? reviewService.findArtistReviewByUserId(loggedUser.getId(), artistId).get().getRating() : 0;
        boolean showNext = reviews.size() == pageSize;
        boolean showPrevious = pageNum > 1;

        mav.addObject("artist", artist);
        mav.addObject("albums", albums);
        mav.addObject("songs", songs);
        mav.addObject("reviews", reviews);
        mav.addObject("isFavorite", userService.isArtistFavorite(loggedUser.getId(), artistId));
        mav.addObject("isReviewed", isReviewed);
        mav.addObject("loggedUserRating", loggedUserRating);
        mav.addObject("pageNum", pageNum);
        mav.addObject("error", error);
        mav.addObject("showNext", showNext);
        mav.addObject("showPrevious", showPrevious);

        return mav;
    }

    @RequestMapping(value = "/{artistId:\\d+}/reviews", method = RequestMethod.GET)
    public ModelAndView createForm(@ModelAttribute("loggedUser") User loggedUser, @ModelAttribute("reviewForm") final ReviewForm reviewForm, @PathVariable Long artistId) {
        if (!reviewService.findArtistReviewByUserId(loggedUser.getId(), artistId).isEmpty())
            return new ModelAndView("redirect:/artist/" + artistId);

        Optional<Artist> artistOptional = artistService.find(artistId);
        if (artistOptional.isEmpty())
            return null;

        Artist artist = artistOptional.get();

        ModelAndView mav = new ModelAndView("reviews/artist_review");
        mav.addObject("artist", artist);
        mav.addObject("edit", false);


        return mav;
    }

    @RequestMapping(value = "/{artistId:\\d+}/edit-review", method = RequestMethod.GET)
    public ModelAndView editAlbumReview(@ModelAttribute("loggedUser") User loggedUser, @ModelAttribute("reviewForm") final ReviewForm reviewForm, @PathVariable Long artistId) {
        if (reviewService.findArtistReviewByUserId(loggedUser.getId(), artistId).isEmpty())
            return createForm(loggedUser, reviewForm, artistId);

        ArtistReview review = reviewService.findArtistReviewByUserId(loggedUser.getId(), artistId).get();

        reviewForm.setTitle(review.getTitle());
        reviewForm.setDescription(review.getDescription());
        reviewForm.setRating(review.getRating());

        ModelAndView mav = new ModelAndView("reviews/artist_review");
        mav.addObject("artist", review.getArtist());
        mav.addObject("reviewForm", reviewForm);
        mav.addObject("edit", true);


        return mav;
    }

    @RequestMapping(value = "/{artistId:\\d+}/edit-review", method = RequestMethod.POST)
    public ModelAndView editArtistReview(@Valid @ModelAttribute("reviewForm") final ReviewForm reviewForm, final BindingResult errors, @ModelAttribute("loggedUser") User loggedUser, @PathVariable Long artistId, Model model) throws MessagingException {
        if (errors.hasErrors()) {
            return createForm(loggedUser, reviewForm, artistId);
        }
        ArtistReview review = reviewService.findArtistReviewByUserId(loggedUser.getId(), artistId).get();

        ArtistReview artistReview = new ArtistReview(
                review.getId(),
                new Artist(artistId),
                loggedUser,
                reviewForm.getTitle(),
                reviewForm.getDescription(),
                reviewForm.getRating(),
                LocalDateTime.now(),
                review.getLikes(),
                review.isBlocked()
        );
        reviewService.updateArtistReview(artistReview);
        LOGGER.info("Artist review updated for artist ID {} by user ID {}", artistId, loggedUser.getId());
        return new ModelAndView("redirect:/artist/" + artistId);
    }

    @RequestMapping(value = "/{artistId:\\d+}/reviews", method = RequestMethod.POST)
    public ModelAndView create(@Valid @ModelAttribute("reviewForm") final ReviewForm reviewForm, final BindingResult errors, @ModelAttribute("loggedUser") User loggedUser, @PathVariable Long artistId) throws MessagingException {
        if (errors.hasErrors()) {
            return createForm(loggedUser, reviewForm, artistId);
        }
        ArtistReview artistReview = new ArtistReview(
                loggedUser,
                new Artist(artistId),
                reviewForm.getTitle(),
                reviewForm.getDescription(),
                reviewForm.getRating(),
                LocalDateTime.now(),
                0,
                false
        );
        reviewService.saveArtistReview(artistReview);
        LOGGER.info("New artist review created for artist ID {} by user ID {}", artistId, loggedUser.getId());
        return new ModelAndView("redirect:/artist/" + artistId);
    }

    @RequestMapping(value = "/{artistId:\\d+}/delete-review", method = RequestMethod.GET)
    public ModelAndView delete(@Valid @ModelAttribute("reviewForm") final ReviewForm reviewForm, final BindingResult errors, @ModelAttribute("loggedUser") User loggedUser, @PathVariable Long artistId) throws MessagingException {
        ArtistReview review = reviewService.findArtistReviewByUserId(loggedUser.getId(), artistId).get();
        reviewService.deleteReview(review, loggedUser.getId());
        LOGGER.info("Artist review deleted for artist ID {} by user ID {}", artistId, loggedUser.getId());
        return new ModelAndView("redirect:/artist/" + artistId);
    }

    @RequestMapping(value = "/{artistId:\\d+}/add-favorite", method = RequestMethod.GET)
    public ModelAndView addFavorite(@ModelAttribute("loggedUser") User loggedUser, @PathVariable Long artistId) throws MessagingException {
        if(userService.addFavoriteArtist(loggedUser.getId(), artistId))
            LOGGER.info("Artist ID {} added to favorites by user ID {}", artistId, loggedUser.getId());
        else {
            String errorMessage = messageSource.getMessage("error.too.many.favorites.artist", null, LocaleContextHolder.getLocale());
            return new ModelAndView("redirect:/artist/" + artistId + "?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        }
        return new ModelAndView("redirect:/artist/" + artistId);
    }

    @RequestMapping(value = "/{artistId:\\d+}/remove-favorite", method = RequestMethod.GET)
    public ModelAndView removeFavorite(@ModelAttribute("loggedUser") User loggedUser, @PathVariable Long artistId) throws MessagingException {
        userService.removeFavoriteArtist(loggedUser.getId(), artistId);
        LOGGER.info("Artist ID {} removed from favorites by user ID {}", artistId, loggedUser.getId());
        return new ModelAndView("redirect:/artist/" + artistId);
    }
}
