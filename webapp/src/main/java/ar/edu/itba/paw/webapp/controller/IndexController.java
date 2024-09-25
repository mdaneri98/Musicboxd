package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.services.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class IndexController {
    private final ArtistService artistService;
    private final ReviewService reviewService;
    private final UserService userService;
    private final SongService songService;
    private final AlbumService albumService;

    public IndexController(ArtistService artistService, ReviewService reviewService, UserService userService, SongService songService, AlbumService albumService) {
        this.artistService = artistService;
        this.reviewService = reviewService;
        this.userService = userService;
        this.songService = songService;
        this.albumService = albumService;
    }

    @RequestMapping("/")
    public ModelAndView index(@ModelAttribute("loggedUser") User loggedUser) {
        return home(loggedUser);
    }

    @RequestMapping("/search")
    public ModelAndView search(@ModelAttribute("loggedUser") User loggedUser) {
        final ModelAndView mav = new ModelAndView("search");
        mav.addObject("artists", artistService.findAll());
        mav.addObject("albums", albumService.findAll());
        mav.addObject("songs", songService.findAll());
        mav.addObject("users", userService.findAll());
        return mav;
    }

    @RequestMapping("/home")
    public ModelAndView home(@ModelAttribute("loggedUser") User loggedUser) {
        return home(loggedUser, 1);
    }

    @RequestMapping("/home/{pageNum:\\d+}")
    public ModelAndView home(@ModelAttribute("loggedUser") User loggedUser, @PathVariable(name = "pageNum", required = false) Integer pageNum) {
        final ModelAndView mav = new ModelAndView("home");

        List<Review> popularReviews = reviewService.getPopularReviewsNDaysPaginated(30,pageNum, 10, loggedUser.getId());
        List<Review> followingReviews = reviewService.getReviewsFromFollowedUsersPaginated(loggedUser.getId(), pageNum, 10, loggedUser.getId());

        mav.addObject("popularReviews", popularReviews);
        mav.addObject("followingReviews", followingReviews);
        mav.addObject("pageNum", pageNum);

        return mav;
    }
}
