package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.Artist;
import ar.edu.itba.paw.User;
import ar.edu.itba.paw.services.AlbumService;
import ar.edu.itba.paw.services.ArtistService;
import ar.edu.itba.paw.services.SongService;
import ar.edu.itba.paw.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

/*
    No es un servlet.
 */

@Controller
public class HelloWorldController {

    private final UserService userService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final SongService songService;

    public HelloWorldController(UserService userService, ArtistService artistService, AlbumService albumService, SongService songService) {
        this.userService = userService;
        this.artistService = artistService;
        this.albumService = albumService;
        this.songService = songService;
    }

    @RequestMapping("/")
    public ModelAndView index(@RequestParam(name = "userId", defaultValue = "1") long userId) {
        final ModelAndView mav = new ModelAndView("index");
        mav.addObject("artists", artistService.findAll());
        return mav;
    }

    @RequestMapping("/artist/{artistId:\\d+}")
    public ModelAndView artist(@PathVariable(name = "artistId") long artistId) {
        final ModelAndView mav = new ModelAndView("artist");
        Artist artist = artistService.findById(artistId).get();
        mav.addObject("artist", artist);
        mav.addObject("albums", albumService.findByArtistId(artist.getId()));
        mav.addObject("songs", songService.findByArtistId(artist.getId()));
        return mav;
    }

    @RequestMapping("/review/{artistId:\\d+}")
    public ModelAndView review(@PathVariable(name = "artistId") long artistId) {
        final ModelAndView mav = new ModelAndView("review");
        Artist artist = artistService.findById(artistId).get();
        mav.addObject("artist", artist);
        return mav;
    }
}
