package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.form.ResetPasswordForm;
import ar.edu.itba.paw.webapp.form.UserForm;
import ar.edu.itba.paw.webapp.form.UserProfileForm;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import ar.edu.itba.paw.services.ImageService;


@RequestMapping("/user")
@Controller
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final ReviewService reviewService;
    private final MessageSource messageSource;
    private final ImageService imageService;


    public UserController(UserService userService, AuthenticationManager authenticationManager, ReviewService reviewService, MessageSource messageSource, ImageService imageService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.reviewService = reviewService;
        this.messageSource = messageSource;
        this.imageService = imageService;
    }

    @RequestMapping("/profile")
    public ModelAndView profile(@ModelAttribute("loggedUser") User loggedUser,
                                @RequestParam(name = "pageNum", required = false) Integer pageNum,
                                @RequestParam(name = "page", required = false) String page) {
        if (loggedUser.getId() == 0) { return new ModelAndView("redirect:/"); }
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (page == null || page.isEmpty()) {
            page = "favorites";
        }
        boolean reviewsActive = page.equals("reviews");

        int pageSize = 5;

        final ModelAndView mav = new ModelAndView("users/profile");

        List<Album> favoriteAlbums = userService.getFavoriteAlbums(loggedUser.getId());
        List<Artist> favoriteArtists = userService.getFavoriteArtists(loggedUser.getId());
        List<Song> favoriteSongs = userService.getFavoriteSongs(loggedUser.getId());
        List<Review> reviews = reviewService.findReviewsByUserPaginated(loggedUser.getId(), pageNum, pageSize, loggedUser.getId());

        boolean showNext = reviews.size() == pageSize;
        boolean showPrevious = pageNum > 1;

        mav.addObject("user", userService.find(loggedUser.getId()).get());
        mav.addObject("albums", favoriteAlbums);
        mav.addObject("artists", favoriteArtists);
        mav.addObject("songs", favoriteSongs);
        mav.addObject("reviews", reviews);
        mav.addObject("pageNum", pageNum);
        mav.addObject("showNext", showNext);
        mav.addObject("showPrevious", showPrevious);
        mav.addObject("reviewsActive", reviewsActive);

        return mav;
    }


    @RequestMapping(path = "/edit", method = RequestMethod.GET)
    public ModelAndView editProfile(@ModelAttribute("userProfileForm") final UserProfileForm userProfileForm,
                                    @ModelAttribute("loggedUser") User loggedUser) {
        ModelAndView modelAndView = new ModelAndView("users/edit_profile");

        userProfileForm.setUsername(loggedUser.getUsername());
        userProfileForm.setName(loggedUser.getName());
        userProfileForm.setBio(loggedUser.getBio());

        return modelAndView;
    }

    @RequestMapping(path = "/profile/settings", method = RequestMethod.GET)
    public ModelAndView settings(@ModelAttribute("loggedUser") User loggedUser) {
        return new ModelAndView("settings/settings");
    }

    @RequestMapping(path = "/edit", method = RequestMethod.POST)
    public ModelAndView submitProfile(@Valid @ModelAttribute("userProfileForm") final UserProfileForm upf,
                                      final BindingResult errors,
                                      @ModelAttribute("loggedUser") User loggedUser) {
        userService.findByUsername(upf.getUsername()).ifPresent(user -> {
            if (!user.getId().equals(loggedUser.getId())) {
                errors.rejectValue("username", "validation.user.username.in.use");
            }
        });

        // Check if there are any validation errors
        if (errors.hasErrors())
            return editProfile(upf, loggedUser);

        loggedUser.setUsername(upf.getUsername());
        loggedUser.setName(upf.getName());
        loggedUser.setBio(upf.getBio());
        loggedUser.setImage(imageService.update(new Image(loggedUser.getImage().getId(), getBytes(upf.getProfilePicture()))).get());
        userService.update(loggedUser);

        return new ModelAndView("redirect:/user/profile");
    }

    @RequestMapping("/email-verification")
    public ModelAndView verify(@RequestParam(name = "code", defaultValue = "0") String verificationCode,
                               @ModelAttribute("loggedUser") User loggedUser){
        Long userId = userService.verify(VerificationType.VERIFY_EMAIL, verificationCode);
        if (userId < 1) {
            //userService.createVerification(VerificationType.VERIFY_EMAIL, loggedUser); //Implica deber estar logueado
            String errorMessage = messageSource.getMessage("error.user.verification.expired", null, LocaleContextHolder.getLocale());
            return new ModelAndView("redirect:/?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        }

        String successMessage = messageSource.getMessage("success.user.verification", null, LocaleContextHolder.getLocale());
        return new ModelAndView("redirect:/?success=" + URLEncoder.encode(successMessage, StandardCharsets.UTF_8));
    }

    @RequestMapping("/{userId:\\d+}")
    public ModelAndView user(@ModelAttribute("loggedUser") User loggedUser,
                             @PathVariable(name = "userId") long userId,
                             @RequestParam(name = "pageNum", required = false) Integer pageNum,
                             @RequestParam(name = "page", required = false) String page) {
        int pageSize = 5;
        boolean reviewsActive = false;
        
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (page == null || page.isEmpty()) {
            page = "favorites";
        }
        if (page.equals("reviews")) {
            reviewsActive = true;
        }

        if (userId == loggedUser.getId()) {
            return new ModelAndView("redirect:/user/profile?page=" + page);
        }

        final ModelAndView mav = new ModelAndView("/users/user");
        Optional<User> userOptional = userService.find(userId);
        if (userOptional.isEmpty()) {
            String errorMessage = messageSource.getMessage("error.user.find", null, LocaleContextHolder.getLocale());
            return new ModelAndView("redirect:/?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        }

        List<Review> reviews = reviewService.findReviewsByUserPaginated(userId, pageNum, pageSize, loggedUser.getId());

        boolean showNext = reviews.size() == pageSize;
        boolean showPrevious = pageNum > 1;

        mav.addObject("user", userOptional.get());
        mav.addObject("isFollowing", userService.isFollowing(loggedUser.getId(), userId));
        mav.addObject("albums", userService.getFavoriteAlbums(userId));
        mav.addObject("artists", userService.getFavoriteArtists(userId));
        mav.addObject("songs", userService.getFavoriteSongs(userId));
        mav.addObject("reviews", reviews);
        mav.addObject("pageNum", pageNum);
        mav.addObject("showNext", showNext);
        mav.addObject("showPrevious", showPrevious);
        mav.addObject("reviewsActive", reviewsActive);
        return mav;
    }


    @RequestMapping("/{userId:\\d+}/follow-info")
    public ModelAndView followInfo(@ModelAttribute("loggedUser") User loggedUser,
                                   @PathVariable(name = "userId") long userId,
                                   @RequestParam(name = "pageNum", required = false) Integer pageNum,
                                   @RequestParam(name = "page", required = false) String activePage) {
        if (pageNum == null || pageNum <= 0) pageNum = 1;
        if (activePage == null || activePage.isEmpty()) activePage = "followers";
        boolean followersActive = activePage.equals("followers");
        boolean followingActive = activePage.equals("following");


        ModelAndView mav = new ModelAndView("/users/follow_info");
        int pageSize = 20;

        Optional<User> userOptional = userService.find(userId);
        if (userOptional.isEmpty()) {
            String errorMessage = messageSource.getMessage("error.user.find", null, LocaleContextHolder.getLocale());
            return new ModelAndView("redirect:/?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        }

        boolean showPrevious = pageNum > 1;
        Boolean showNext = null;

        List<User> userList = null;
        if (followersActive){
            userList = userService.getFollowers(userOptional.get().getId(), pageNum, pageSize);
            showNext = userList.size() == pageSize;
        }
        if (followingActive) {
            userList = userService.getFollowings(userOptional.get().getId(), pageNum, pageSize);;
            showNext = userList.size() == pageSize;
        }


        mav.addObject("user", userOptional.get());
        mav.addObject("userList", userList);
        mav.addObject("loggedUser", loggedUser);
        mav.addObject("pageNum", pageNum);
        mav.addObject("followersActive", followersActive);
        mav.addObject("showNext", showNext);
        mav.addObject("showPrevious", showPrevious);

        return mav;
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(required = false) String error) {
        ModelAndView mav = new ModelAndView("users/login");
        if (error != null) {
            mav.addObject("error", true);
        }
        return mav;
    }

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public ModelAndView createForm(@ModelAttribute("userForm") final UserForm userForm) {
        return new ModelAndView("users/register");
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public ModelAndView create(@Valid @ModelAttribute("userForm") final UserForm userForm,
                               final BindingResult errors) {
        if (errors.hasErrors())
            return createForm(userForm);

        final Optional<User> userOpt = userService.create(userForm.getUsername(), userForm.getEmail(), userForm.getPassword());
        if (userOpt.isEmpty()) {
            String errorMessage = messageSource.getMessage("error.user.creation", null, LocaleContextHolder.getLocale());
            return new ModelAndView("redirect:/?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userForm.getUsername(), userForm.getPassword(), null);
        SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(authenticationToken));

        String errorMessage = messageSource.getMessage("success.user.creation", null, LocaleContextHolder.getLocale());
        return new ModelAndView("redirect:/?success=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
    }

    @RequestMapping(value = "/forgot-password", method = RequestMethod.GET)
    public ModelAndView forgotPassword() {
        return new ModelAndView("users/password/forgot_password");
    }

    @RequestMapping(value = "/forgot-password", method = RequestMethod.POST)
    public ModelAndView forgotPassword(@RequestParam("email") String email) {
        Optional<User> userOptional = userService.findByEmail(email);

        if (userOptional.isEmpty()) {
            return new ModelAndView("redirect:/user/forgot-password");
        }

        User user = userOptional.get();
        userService.createVerification(VerificationType.VERIFY_FORGOT_PASSWORD, user);

        return new ModelAndView("redirect:/user/login");
    }

    @RequestMapping(value = "/reset-password", method = RequestMethod.GET)
    public ModelAndView resetPassword(@ModelAttribute(name="resetPasswordForm") ResetPasswordForm resetPasswordForm, @RequestParam("code") String code) {
        resetPasswordForm.setCode(code);
        return new ModelAndView("users/password/reset_password");
    }

    @RequestMapping(value = "/reset-password", method = RequestMethod.POST)
    public ModelAndView resetPassword(@Valid @ModelAttribute("resetPasswordForm") ResetPasswordForm resetPasswordForm,
                                 BindingResult errors) {
        if (errors.hasErrors())
            return resetPassword(resetPasswordForm, resetPasswordForm.getCode());

        Long userId = userService.verify(VerificationType.VERIFY_FORGOT_PASSWORD, resetPasswordForm.getCode());
        if (userId == null || userId < 1) {
            String errorMessage = messageSource.getMessage("error.user.verification", null, LocaleContextHolder.getLocale());
            return new ModelAndView("redirect:/?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        }

        Optional<User> userOptional = userService.find(userId);
        if (userOptional.isEmpty()) {
            String errorMessage = messageSource.getMessage("error.user.verification", null, LocaleContextHolder.getLocale());
            return new ModelAndView("redirect:/?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        }
        userService.changePassword(userOptional.get().getId(), resetPasswordForm.getPassword());

        String successMessage = messageSource.getMessage("success.user.change.password", null, LocaleContextHolder.getLocale());
        return new ModelAndView("redirect:/?success=" + URLEncoder.encode(successMessage, StandardCharsets.UTF_8));
    }

    @RequestMapping(path = "/{userId:\\d+}/follow", method = RequestMethod.GET)
    public ModelAndView follow(@ModelAttribute("loggedUser") User loggedUser,
                               @PathVariable(name = "userId") long userId) {
        userService.createFollowing(loggedUser, userId);
        return new ModelAndView("redirect:/user/" + userId);
    }

    @RequestMapping(path = "/{userId:\\d+}/unfollow", method = RequestMethod.GET)
    public ModelAndView unfollow(@ModelAttribute("loggedUser") User loggedUser,
                                 @PathVariable(name = "userId") long userId) {
        userService.undoFollowing(loggedUser, userId);
        return new ModelAndView("redirect:/user/" + userId);
    }

    @RequestMapping(path = "/language", method = RequestMethod.POST)
    public ModelAndView updateLanguage(@ModelAttribute("loggedUser") User loggedUser,
                                       @RequestParam("language") String language) {
        if (!List.of("en", "es", "fr", "de", "it", "pt", "ja").contains(language)) {
            String errorMessage = messageSource.getMessage("error.user.language.invalid", null, LocaleContextHolder.getLocale());
            return new ModelAndView("redirect:/user/profile/settings?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        }

        loggedUser.setPreferredLanguage(language);
        userService.update(loggedUser);

        return new ModelAndView("redirect:/user/profile/settings");
    }

    @RequestMapping(path = "/settings/theme", method = RequestMethod.POST)
    public ModelAndView updateTheme(@ModelAttribute("loggedUser") User loggedUser,
                                   @RequestParam("theme") String theme) {
        if (!List.of("dark", "kawaii", "forest", "ocean", "sepia").contains(theme)) {
            return new ModelAndView("redirect:/user/profile/settings");
        }

        loggedUser.setTheme(theme);
        userService.update(loggedUser);

        return new ModelAndView("redirect:/user/profile/settings");
    }

    @RequestMapping(path = "/settings/notifications/follow", method = RequestMethod.POST)
    public ModelAndView updateFollowNotifications(@ModelAttribute("loggedUser") User loggedUser,
                                                 @RequestParam("enabled") Boolean enabled) {
        loggedUser.setFollowNotificationsEnabled(enabled);
        userService.update(loggedUser);
        return new ModelAndView("redirect:/user/profile/settings");
    }

    @RequestMapping(path = "/settings/notifications/like", method = RequestMethod.POST)
    public ModelAndView updateLikeNotifications(@ModelAttribute("loggedUser") User loggedUser,
                                               @RequestParam("enabled") Boolean enabled) {
        loggedUser.setLikeNotificationsEnabled(enabled);
        userService.update(loggedUser);
        return new ModelAndView("redirect:/user/profile/settings");
    }

    @RequestMapping(path = "/settings/notifications/comment", method = RequestMethod.POST)
    public ModelAndView updateCommentNotifications(@ModelAttribute("loggedUser") User loggedUser,
                                                  @RequestParam("enabled") Boolean enabled) {
        loggedUser.setCommentNotificationsEnabled(enabled);
        userService.update(loggedUser);
        return new ModelAndView("redirect:/user/profile/settings");
    }

    @RequestMapping(path = "/settings/notifications/review", method = RequestMethod.POST)
    public ModelAndView updateReviewNotifications(@ModelAttribute("loggedUser") User loggedUser,
                                                 @RequestParam("enabled") Boolean enabled) {
        loggedUser.setReviewNotificationsEnabled(enabled);
        userService.update(loggedUser);
        return new ModelAndView("redirect:/user/profile/settings");
    }

    private byte[] getBytes(MultipartFile imageFile) {
        if (imageFile == null) { return null; }
        byte[] bytes;
        try {
            bytes = imageFile.getBytes();
        } catch (IOException e) {
            bytes = null;
        }
        return bytes;
    }
}
