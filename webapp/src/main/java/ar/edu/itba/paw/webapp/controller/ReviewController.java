package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.Artist;
import ar.edu.itba.paw.reviews.ArtistReview;
import ar.edu.itba.paw.services.AlbumReviewService;
import ar.edu.itba.paw.services.ArtistReviewService;
import ar.edu.itba.paw.services.SongReviewService;
import ar.edu.itba.paw.webapp.form.ReviewForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.TimeZone;


@Controller
public class ReviewController {

    private final ArtistReviewService artistReviewService;
    private final AlbumReviewService albumReviewService;
    private final SongReviewService songReviewService;

    public ReviewController(ArtistReviewService artistReviewService, AlbumReviewService albumReviewService, SongReviewService songReviewService) {
        this.artistReviewService = artistReviewService;
        this.albumReviewService = albumReviewService;
        this.songReviewService = songReviewService;
    }

    @RequestMapping(value = "/{entityType}/{entityId}/reviews", method = RequestMethod.GET)
    public ModelAndView createForm(@ModelAttribute("reviewForm") final ReviewForm reviewForm, @PathVariable String entityType, @PathVariable Long entityId) {
        String viewName = "reviews/" + entityType + "_review";

        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addObject(entityType + "_id", entityId);

        return modelAndView;
    }

    @RequestMapping(value = "/{entityType}/{entityId}/reviews", method = RequestMethod.POST)
    public ModelAndView create(@Valid @ModelAttribute("reviewForm") final ReviewForm reviewForm, final BindingResult errors, @PathVariable String entityType, @PathVariable Long entityId, @RequestParam(name = "userId") long userId) {
        if (errors.hasErrors()) {
            return createForm(reviewForm, entityType, entityId);
        }

        if (entityType.equals("album")) {

        } else if (entityType.equals("song")) {

        } else if (entityType.equals("artist")) {
            ArtistReview artistReview = new ArtistReview(
                    userId,
                    entityId,
                    reviewForm.getTitle(),
                    reviewForm.getDescription(),
                    reviewForm.getRating(),
                    LocalDateTime.now(),
                    0
            );
            artistReviewService.save(artistReview);
        }

        return new ModelAndView("redirect:/");
    }

    // Métodos adicionales para editar y eliminar reseñas
    // ...
}
