package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.*;

import ar.edu.itba.paw.services.AlbumService;
import ar.edu.itba.paw.services.ArtistService;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.services.SongService;

import ar.edu.itba.paw.webapp.form.ModAlbumForm;
import ar.edu.itba.paw.webapp.form.ModArtistForm;
import ar.edu.itba.paw.webapp.form.ModSongForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

    private final long DEFAULT_IMAGE_ID = 1;

    public ModeratorController(ImageService imageService, ArtistService artistService, AlbumService albumService, SongService songService) {
        this.imageService = imageService;
        this.artistService = artistService;
        this.albumService = albumService;
        this.songService = songService;
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public ModelAndView home() {
        final ModelAndView mav = new ModelAndView("/moderator/m_home");

        mav.addObject("artists", artistService.findAll());
        mav.addObject("albums", albumService.findAll());
        mav.addObject("songs", songService.findAll());

        return mav;
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
            imageId = imageService.save(modArtistForm.getFile().getBytes());
        } catch (IOException e) {
            e.printStackTrace();    //Change to logging ERROR
        }

        Artist artist = new Artist(modArtistForm.getName(),modArtistForm.getBio(), imageId);
        long artistId = artistService.save(artist);

        ModelAndView modelAndView = new ModelAndView("redirect:/artist/" + artistId);
        modelAndView.addObject("artist", artist);
        return modelAndView;
    }

    @RequestMapping(path = "add/artist/{artistId:\\d+}/album", method = RequestMethod.GET)
    public ModelAndView addAlbumForm(@PathVariable(name = "artistId") final long artistId,
                                     @ModelAttribute("modAlbumFrom") final ModAlbumForm modAlbumForm,
                                     @ModelAttribute("loggedUser") User loggedUser) {
        return new ModelAndView("moderator/add-album").addObject(artistId);
    }

    @RequestMapping(path = "add/artist/{artistId:\\d+}/album", method = RequestMethod.POST)
    public ModelAndView submitAlbumForm(@PathVariable(name = "artistId") final long artistId,
                                        @Valid @ModelAttribute("modAlbumFrom") final ModAlbumForm modAlbumForm,
                                        @ModelAttribute("loggedUser") User loggedUser,
                                        final BindingResult errors) {

        // Check if there are any validation errors
        if (errors.hasErrors()) {
            return addAlbumForm(artistId, modAlbumForm, loggedUser);
        }

        long imageId = DEFAULT_IMAGE_ID;
        try {
            imageId = imageService.save(modAlbumForm.getFile().getBytes());
        } catch (IOException e) {
            e.printStackTrace();    //Change to logging ERROR
        }
        Artist artist = artistService.findById(artistId).get();
        Album album = new Album(modAlbumForm.getTitle(), modAlbumForm.getGenre(), imageId, artist);
        albumService.save(album);

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
