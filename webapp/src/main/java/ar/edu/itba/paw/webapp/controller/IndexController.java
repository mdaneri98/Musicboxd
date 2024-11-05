package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.services.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

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

    @RequestMapping(value = {"/home", "/"})
    public ModelAndView home(@ModelAttribute("loggedUser") User loggedUser, 
                            @RequestParam(name = "pageNum", required = false) Integer pageNum, 
                            @RequestParam(name = "error", required = false) String error, 
                            @RequestParam(name = "success", required = false) String success,
                            @RequestParam(name = "page", required = false) String activePage) {
                                
        if (pageNum == null || pageNum < 1) pageNum = 1;

        if (activePage == null || activePage.isEmpty()) activePage = "forYou";
        boolean forYouActive = activePage.equals("forYou");
        boolean followingActive = activePage.equals("following");

        int pageSize = 10;
        long loggedUserId;
        ModelAndView mav;
        List<Review> reviews;

        if (User.isAnonymus(loggedUser)) {
            mav = new ModelAndView("anonymous/home");
            loggedUserId = 0;
        } else {
            mav = new ModelAndView("home");
            loggedUserId = loggedUser.getId();
        }

        if (followingActive) {
            reviews = reviewService.getReviewsFromFollowedUsersPaginated(loggedUserId, pageNum, pageSize, loggedUserId);

        }
        else {
            reviews = reviewService.getPopularReviewsPaginated(pageNum, pageSize, loggedUserId);
        }

        mav.addObject("success", success);
        mav.addObject("error", error);
        mav.addObject("showNext", reviews.size() == pageSize);
        mav.addObject("showPrevious", pageNum > 1);
        mav.addObject("reviews",reviews);
        mav.addObject("pageNum", pageNum);
        mav.addObject("pageSize", pageSize);
        mav.addObject("forYouActive", forYouActive);
        mav.addObject("followingActive", followingActive);

        return mav;
    }

    @RequestMapping("/search")
    public ModelAndView search(@ModelAttribute("loggedUser") User loggedUser) {
        return new ModelAndView("search");
    }

    @RequestMapping("/music")
    public ModelAndView music(@ModelAttribute("loggedUser") User loggedUser) {
        ModelAndView mav = new ModelAndView("music");

        int pageSize = 10;

        List<Album> topRatedAlbums = albumService.findPaginated(FilterType.RATING,1, pageSize);
        List<Album> mostPopularAlbums = albumService.findPaginated(FilterType.POPULAR,1, pageSize);

        List<Artist> topRatedArtists = artistService.findPaginated(FilterType.RATING,1, pageSize);
        List<Artist> mostPopularArtists = artistService.findPaginated(FilterType.POPULAR,1, pageSize);

        List<Song> topRatedSongs = songService.findPaginated(FilterType.RATING,1, pageSize);
        List<Song> mostPopularSongs = songService.findPaginated(FilterType.POPULAR,1, pageSize);

        mav.addObject("topRatedAlbums", topRatedAlbums);
        mav.addObject("mostPopularAlbums", mostPopularAlbums);

        mav.addObject("topRatedArtists", topRatedArtists);
        mav.addObject("mostPopularArtists", mostPopularArtists);

        mav.addObject("topRatedSongs", topRatedSongs);
        mav.addObject("mostPopularSongs", mostPopularSongs);

        return mav;
    }

    @RequestMapping("/music/view-all")
    public ModelAndView viewAll(@ModelAttribute("loggedUser") User loggedUser,
                                @RequestParam(name = "page", required = false) String page,
                                @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                @RequestParam(name = "filter", required = false) String filter) {
        ModelAndView mav = new ModelAndView("view_all_music");

        boolean albumsActive = page.equals("albums");
        boolean artistsActive = page.equals("artists");
        boolean songsActive = page.equals("songs");
        if (filter == null) filter = "POPULAR";
        FilterType filterType = FilterType.valueOf(filter);

        int pageSize = 20;
        boolean showNext = false;
        List<Album> albums = new ArrayList<>();
        List<Artist> artists = new ArrayList<>();
        List<Song> songs = new ArrayList<>();

        if (songsActive) {
            songs = songService.findPaginated(filterType, pageNum, pageSize);
            showNext = songs.size() == pageSize;
        } else if (albumsActive) {
            albums = albumService.findPaginated(filterType, pageNum, pageSize);
            showNext = albums.size() == pageSize;
        } else if (artistsActive) {
            artists = artistService.findPaginated(filterType, pageNum, pageSize);
            showNext = artists.size() == pageSize;
        }

        mav.addObject("artists", artists);
        mav.addObject("albums", albums);
        mav.addObject("songs", songs);
        mav.addObject("currentPage", pageNum);
        mav.addObject("albumsActive", albumsActive);
        mav.addObject("artistsActive", artistsActive);
        mav.addObject("songsActive", songsActive);
        mav.addObject("showNext", showNext);
        mav.addObject("showPrevious", pageNum > 1);
        mav.addObject("filter", filter);

        return mav;
    }

    @RequestMapping(value = "/search/{type}", method = RequestMethod.GET)
    @ResponseBody
    public String subSearch(@PathVariable("type") String type, @RequestParam(name = "s", defaultValue = "") String substringSearch) {

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
                List<User> users = userService.findByUsernameContaining(substringSearch, 1, 100);
                jsonResult = users.stream()
                        .map(User::toJson)
                        .collect(Collectors.joining(",", "[", "]"));
                break;
            default:
                return "{\"error\": \"Tipo de búsqueda no reconocido\"}";
        }
        return jsonResult;
    }

}
