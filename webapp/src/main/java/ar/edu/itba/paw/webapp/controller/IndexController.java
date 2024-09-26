package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
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

    @RequestMapping(value = "/search/{type}", method = RequestMethod.GET)
    @ResponseBody
    public String subSearch(@PathVariable("type") String type, @RequestParam(name = "s", defaultValue = "") String substringSearch) {
        logger.debug("Iniciando búsqueda en subSearch con type={} y substringSearch={}", type, substringSearch);

        String jsonResult = "";
        switch (type) {
            case "song":
                List<Song> songs = songService.findByTitleContaining(substringSearch);
                // Convertimos la lista a un string JSON
                jsonResult = songs.stream()
                        .map(Song::toJson)
                        .collect(Collectors.joining(",", "[", "]"));
                break;

            case "album":
                List<Album> albums = albumService.findByTitleContaining(substringSearch);
                // Convertimos la lista a un string JSON
                jsonResult = albums.stream()
                        .map(Album::toJson)
                        .collect(Collectors.joining(",", "[", "]"));
                break;

            case "artist":
                List<Artist> artists = artistService.findByNameContaining(substringSearch);
                // Convertimos la lista a un string JSON
                jsonResult = artists.stream()
                        .map(Artist::toJson)
                        .collect(Collectors.joining(",", "[", "]"));
                break;
            case "user":
                List<User> users = userService.findByUsernameContaining(substringSearch);
                // Convertimos la lista a un string JSON
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
