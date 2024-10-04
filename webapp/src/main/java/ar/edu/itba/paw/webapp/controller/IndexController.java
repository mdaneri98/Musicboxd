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
    public ModelAndView home(@ModelAttribute("loggedUser") User loggedUser) {
        return home(loggedUser, 1);
    }

    @RequestMapping(value = "/home/{pageNum:\\d+}")
    public ModelAndView home(@ModelAttribute("loggedUser") User loggedUser, @RequestParam(name = "pageNum", required = false) Integer pageNum) {
        int pageSize = 10;
        long loggedUserId;
        ModelAndView mav;

        if (loggedUser == null){
            mav = new ModelAndView("anonymous/home");
            loggedUserId = 0;
        }
        else {
            mav = new ModelAndView("home");
            loggedUserId = loggedUser.getId();
        }

        List<Review> popularReviews = reviewService.getPopularReviewsPaginated(pageNum, pageSize, loggedUserId);
        List<Review> followingReviews = reviewService.getReviewsFromFollowedUsersPaginated(loggedUserId, pageNum, pageSize, loggedUserId);
        if (pageNum > 1 && popularReviews.isEmpty() && followingReviews.isEmpty()) return home(loggedUser, 1);
        boolean hasNextPopular = popularReviews.size() >= pageSize-1;
        boolean hasNextFollowing = followingReviews.size() >= pageSize-1;


        mav.addObject("showNext", hasNextPopular || hasNextFollowing);
        mav.addObject("showPrevious", pageNum > 1);

        mav.addObject("popularReviews", popularReviews);
        mav.addObject("followingReviews", followingReviews);
        mav.addObject("pageNum", pageNum);
        mav.addObject("pageSize", pageSize-1);

        return mav;
    }

    @RequestMapping("/search")
    public ModelAndView search(@ModelAttribute("loggedUser") User loggedUser) {
        ModelAndView mav = new ModelAndView("search");

        List<Album> albums = albumService.findPaginated(FilterType.NEWEST,1, 10);
        List<Artist> artists = artistService.findPaginated(FilterType.RATING,1, 10);

        mav.addObject("top_albums", albums);
        mav.addObject("top_artists", artists);

        return mav;
    }

    @RequestMapping("/music")
    public ModelAndView music(@ModelAttribute("loggedUser") User loggedUser) {
        ModelAndView mav;
        if (loggedUser == null) mav = new ModelAndView("anonymous/music");
        else mav = new ModelAndView("music");

        List<Album> topRatedAlbums = albumService.findPaginated(FilterType.RATING,1, 5);
        List<Album> mostPopularAlbums = albumService.findPaginated(FilterType.POPULAR,1, 5);

        List<Artist> topRatedArtists = artistService.findPaginated(FilterType.RATING,1, 5);
        List<Artist> mostPopularArtists = artistService.findPaginated(FilterType.POPULAR,1, 5);

        List<Song> topRatedSongs = songService.findPaginated(FilterType.RATING,1, 5);
        List<Song> mostPopularSongs = songService.findPaginated(FilterType.POPULAR,1, 5);

        mav.addObject("topRatedAlbums", topRatedAlbums);
        mav.addObject("mostPopularAlbums", mostPopularAlbums);

        mav.addObject("topRatedArtists", topRatedArtists);
        mav.addObject("mostPopularArtists", mostPopularArtists);

        mav.addObject("topRatedSongs", topRatedSongs);
        mav.addObject("mostPopularSongs", mostPopularSongs);

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
