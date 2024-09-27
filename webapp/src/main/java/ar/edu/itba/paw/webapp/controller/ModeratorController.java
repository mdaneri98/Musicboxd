package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.*;

import ar.edu.itba.paw.services.*;

import ar.edu.itba.paw.webapp.form.ModAlbumForm;
import ar.edu.itba.paw.webapp.form.ModArtistForm;
import ar.edu.itba.paw.webapp.form.ModSongForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/mod")
@Controller
public class ModeratorController {

    private final ImageService imageService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final SongService songService;
    private final ReviewService reviewService;

    private final long DEFAULT_IMAGE_ID = 1;

    public ModeratorController(ImageService imageService, ArtistService artistService, AlbumService albumService, SongService songService, ReviewService reviewService) {
        this.imageService = imageService;
        this.artistService = artistService;
        this.albumService = albumService;
        this.songService = songService;
        this.reviewService = reviewService;
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public ModelAndView home() {
        final ModelAndView mav = new ModelAndView("/moderator/m_home");

        mav.addObject("artists", artistService.findAll());
        mav.addObject("albums", albumService.findAll());
        mav.addObject("songs", songService.findAll());

        return mav;
    }

    @RequestMapping(value = "/block/{reviewId:\\d+}", method = RequestMethod.GET)
    public ModelAndView block(@PathVariable(name = "reviewId") final long reviewId) {
        reviewService.block(reviewId);
        return new ModelAndView("redirect:/");
    }

    @RequestMapping(path = "add/artist", method = RequestMethod.GET)
    public ModelAndView addArtistForm(@ModelAttribute("modArtistForm") final ModArtistForm modArtistForm,
                                      @ModelAttribute("loggedUser") User loggedUser) {
        return new ModelAndView("moderator/add-artist");
    }

    @RequestMapping(path = "add/artist", method = RequestMethod.POST)
    public ModelAndView submitArtistForm(@Valid @ModelAttribute("modArtistForm") final ModArtistForm modArtistForm,
                                         @ModelAttribute("loggedUser") User loggedUser,
                                         final BindingResult errors) {

        // Check if there are any validation errors
        if (errors.hasErrors()) {
            return addArtistForm(modArtistForm, loggedUser);
        }
        long imageId = DEFAULT_IMAGE_ID;
        try {
            imageId = imageService.save(modArtistForm.getArtistImage().getBytes());
        } catch (IOException e) {
            e.printStackTrace();    //Change to logging ERROR
        }

        Artist artist = new Artist(modArtistForm.getName(),modArtistForm.getBio(), imageId);
        long artistId = artistService.save(artist);
        artist.setId(artistId);

        //  List of albums
        for (ModAlbumForm albumForm : modArtistForm.getAlbums()) {
            if (albumForm.getTitle() != null) {
                long imgId = DEFAULT_IMAGE_ID;
                try {
                    imgId = imageService.save(albumForm.getAlbumImage().getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Album album = new Album(albumForm.getTitle(), albumForm.getGenre(), imgId, artist);
                albumService.save(album);  // Save each album in the service
            }
        }

        ModelAndView modelAndView = new ModelAndView("redirect:/artist/" + artistId);
        modelAndView.addObject("artist", artist);
        return modelAndView;
    }

    @RequestMapping(path = "add/artist/{artistId:\\d+}/album", method = RequestMethod.GET)
    public ModelAndView addAlbumForm(@PathVariable(name = "artistId") final long artistId,
                                     @ModelAttribute("modAlbumForm") final ModAlbumForm modAlbumForm,
                                     @ModelAttribute("loggedUser") User loggedUser) {
        return new ModelAndView("moderator/add-album").addObject(artistId);
    }

    @RequestMapping(path = "add/artist/{artistId:\\d+}/album", method = RequestMethod.POST)
    public ModelAndView submitAlbumForm(@PathVariable(name = "artistId") final long artistId,
                                        @Valid @ModelAttribute("modAlbumForm") final ModAlbumForm modAlbumForm,
                                        @ModelAttribute("loggedUser") User loggedUser,
                                        final BindingResult errors) {

        // Check if there are any validation errors
        if (errors.hasErrors()) {
            return addAlbumForm(artistId, modAlbumForm, loggedUser);
        }

        long imageId = DEFAULT_IMAGE_ID;
        try {
            imageId = imageService.save(modAlbumForm.getAlbumImage().getBytes());
        } catch (IOException e) {
            e.printStackTrace();    //Change to logging ERROR
        }
        Artist artist = artistService.findById(artistId).get();
        Album album = new Album(modAlbumForm.getTitle(), modAlbumForm.getGenre(), imageId, artist);
        albumService.save(album);

        //  List of songs
        for (ModSongForm songForm : modAlbumForm.getSongs()) {
            if (songForm.getTitle() != null) {
                Song song = new Song(songForm.getTitle(), songForm.getDuration(), songForm.getTrackNumber().intValue(), album);
                songService.save(song); //save each song in the service
            }
        }

        ModelAndView modelAndView = new ModelAndView("redirect:/artist/" + artistId);
        modelAndView.addObject("artist", artist);
        return modelAndView;
    }

    @RequestMapping(path = "/add/album/{albumId:\\d+}/song", method = RequestMethod.GET)
    public ModelAndView addSongForm(@PathVariable(name = "albumId") final long albumId,
                                    @ModelAttribute("modSongForm") final ModSongForm modSongForm,
                                    @ModelAttribute("loggedUser") User loggedUser) {
        return new ModelAndView("moderator/add-song").addObject(albumId);
    }

    @RequestMapping(path = "add/album/{albumId:\\d+}/song", method = RequestMethod.POST)
    public ModelAndView submitSongForm(@PathVariable(name = "albumId") final long albumId,
                                       @Valid @ModelAttribute("modSongForm") final ModSongForm modSongForm,
                                       @ModelAttribute("loggedUser") User loggedUser,
                                       final BindingResult errors) {

        if (errors.hasErrors()) {
            return addSongForm(albumId, modSongForm, loggedUser);
        }

        Album album = albumService.findById(albumId).get();
        Song song = new Song(modSongForm.getTitle(), modSongForm.getDuration(), modSongForm.getTrackNumber().intValue(), album);
        songService.save(song);

        ModelAndView modelAndView = new ModelAndView("redirect:/album/" + albumId);
        modelAndView.addObject("album", album);
        return modelAndView;
    }
}
