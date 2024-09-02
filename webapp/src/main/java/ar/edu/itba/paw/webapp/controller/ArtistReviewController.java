package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.Artist;
import ar.edu.itba.paw.reviews.ArtistReview;
import ar.edu.itba.paw.services.AlbumReviewService;
import ar.edu.itba.paw.services.ArtistReviewService;
import ar.edu.itba.paw.services.ArtistService;
import ar.edu.itba.paw.services.SongReviewService;
import ar.edu.itba.paw.webapp.form.ArtistReviewForm;
import ar.edu.itba.paw.webapp.form.ReviewForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.TimeZone;


@Controller
public class ArtistReviewController {

    private final ArtistReviewService artistReviewService;
    private final ArtistService artistService;

    public ArtistReviewController(ArtistReviewService artistReviewService, ArtistService artistService) {
        this.artistReviewService = artistReviewService;
        this.artistService = artistService;
    }

    @RequestMapping(value = "/artist/{artistId}/reviews", method = RequestMethod.GET)
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

    @RequestMapping(value = "/artist/{artistId}/reviews", method = RequestMethod.POST)
    public ModelAndView create(@Valid @ModelAttribute("artistReviewForm") final ArtistReviewForm artistReviewForm, final BindingResult errors, @PathVariable Long artistId) {
        if (errors.hasErrors()) {
            return createForm(artistReviewForm, artistId);
        }

        ArtistReview artistReview = new ArtistReview(
                artistReviewForm.getUserId(),
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
