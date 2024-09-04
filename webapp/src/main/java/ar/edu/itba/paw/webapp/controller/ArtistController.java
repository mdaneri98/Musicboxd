package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.Album;
import ar.edu.itba.paw.Artist;
import ar.edu.itba.paw.Song;
import ar.edu.itba.paw.User;
import ar.edu.itba.paw.reviews.ArtistReview;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.form.ArtistReviewForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RequestMapping ("/artist")
@Controller
public class ArtistController {

    private final UserService userService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final SongService songService;
    private final ArtistReviewService artistReviewService;
    private final EmailService emailService;

    public ArtistController(UserService userService, ArtistService artistService, AlbumService albumService, SongService songService, ArtistReviewService artistReviewService, EmailService emailService) {
        this.userService = userService;
        this.artistService = artistService;
        this.albumService = albumService;
        this.songService = songService;
        this.artistReviewService = artistReviewService;
        this.emailService = emailService;
    }

    @RequestMapping("/")
    public ModelAndView findAll() {
        //TODO: Redirigir a algun artista.
        return new ModelAndView("redirect:/");
    }

    @RequestMapping("/{artistId:\\d+}")
    public ModelAndView artist(@PathVariable(name = "artistId") long artistId) {
        final ModelAndView mav = new ModelAndView("artist");

        Artist artist = artistService.findById(artistId).get();
        List<Album> albums = albumService.findByArtistId(artistId);
        List<Song> songs = songService.findByArtistId(artistId);
        List<ArtistReview> reviews = artistReviewService.findByArtistId(artistId);

        mav.addObject("artist", artist);
        mav.addObject("albums", albums);
        mav.addObject("songs", songs);
        mav.addObject("reviews", reviews);
        return mav;
    }

    @RequestMapping(value = "/{artistId}/reviews", method = RequestMethod.GET)
    public ModelAndView createForm(@ModelAttribute("artistReviewForm") final ArtistReviewForm artistReviewForm, @PathVariable Long artistId) {
        Optional<Artist> artistOptional = artistService.findById(artistId);
        if (artistOptional.isEmpty())
            return null;

        Artist artist = artistOptional.get();

        ModelAndView modelAndView = new ModelAndView("reviews/artist_review");
        artistReviewForm.setArtistId(artist.getId());
        modelAndView.addObject("artist", artist);

        return modelAndView;
    }

    @RequestMapping(value = "/{artistId}/reviews", method = RequestMethod.POST)
    public ModelAndView create(@Valid @ModelAttribute("artistReviewForm") final ArtistReviewForm artistReviewForm, final BindingResult errors, @PathVariable Long artistId) throws MessagingException {
        if (errors.hasErrors()) {
            return createForm(artistReviewForm, artistId);
        }

        Optional<User> optUser = userService.findByEmail(artistReviewForm.getUserEmail());
        if (optUser.isEmpty()) {
            User unverifiedUser = User.unverifiedUser(artistReviewForm.getUserEmail());
            userService.save(unverifiedUser);
            emailService.sendVerification(unverifiedUser.getEmail());
        }

        User savedUser = userService.findByEmail(artistReviewForm.getUserEmail()).orElseThrow();
        ArtistReview artistReview = new ArtistReview(
                savedUser.getId(),
                artistId,
                artistReviewForm.getTitle(),
                artistReviewForm.getDescription(),
                artistReviewForm.getRating(),
                LocalDateTime.now(),
                0
        );
        artistReviewService.save(artistReview);
        return new ModelAndView("redirect:/");
    }

    // Métodos adicionales para editar y eliminar reseñas
    // ...
}
