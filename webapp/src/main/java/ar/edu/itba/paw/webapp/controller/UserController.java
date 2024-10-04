package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.Review;
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
import org.springframework.web.multipart.MultipartFile;
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
        List<User> followingUsers = userService.getFollowingData(loggedUser.getId(), followingPageSize, (pageNum - 1) * followingPageSize).getFollowing();
        List<Album> favoriteAlbums = userService.getFavoriteAlbums(loggedUser.getId());
        List<Artist> favoriteArtists = userService.getFavoriteArtists(loggedUser.getId());
        List<Song> favoriteSongs = userService.getFavoriteSongs(loggedUser.getId());
        List<Review> reviews = reviewService.findReviewsByUserPaginated(loggedUser.getId(), pageNum, pageSize, loggedUser.getId());

        // Lógica para determinar si se deben mostrar los botones "Next" y "Previous"
        boolean showNext = reviews.size() == pageSize;  // Si hay tantas reseñas como el tamaño de página, hay más para mostrar
        boolean showPrevious = pageNum > 1;  // Mostrar "Previous" si no estamos en la primera página

        // Agregar los objetos al modelo
        mav.addObject("followingUsers", followingUsers);
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
                                      @ModelAttribute("loggedUser") User loggedUser,
                                      final BindingResult errors) {

        // Check if there are any validation errors
        if (errors.hasErrors()) {
            return editProfile(upf, loggedUser);
        }

        loggedUser.setUsername(upf.getUsername());
        loggedUser.setName(upf.getName());
        loggedUser.setBio(upf.getBio());
        userService.update(loggedUser, getBytes(upf.getProfilePicture()));
        return new ModelAndView("redirect:/user/profile");
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
        User user = userService.findById(userId).orElseThrow();

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

        // Añadir objetos al modelo
        mav.addObject("user", userService.findById(userId).get());
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
