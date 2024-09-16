package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.advice.UserControllerAdvice;
import ar.edu.itba.paw.webapp.auth.AuthCUserDetails;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/user")
@Controller
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @RequestMapping("/")
    public ModelAndView profile(@ModelAttribute("loggedUser") User loggedUser) {
        final ModelAndView mav = new ModelAndView("users/profile");
        LOGGER.info("Logged username: {}", loggedUser.getUsername());
        mav.addObject("user", loggedUser);
        return mav;
    }

    @RequestMapping("/verification")
    public ModelAndView verify(@RequestParam(name = "code", defaultValue = "0") String verificationCode ){
        boolean ok = userService.verify(verificationCode);
        if (!ok) {
            return new ModelAndView("users/verification_expired");
        }
        return new ModelAndView("redirect:/");
    }

    @RequestMapping("/{userId:\\d+}")
    public ModelAndView user(@PathVariable(name = "userId") long userId) {
        final ModelAndView mav = new ModelAndView("/users/user");

        User user = userService.findById(userId).get();
        // GET FAVOURITE MUSIC FROM USER
//        Artist artists = artistService.findById(artistId).get();
//        List<Album> albums = albumService.findByArtistId(artistId);
//        List<Song> songs = songService.findByArtistId(artistId);
//        List<ArtistReview> reviews = artistReviewService.findByUserId(artistId);


        mav.addObject("user", user);
//        mav.addObject("albums", albums);
//        mav.addObject("artists", artists);
//        mav.addObject("songs", songs);
//        mav.addObject("reviews", reviews);

        return mav;
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        return new ModelAndView("users/login");
    }

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public ModelAndView createForm(@ModelAttribute("userForm") final UserForm userForm) {
        /* ModelAttribute agrega al `mav`: <K: "userForm",V: userForm > */
        return new ModelAndView("users/register");
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public ModelAndView create(@Valid @ModelAttribute("userForm") final UserForm userForm, final BindingResult errors) {
        if (errors.hasErrors()) {
            return createForm(userForm);
        }

        User user = new User(userForm.getUsername(), userForm.getPassword(), userForm.getEmail());
        final int done = userService.create(user);
        // "Generar una sesión" (así no redirije a /login)
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userForm.getUsername(), userForm.getPassword(), null);
        SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(authenticationToken));

        return new ModelAndView("redirect:/");
    }

    @RequestMapping(path = "/{userId:\\d+}/follow", method = RequestMethod.POST)
    public ModelAndView follow(@ModelAttribute("loggedUser") User loggedUser, @PathVariable(name = "userId") long userId) {
        final int done = userService.createFollowing(loggedUser, userId);
        return new ModelAndView("redirect:/");
    }

    @RequestMapping(path = "/{userId:\\d+}/unfollow", method = RequestMethod.POST)
    public ModelAndView unfollow(@ModelAttribute("loggedUser") User loggedUser, @PathVariable(name = "userId") long userId) {
        final int done = userService.undoFollowing(loggedUser, userId);
        return new ModelAndView("redirect:/");
    }



}
