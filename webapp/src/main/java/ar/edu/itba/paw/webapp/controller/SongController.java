package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.form.AlbumReviewForm;
import ar.edu.itba.paw.webapp.form.SongReviewForm;
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

@RequestMapping("/song")
@Controller
public class SongController {

    private final UserService userService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final SongService songService;
    private final SongReviewService songReviewService;
    private final EmailService emailService;


    public SongController(UserService userService, ArtistService artistService, AlbumService albumService, SongService songService, AlbumReviewService albumReviewService, SongReviewService songReviewService, EmailService emailService) {
        this.userService = userService;
        this.artistService = artistService;
        this.albumService = albumService;
        this.songService = songService;
        this.songReviewService = songReviewService;
        this.emailService = emailService;
    }

    @RequestMapping("/")
    public ModelAndView findAll() {
        //TODO: Redirigir a algun artista.
        return new ModelAndView("redirect:/");
    }

    @RequestMapping("/{songId:\\d+}")
    public ModelAndView song(@PathVariable(name = "songId") long songId) {
        final ModelAndView mav = new ModelAndView("song");

        Song song = songService.findById(songId).get();
        List<Artist> artists = artistService.findBySongId(songId);
        List<SongReview> reviews = songReviewService.findBySongId(songId);

        mav.addObject("album", song.getAlbum());
        mav.addObject("artists", artists);
        mav.addObject("song", song);
        mav.addObject("reviews", reviews);
        return mav;
    }

    @RequestMapping(value = "/{songId:\\d+}/reviews", method = RequestMethod.GET)
    public ModelAndView createForm(@ModelAttribute("songReviewForm") final SongReviewForm songReviewForm, @PathVariable Long songId) {
        final ModelAndView mav = new ModelAndView("reviews/song_review");
        songReviewForm.setSongId(songId);
        Song song = songService.findById(songId).get();
        mav.addObject("song", song);
        mav.addObject("album", song.getAlbum());
        return mav;
    }


    @RequestMapping(value = "/{songId:\\d+}/reviews", method = RequestMethod.POST)
    public ModelAndView create(@Valid @ModelAttribute("songReviewForm") final SongReviewForm songReviewForm, final BindingResult errors, @PathVariable Long songId) throws MessagingException {
        if (errors.hasErrors()) {
            return createForm(songReviewForm, songId);
        }

        User savedUser = userService.findByEmail(songReviewForm.getUserEmail()).orElseThrow();
        userService.incrementReviewAmount(savedUser);
        SongReview songReview = new SongReview(
                savedUser,
                new Song(songId),
                songReviewForm.getTitle(),
                songReviewForm.getDescription(),
                songReviewForm.getRating(),
                LocalDateTime.now(),
                0
        );
        songReviewService.save(songReview);
        return new ModelAndView("redirect:/");
    }
}
