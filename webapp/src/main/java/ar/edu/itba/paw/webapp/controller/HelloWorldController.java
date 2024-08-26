package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.services.ArtistService;
import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ViewResolver;

/*
    No es un servlet.
 */

@Controller
public class HelloWorldController {

    private final UserService userService;
    private final ArtistService artistService;

    public HelloWorldController(UserService userService, ArtistService artistService) {
        this.userService = userService;
        this.artistService = artistService;
    }

    @RequestMapping("/")
    public ModelAndView index(@RequestParam(name = "userId", defaultValue = "1") long userId) {
        final ModelAndView mav = new ModelAndView("index");
        mav.addObject("userId", userId);
        mav.addObject("username", userService.findById(userId).get().getUsername());
        return mav;
    }

    @RequestMapping("/artist/{artistId:\\d+}")
    public ModelAndView profile(@PathVariable(name = "artistId") long artistId) {
        final ModelAndView mav = new ModelAndView("profile");
        mav.addObject("artist", artistService.findById(artistId).get());
        return mav;
    }

}
