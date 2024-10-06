package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.form.CreatePasswordForm;
import ar.edu.itba.paw.webapp.form.UserForm;
import ar.edu.itba.paw.webapp.form.UserProfileForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequestMapping("/user")
@Controller
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final ReviewService reviewService;
    private final MessageSource messageSource;


    public UserController(UserService userService, AuthenticationManager authenticationManager, ReviewService reviewService, MessageSource messageSource) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.reviewService = reviewService;
        this.messageSource = messageSource;
    }

    @RequestMapping("/profile")
    public ModelAndView profile(@ModelAttribute("loggedUser") User loggedUser,
                                @RequestParam(name = "pageNum", required = false) Integer pageNum) {
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }

        int pageSize = 5;
        int followingPageSize = 20;

        final ModelAndView mav = new ModelAndView("users/profile");
        LOGGER.info("[/profile]Logged username: {}", loggedUser.getUsername());

        // Obtener las listas paginadas
        List<Album> favoriteAlbums = userService.getFavoriteAlbums(loggedUser.getId());
        List<Artist> favoriteArtists = userService.getFavoriteArtists(loggedUser.getId());
        List<Song> favoriteSongs = userService.getFavoriteSongs(loggedUser.getId());
        List<Review> reviews = reviewService.findReviewsByUserPaginated(loggedUser.getId(), pageNum, pageSize, loggedUser.getId());

        // Lógica para determinar si se deben mostrar los botones "Next" y "Previous"
        boolean showNext = reviews.size() == pageSize;  // Si hay tantas reseñas como el tamaño de página, hay más para mostrar
        boolean showPrevious = pageNum > 1;  // Mostrar "Previous" si no estamos en la primera página

        // Agregar los objetos al modelo
        mav.addObject("user", userService.find(loggedUser.getId()).get());
        mav.addObject("albums", favoriteAlbums);
        mav.addObject("artists", favoriteArtists);
        mav.addObject("songs", favoriteSongs);
        mav.addObject("reviews", reviews);
        mav.addObject("pageNum", pageNum);

        // Agregar flags para mostrar los botones "Next" y "Previous"
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
        userService.update(loggedUser, getBytes(upf.getProfilePicture()));
        return new ModelAndView("redirect:/user/profile");
    }

    @RequestMapping("/email-verification")
    public ModelAndView verify(@RequestParam(name = "code", defaultValue = "0") String verificationCode,
                               @ModelAttribute("loggedUser") User loggedUser){
        Long userId = userService.verify(VerificationType.VERIFY_EMAIL, verificationCode);
        if (userId < 1)
            return new ModelAndView("users/verification_expired");

        return new ModelAndView("redirect:/");
    }

    @RequestMapping("/{userId:\\d+}")
    public ModelAndView user(@ModelAttribute("loggedUser") User loggedUser,
                             @PathVariable(name = "userId") long userId,
                             @RequestParam(name = "pageNum", required = false) Integer pageNum) {
        int pageSize = 5;  // Tamaño de página para las reseñas

        // Si pageNum es nulo o menor que 1, se inicializa en 1
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }

        // Si el ID del usuario es el mismo que el del loggedUser, redirige a su perfil
        if (userId == loggedUser.getId()) {
            return new ModelAndView("redirect:/user/profile?pageNum=" + pageNum);
        }

        // Obtener los datos del usuario a mostrar
        final ModelAndView mav = new ModelAndView("/users/user");
        User user = userService.find(userId).orElseThrow();

        List<Review> reviews = reviewService.findReviewsByUserPaginated(userId, pageNum, pageSize, loggedUser.getId());

        // Determinar si se deben mostrar los botones "Next" y "Previous"
        boolean showNext = reviews.size() == pageSize;
        boolean showPrevious = pageNum > 1;

        // Añadir objetos al modelo
        mav.addObject("user", user);
        mav.addObject("isFollowing", userService.isFollowing(loggedUser.getId(), userId));
        mav.addObject("albums", userService.getFavoriteAlbums(userId));
        mav.addObject("artists", userService.getFavoriteArtists(userId));
        mav.addObject("songs", userService.getFavoriteSongs(userId));
        mav.addObject("reviews", reviews);
        mav.addObject("pageNum", pageNum);

        // Añadir flags para mostrar los botones de navegación
        mav.addObject("showNext", showNext);
        mav.addObject("showPrevious", showPrevious);

        return mav;
    }


    @RequestMapping("/{userId:\\d+}/follow-info")
    public ModelAndView followInfo(@ModelAttribute("loggedUser") User loggedUser,
                                   @PathVariable(name = "userId") long userId,
                                   @RequestParam(name = "pageNum", required = false) Integer pageNum) {
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }

        ModelAndView mav = new ModelAndView("/users/follow_info");
        int pageSize = 100;

        // Obtener la información de seguidores y seguidos de manera paginada
        UserFollowingData followingData = userService.getFollowingData(userId, pageSize, (pageNum - 1) * pageSize);
        List<User> followingList = followingData.getFollowing();
        List<User> followersList = followingData.getFollowers();

        // Determinar si mostrar botones "Next" y "Previous"
        boolean showNext = followingList.size() == pageSize || followersList.size() == pageSize;
        boolean showPrevious = pageNum > 1;

        Optional<User> userOptional = userService.find(userId);
        if (userOptional.isEmpty()) {
            String errorMessage = messageSource.getMessage("error.user.find", null, LocaleContextHolder.getLocale());
            return new ModelAndView("redirect:/?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        }

        mav.addObject("user", userOptional.get());
        mav.addObject("followingList", followingList);
        mav.addObject("followersList", followersList);
        mav.addObject("loggedUser", loggedUser);
        mav.addObject("pageNum", pageNum);
        mav.addObject("title", "Following");

        // Añadir flags para mostrar los botones de navegación
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
        /* ModelAttribute agrega al `mav`: <K: "userForm",V: userForm > */
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

        // "Generar una sesión" (así no redirije a /login)
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userForm.getUsername(), userForm.getPassword(), null);
        SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(authenticationToken));

        return new ModelAndView("redirect:/");
    }

    @RequestMapping(value = "/forgot-password", method = RequestMethod.GET)
    public ModelAndView forgotPassword() {
        return new ModelAndView("users/password/forgot_password");
    }

    @RequestMapping(value = "/forgot-password", method = RequestMethod.POST)
    public ModelAndView forgotPassword(@RequestParam("email") String email) {
        // Buscar al usuario por correo electrónico
        Optional<User> userOptional = userService.findByEmail(email);

        if (userOptional.isEmpty()) {
            return new ModelAndView("redirect:/user/forgot-password");
        }

        User user = userOptional.get();
        userService.createVerification(VerificationType.VERIFY_FORGOT_PASSWORD, user);

        return new ModelAndView("redirect:/user/login");
    }

    @RequestMapping(value = "/create-password", method = RequestMethod.GET)
    public ModelAndView createPassword(@ModelAttribute(name="createPasswordForm") CreatePasswordForm createPasswordForm, @RequestParam("code") String code) {
        createPasswordForm.setCode(code);
        return new ModelAndView("users/password/create_password");
    }

    @RequestMapping(value = "/create-password", method = RequestMethod.POST)
    public ModelAndView createPassword(@Valid @ModelAttribute("createPasswordForm") CreatePasswordForm createPasswordForm,
                                 BindingResult errors) {
        if (errors.hasErrors())
            return new ModelAndView("redirect:/user/create-password?code=" + createPasswordForm.getCode());

        Long userId = userService.verify(VerificationType.VERIFY_FORGOT_PASSWORD, createPasswordForm.getCode());
        if (userId == null || userId < 1) {
            return new ModelAndView("redirect:/user/login");
        }

        Optional<User> userOptional = userService.find(userId);
        if (userOptional.isEmpty()) {
            String errorMessage = messageSource.getMessage("error.user.verification", null, LocaleContextHolder.getLocale());
            return new ModelAndView("redirect:/?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        }
        userService.changePassword(userOptional.get().getId(), createPasswordForm.getPassword());

        String successMessage = messageSource.getMessage("success.user.change.password", null, LocaleContextHolder.getLocale());
        return new ModelAndView("redirect:/?success=" + URLEncoder.encode(successMessage, StandardCharsets.UTF_8));
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

    private byte[] getBytes(MultipartFile imageFile) {
        if (imageFile == null) { return null; }
        byte[] bytes;
        try {
            bytes = imageFile.getBytes();
        } catch (IOException e) {
            LOGGER.debug("Error when reading input image: {}.", e.getMessage());
            bytes = null;
        }
        return bytes;
    }
}
