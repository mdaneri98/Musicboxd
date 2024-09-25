package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.SongReview;
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
import java.util.Optional;
import java.util.logging.Logger;

@RequestMapping("/review")
@Controller
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @RequestMapping("/")
    public ModelAndView redirect() {
        return new ModelAndView("redirect:/");
    }

    @RequestMapping("/{reviewId:\\d+}")
    public ModelAndView review (@ModelAttribute("loggedUser") User loggedUser,
                                      @PathVariable(name = "reviewId") long reviewId){
        if (reviewService.isArtistReview(reviewId)) return artistReview(loggedUser, reviewId);
        if (reviewService.isAlbumReview(reviewId)) return albumReview(loggedUser, reviewId);
        if (reviewService.isSongReview(reviewId)) return songReview(loggedUser, reviewId);
        return new ModelAndView("not_found/review_not_found");
    }


    @RequestMapping("/artist/{reviewId:\\d+}")
    public ModelAndView artistReview (@ModelAttribute("loggedUser") User loggedUser,
                               @PathVariable(name = "reviewId") long reviewId){
        ModelAndView mav = new ModelAndView("reviews/review");
        ArtistReview review = reviewService.findArtistReviewById(reviewId).get();
        review.setLiked(reviewService.isLiked(loggedUser.getId(), reviewId));

        mav.addObject("loggedUser", loggedUser);
        mav.addObject("review", review);
        return mav;
    }

    @RequestMapping("/album/{reviewId:\\d+}")
    public ModelAndView albumReview (@ModelAttribute("loggedUser") User loggedUser,
                                      @PathVariable(name = "reviewId") long reviewId){
        ModelAndView mav = new ModelAndView("reviews/review");
        AlbumReview review = reviewService.findAlbumReviewById(reviewId).get();
        review.setLiked(reviewService.isLiked(loggedUser.getId(), reviewId));

        mav.addObject("loggedUser", loggedUser);
        mav.addObject("review", review);
        return mav;
    }

    @RequestMapping("/song/{reviewId:\\d+}")
    public ModelAndView songReview (@ModelAttribute("loggedUser") User loggedUser,
                                     @PathVariable(name = "reviewId") long reviewId){
        ModelAndView mav = new ModelAndView("reviews/review");
        SongReview review = reviewService.findSongReviewById(reviewId).get();
        review.setLiked(reviewService.isLiked(loggedUser.getId(), reviewId));

        mav.addObject("loggedUser", loggedUser);
        mav.addObject("review", review);
        mav.addObject("isLiked", reviewService.isLiked(loggedUser.getId(), reviewId));
        return mav;
    }

    @RequestMapping(value = "/like/{reviewId:\\d+}", method = RequestMethod.GET)
    public ModelAndView createLike(@ModelAttribute("loggedUser") User loggedUser, @PathVariable(name = "reviewId") long reviewId) {
        reviewService.createLike(loggedUser.getId(), reviewId);
        return new ModelAndView("redirect:/review/" + reviewId);
    }

    @RequestMapping(value = "/remove-like/{reviewId:\\d+}", method = RequestMethod.GET)
    public ModelAndView removeLike(@ModelAttribute("loggedUser") User loggedUser, @PathVariable(name = "reviewId") long reviewId) {
        reviewService.removeLike(loggedUser.getId(), reviewId);
        return new ModelAndView("redirect:/review/" + reviewId);
    }
}
