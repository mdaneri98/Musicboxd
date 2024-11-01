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
                                @RequestParam(name = "pageNum", required = false) Integer pageNum) {
        if (loggedUser.getId() == 0) { return new ModelAndView("redirect:/"); }
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }

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
                                      final BindingResult errors,
                                      @ModelAttribute("loggedUser") User loggedUser) {

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
                             @RequestParam(name = "pageNum", required = false) Integer pageNum) {
        int pageSize = 5;

        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }

        if (userId == loggedUser.getId()) {
            return new ModelAndView("redirect:/user/profile?pageNum=" + pageNum);
        }

        final ModelAndView mav = new ModelAndView("/users/user");
        User user = userService.find(userId).orElseThrow();

        List<Review> reviews = reviewService.findReviewsByUserPaginated(userId, pageNum, pageSize, loggedUser.getId());

        boolean showNext = reviews.size() == pageSize;
        boolean showPrevious = pageNum > 1;

        mav.addObject("user", user);
        mav.addObject("isFollowing", userService.isFollowing(loggedUser.getId(), userId));
        mav.addObject("albums", userService.getFavoriteAlbums(userId));
        mav.addObject("artists", userService.getFavoriteArtists(userId));
        mav.addObject("songs", userService.getFavoriteSongs(userId));
        mav.addObject("reviews", reviews);
        mav.addObject("pageNum", pageNum);
        mav.addObject("showNext", showNext);
        mav.addObject("showPrevious", showPrevious);

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
        int pageSize = 100;

        Optional<User> userOptional = userService.find(userId);
        if (userOptional.isEmpty()) {
            String errorMessage = messageSource.getMessage("error.user.find", null, LocaleContextHolder.getLocale());
            return new ModelAndView("redirect:/?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        }

        // FIXME: Get list paginated
        List<User> followingList = userOptional.get().getFollowing();
        List<User> followersList = userOptional.get().getFollowers();

        boolean showPrevious = pageNum > 1;
        Boolean showNext = null;
        List<User> userList = null;
        if (followersActive){
            userList = followersList;
            showNext = followersList.size() == pageSize;
        }
        if (followingActive) {
            userList = followingList;
            showNext = followingList.size() == pageSize;
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
    public ModelAndView login() {
        return new ModelAndView("users/login");
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
