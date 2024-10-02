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
    public ModelAndView home(@ModelAttribute("loggedUser") User loggedUser, @RequestParam(name = "pageNum", required = false) Integer pageNum) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }

        int pageSize = 10;

        if (loggedUser == null) {
            final ModelAndView mav = new ModelAndView("anonymous/home");

            // Elementos paginados
            List<Album> popularAlbums = albumService.findPaginated(FilterType.RATING, pageNum, pageSize);
            List<Artist> popularArtists = artistService.findPaginated(FilterType.RATING, pageNum, pageSize);
            List<Review> popularReviews = reviewService.getPopularReviewsPaginated(pageNum, pageSize, 0);

            // Determinar si hay más páginas de álbumes, artistas o reseñas
            boolean hasNextAlbums = popularAlbums.size() == pageSize;
            boolean hasNextArtists = popularArtists.size() == pageSize;
            boolean hasNextReviews = popularReviews.size() == pageSize;

            // Añadir objetos necesarios al modelo
            mav.addObject("popularAlbums", popularAlbums);
            mav.addObject("popularArtists", popularArtists);
            mav.addObject("popularReviews", popularReviews);
            mav.addObject("pageNum", pageNum);
            mav.addObject("pageSize", pageSize);

            // Lógica para mostrar botones Next y Previous
            mav.addObject("showNext", hasNextAlbums || hasNextArtists || hasNextReviews);
            mav.addObject("showPrevious", pageNum > 1);

            return mav;
        }

        final ModelAndView mav = new ModelAndView("home");

        // Obtener los elementos paginados
        List<Review> popularReviews = reviewService.getPopularReviewsPaginated(pageNum, pageSize, loggedUser.getId());
        List<Review> followingReviews = reviewService.getReviewsFromFollowedUsersPaginated(loggedUser.getId(), pageNum, pageSize, loggedUser.getId());

        // Determinar si hay más reseñas para la siguiente página
        boolean hasNextPopular = popularReviews.size() == pageSize;
        boolean hasNextFollowing = followingReviews.size() == pageSize;

        // Añadir los objetos al modelo
        mav.addObject("popularReviews", popularReviews);
        mav.addObject("followingReviews", followingReviews);
        mav.addObject("pageNum", pageNum);
        mav.addObject("pageSize", pageSize);

        // Lógica para mostrar botones Next y Previous
        mav.addObject("showNext", hasNextPopular || hasNextFollowing);
        mav.addObject("showPrevious", pageNum > 1);

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
