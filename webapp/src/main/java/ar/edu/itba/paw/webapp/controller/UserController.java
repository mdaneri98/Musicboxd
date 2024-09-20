package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.advice.UserControllerAdvice;
import ar.edu.itba.paw.webapp.auth.AuthCUserDetails;
import ar.edu.itba.paw.webapp.form.UserForm;
import ar.edu.itba.paw.webapp.form.UserProfileForm;
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
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/user")
@Controller
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final ImageService imageService;
    private final AuthenticationManager authenticationManager;
    private final ReviewService reviewService;

    public UserController(UserService userService, ImageService imageService, AuthenticationManager authenticationManager, ReviewService reviewService) {
        this.userService = userService;
        this.imageService = imageService;
        this.authenticationManager = authenticationManager;
        this.reviewService = reviewService;
    }

    @RequestMapping("/profile/{pageNum:\\d+}")
    public ModelAndView profile(@ModelAttribute("loggedUser") User loggedUser, @PathVariable(name = "pageNum", required = false) Integer pageNum ) {
        final ModelAndView mav = new ModelAndView("users/profile");
        LOGGER.info("Logged username: {}", loggedUser.getUsername());
        if (pageNum == null || pageNum <= 0) pageNum = 1;

        mav.addObject("albums", userService.getFavoriteAlbums(loggedUser.getId()));
        mav.addObject("artists", userService.getFavoriteArtists(loggedUser.getId()));
        mav.addObject("songs", userService.getFavoriteSongs(loggedUser.getId()));
        mav.addObject("reviews", reviewService.findReviewsByUserPaginated(loggedUser.getId(), pageNum,5));
        mav.addObject("pageNum", pageNum);
        return mav;
    }

    @RequestMapping(path = "/edit", method = RequestMethod.GET)
    public ModelAndView editProfile(@ModelAttribute("userProfileForm") final UserProfileForm userProfileForm,
                                    @ModelAttribute("loggedUser") User loggedUser) {
        ModelAndView modelAndView = new ModelAndView("users/edit_profile");

        userProfileForm.setUsername(loggedUser.getUsername());
        userProfileForm.setName(loggedUser.getName());
        userProfileForm.setBio(loggedUser.getBio());

        modelAndView.addObject("userProfileForm", userProfileForm);
        return modelAndView;
    }

    @RequestMapping(path = "/edit", method = RequestMethod.POST)
    public ModelAndView submitProfile(@Valid @ModelAttribute("userProfileForm") final UserProfileForm upf,
                                      @ModelAttribute("loggedUser") User loggedUser,
                                      final BindingResult errors) {

        // Check if there are any validation errors
        if (errors.hasErrors()) {
            return editProfile(upf, loggedUser);
        }
        if(upf.getProfilePicture() != null && !upf.getProfilePicture().isEmpty()) {
            try {
                long imageId = imageService.save(upf.getProfilePicture().getBytes());
                loggedUser.setImgId(imageId);
            } catch (IOException e) {
                LOGGER.debug("Error en '/user/edit' al leer los bytes del profile {}", e.getMessage());
                return new ModelAndView("redirect:/error");
            }
        }

        loggedUser.setUsername(upf.getUsername());
        loggedUser.setName(upf.getName());
        loggedUser.setBio(upf.getBio());
        
        userService.update(loggedUser.getId(), upf.getUsername(), loggedUser.getEmail(), loggedUser.getPassword(), upf.getName(), upf.getBio(), LocalDateTime.now(), loggedUser.isVerified(), loggedUser.isModerator(), loggedUser.getImgId(), loggedUser.getFollowersAmount(), loggedUser.getFollowingAmount(), loggedUser.getReviewAmount());
        return new ModelAndView("redirect:/user/");
    }

    @RequestMapping("/verification")
    public ModelAndView verify(@RequestParam(name = "code", defaultValue = "0") String verificationCode,
                               @ModelAttribute("loggedUser") User loggedUser){
        boolean ok = userService.verify(verificationCode);
        if (!ok) {
            return new ModelAndView("users/verification_expired");
        }
        return new ModelAndView("redirect:/");
    }

    @RequestMapping("/profile")
    public ModelAndView profile (@ModelAttribute("loggedUser") User loggedUser){
        return profile(loggedUser,1);
    }


    @RequestMapping("/{userId:\\d+}")
    public ModelAndView user (@ModelAttribute("loggedUser") User loggedUser,
                              @PathVariable(name = "userId") long userId){
        return user(loggedUser,userId,1);
    }

    @RequestMapping("/{userId:\\d+}/{pageNum:\\d+}")
    public ModelAndView user(@ModelAttribute("loggedUser") User loggedUser,
                             @PathVariable(name = "userId") long userId, @PathVariable(name = "pageNum", required = false) Integer pageNum ) {
        if (userId == loggedUser.getId()) return new ModelAndView("redirect:/user/profile/").addObject("user", loggedUser);
        if (pageNum == null || pageNum <= 0) pageNum = 1;

        final ModelAndView mav = new ModelAndView("/users/user");

        User user = userService.findById(userId).get();
        mav.addObject("user", user);
        mav.addObject("isFollowing", userService.isFollowing(loggedUser.getId(), userId));
        mav.addObject("albums", userService.getFavoriteAlbums(userId));
        mav.addObject("artists", userService.getFavoriteArtists(userId));
        mav.addObject("songs", userService.getFavoriteSongs(userId));
        mav.addObject("reviews", reviewService.findReviewsByUserPaginated(userId, pageNum, 5));
        mav.addObject("pageNum", pageNum);

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
    public ModelAndView create(@Valid @ModelAttribute("userForm") final UserForm userForm,
                               final BindingResult errors) {
        if (errors.hasErrors()) {
            return createForm(userForm);
        }


        final int done = userService.create(userForm.getUsername(), userForm.getEmail(), userForm.getPassword());
        // "Generar una sesión" (así no redirije a /login)
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userForm.getUsername(), userForm.getPassword(), null);
        SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(authenticationToken));

        return new ModelAndView("redirect:/");
    }

    @RequestMapping(path = "/{userId:\\d+}/follow", method = RequestMethod.POST)
    public ModelAndView follow(@ModelAttribute("loggedUser") User loggedUser,
                               @PathVariable(name = "userId") long userId) {
        final int done = userService.createFollowing(loggedUser, userId);
        return new ModelAndView("redirect:/user/" + userId);
    }

    @RequestMapping(path = "/{userId:\\d+}/unfollow", method = RequestMethod.POST)
    public ModelAndView unfollow(@ModelAttribute("loggedUser") User loggedUser,
                                 @PathVariable(name = "userId") long userId) {
        final int done = userService.undoFollowing(loggedUser, userId);
        return new ModelAndView("redirect:/user/" + userId);
    }


}
