package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.form.AlbumReviewForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequestMapping("/album")
@Controller
public class AlbumController {

    private final UserService userService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final SongService songService;
    private final AlbumReviewService albumReviewService;
    private final EmailService emailService;


    public AlbumController(UserService userService, ArtistService artistService, AlbumService albumService, SongService songService, AlbumReviewService albumReviewService, EmailService emailService) {
        this.userService = userService;
        this.artistService = artistService;
        this.albumService = albumService;
        this.songService = songService;
        this.albumReviewService = albumReviewService;
        this.emailService = emailService;
    }

    @RequestMapping("/")
    public ModelAndView findAll() {
        //TODO: Redirigir a algun artista.
        return new ModelAndView("redirect:/");
    }

    @RequestMapping("/{albumId:\\d+}")
    public ModelAndView album(@PathVariable(name = "albumId") long albumId) {
        final ModelAndView mav = new ModelAndView("album");

        Album album = albumService.findById(albumId).orElseThrow();
        List<Song> songs = songService.findByAlbumId(albumId);
        List<AlbumReview> reviews = albumReviewService.findByAlbumId(albumId);

        mav.addObject("album", album);
        mav.addObject("songs", songs);
        mav.addObject("artist", album.getArtist());
        mav.addObject("reviews", reviews);

        return mav;
    }

    @RequestMapping(value = "/{albumId}/reviews", method = RequestMethod.GET)
    public ModelAndView createForm(@ModelAttribute("albumReviewForm") final AlbumReviewForm albumReviewForm, @PathVariable Long albumId) {
        Album album = albumService.findById(albumId).orElseThrow();


        ModelAndView modelAndView = new ModelAndView("reviews/album_review");
        albumReviewForm.setAlbumId(album.getId());
        modelAndView.addObject("album", album);

        return modelAndView;
    }

    @RequestMapping(value = "/{albumId}/reviews", method = RequestMethod.POST)
    public ModelAndView create(@Valid @ModelAttribute("albumReviewForm") final AlbumReviewForm albumReviewForm, final BindingResult errors, @PathVariable Long albumId) throws MessagingException {
        if (errors.hasErrors()) {
            return createForm(albumReviewForm, albumId);
        }

        User savedUser = userService.findByEmail(albumReviewForm.getUserEmail()).orElseThrow();
        userService.incrementReviewAmount(savedUser);
        AlbumReview albumReview = new AlbumReview(
                savedUser,
                new Album(albumId),
                albumReviewForm.getTitle(),
                albumReviewForm.getDescription(),
                albumReviewForm.getRating(),
                LocalDateTime.now(),
                0
        );
        albumReviewService.save(albumReview);
        return new ModelAndView("redirect:/");
    }

}
