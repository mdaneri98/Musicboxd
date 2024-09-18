package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.AlbumReview;
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

    public IndexController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @RequestMapping("/")
    public ModelAndView index(@ModelAttribute("loggedUser") User loggedUser) {
        final ModelAndView mav = new ModelAndView("index");
        mav.addObject("artists", artistService.findAll());
        return mav;
    }
}
