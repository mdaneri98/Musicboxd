package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

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

    @RequestMapping(value = {"/home", "/"})
    public ModelAndView home(@ModelAttribute("loggedUser") User loggedUser, @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum) {
        if (loggedUser == null) {
            final ModelAndView mav = new ModelAndView("anonymus/home");

            List<Album> popularAlbums = albumService.findPaginated(FilterType.RATING, 5, 0);
            List<Review> popularReviews = reviewService.getPopularReviewsNDaysPaginated(30,pageNum, 10, 0);

            mav.addObject("popularAlbums", popularAlbums);
            mav.addObject("popularReviews", popularReviews);
            mav.addObject("pageNum", pageNum);

            return mav;
        }

        final ModelAndView mav = new ModelAndView("home");

        List<Review> popularReviews = reviewService.getPopularReviewsNDaysPaginated(30,pageNum, 10, loggedUser.getId());
        List<Review> followingReviews = reviewService.getReviewsFromFollowedUsersPaginated(loggedUser.getId(), pageNum, 10, loggedUser.getId());
        if (pageNum > 1 && popularReviews.isEmpty() && followingReviews.isEmpty()) return home(loggedUser, 1);

        mav.addObject("popularReviews", popularReviews);
        mav.addObject("followingReviews", followingReviews);
        mav.addObject("pageNum", pageNum);

        return mav;
    }

    @RequestMapping("/search")
    public ModelAndView search(@ModelAttribute("loggedUser") User loggedUser) {
        ModelAndView mav = new ModelAndView("search");

        List<Album> albums = albumService.findPaginated(FilterType.NEWEST,10, 0);
        List<Artist> artists = artistService.findPaginated(FilterType.RATING,10, 0);

        mav.addObject("top_albums", albums);
        mav.addObject("top_artists", artists);

        return mav;
    }

    @RequestMapping(value = "/search/{type}", method = RequestMethod.GET)
    @ResponseBody
    public String subSearch(@PathVariable("type") String type, @RequestParam(name = "s", defaultValue = "") String substringSearch) {
        logger.debug("Iniciando búsqueda en subSearch con type={} y substringSearch={}", type, substringSearch);

        String jsonResult = "";
        switch (type) {
            case "song":
                List<Song> songs = songService.findByTitleContaining(substringSearch);
                jsonResult = songs.stream()
                        .map(Song::toJson)
                        .collect(Collectors.joining(",", "[", "]"));
                break;

            case "album":
                List<Album> albums = albumService.findByTitleContaining(substringSearch);
                jsonResult = albums.stream()
                        .map(Album::toJson)
                        .collect(Collectors.joining(",", "[", "]"));
                break;

            case "artist":
                List<Artist> artists = artistService.findByNameContaining(substringSearch);
                jsonResult = artists.stream()
                        .map(Artist::toJson)
                        .collect(Collectors.joining(",", "[", "]"));
                break;
            case "user":
                List<User> users = userService.findByUsernameContaining(substringSearch);
                jsonResult = users.stream()
                        .map(User::toJson)
                        .collect(Collectors.joining(",", "[", "]"));
                break;
            default:
                logger.warn("Tipo de búsqueda no reconocido: {}", type);
                return "{\"error\": \"Tipo de búsqueda no reconocido\"}";
        }
        return jsonResult;
    }

}
