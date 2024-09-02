package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.Album;
import ar.edu.itba.paw.Artist;
import ar.edu.itba.paw.services.AlbumService;
import ar.edu.itba.paw.services.ArtistService;
import ar.edu.itba.paw.services.SongService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@RequestMapping("/album")
@Controller
public class AlbumController {

    //private final UserService userService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final SongService songService;
    //private final ArtistReviewService artistReviewService;


    public AlbumController(ArtistService artistService, AlbumService albumService, SongService songService) {
        this.artistService = artistService;
        this.albumService = albumService;
        this.songService = songService;
    }

    @RequestMapping("/")
    public ModelAndView findAll() {
        //TODO: Redirigir a algun artista.
        return new ModelAndView("redirect:/");
    }

    @RequestMapping("/{albumId:\\d+}")
    public ModelAndView artist(@PathVariable(name = "albumId") long albumId) {
        Album album = albumService.findById(albumId).orElseThrow();
        Artist artist = artistService.findById(album.getArtistId()).orElseThrow();

        final ModelAndView mav = new ModelAndView("album");
        mav.addObject("album", album);
        mav.addObject("songs", songService.findByAlbumId(album.getId()));
        mav.addObject("artist", artist);

        return mav;
    }

}

/*

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
 */

