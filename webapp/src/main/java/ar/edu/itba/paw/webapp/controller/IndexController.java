package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.services.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    private final ReviewService reviewService;

    public IndexController(ArtistService artistService, ReviewService reviewService) {
        this.artistService = artistService;
        this.reviewService = reviewService;
    }

    @RequestMapping("/")
    public ModelAndView index(@ModelAttribute("loggedUser") User loggedUser) {
        return home(loggedUser);
    }

    @RequestMapping("/music")
    public ModelAndView music(@ModelAttribute("loggedUser") User loggedUser) {
        final ModelAndView mav = new ModelAndView("music");
        mav.addObject("artists", artistService.findAll());
        return mav;
    }

    @RequestMapping("/home")
    public ModelAndView home(@ModelAttribute("loggedUser") User loggedUser) {
        return home(loggedUser, 1);
    }

    @RequestMapping("/home/{pageNum:\\d+}")
    public ModelAndView home(@ModelAttribute("loggedUser") User loggedUser, @PathVariable(name = "pageNum", required = false) Integer pageNum) {
        final ModelAndView mav = new ModelAndView("home");

        List<Review> popularReviews = reviewService.getPopularReviewsNDaysPaginated(30,pageNum, 10);
        List<Review> followingReviews = reviewService.getReviewsFromFollowedUsersPaginated(loggedUser.getId(), pageNum, 10);

        mav.addObject("popularReviews", popularReviews);
        mav.addObject("followingReviews", followingReviews);

        return mav;
    }
}
