package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.*;

import ar.edu.itba.paw.services.*;

import ar.edu.itba.paw.webapp.form.ModAlbumForm;
import ar.edu.itba.paw.webapp.form.ModArtistForm;
import ar.edu.itba.paw.webapp.form.ModSongForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@RequestMapping("/mod")
@Controller
public class ModeratorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModeratorController.class);

    private final ArtistService artistService;
    private final AlbumService albumService;
    private final SongService songService;
    private final ReviewService reviewService;

    public ModeratorController(ArtistService artistService, AlbumService albumService, SongService songService, ReviewService reviewService) {
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

    @RequestMapping(value = "/unblock/{reviewId:\\d+}", method = RequestMethod.GET)
    public ModelAndView unblock(@PathVariable(name = "reviewId") final long reviewId) {
        reviewService.unblock(reviewId);
        return new ModelAndView("redirect:/");
    }

    @RequestMapping(path = "/add/artist", method = RequestMethod.GET)
    public ModelAndView addArtistForm(@ModelAttribute("modArtistForm") final ModArtistForm modArtistForm,
                                      @ModelAttribute("loggedUser") User loggedUser) {
        ModelAndView modelAndView = new ModelAndView("moderator/add-artist");
        modelAndView.addObject("postUrl", "/mod/add/artist");
        return modelAndView;
    }

    @RequestMapping(path = "/add/artist", method = RequestMethod.POST)
    public ModelAndView submitArtistForm(@Valid @ModelAttribute("modArtistForm") final ModArtistForm modArtistForm,
                                         @ModelAttribute("loggedUser") User loggedUser,
                                         final BindingResult errors) {

        // Check if there are any validation errors
        if (errors.hasErrors()) {
            return addArtistForm(modArtistForm, loggedUser);
        }

        // Save new Artist
        Artist artist = convert(modArtistForm);
        artist.setId(artistService.save(artist, modArtistForm.getArtistImage()));

        // TODO: hacer lo asi
        //artist = artistService.save(artist, modArtistForm.getArtistImage());

        // Save new Albums
        for (ModAlbumForm albumForm : modArtistForm.getAlbums()) {
            if(!albumForm.isDeleted()) {
                albumService.save(convert(albumForm, artist), albumForm.getAlbumImage());
            }
        }

        ModelAndView modelAndView = new ModelAndView("redirect:/artist/" + artist.getId());
        modelAndView.addObject("artist", artist);
        return modelAndView;
    }

    @RequestMapping(path = "edit/artist/{artistId:\\d+}", method = RequestMethod.GET)
    public ModelAndView editArtistForm(@ModelAttribute("modArtistForm") final ModArtistForm modArtistForm,
                                       @ModelAttribute("loggedUser") User loggedUser,
                                       @PathVariable(name = "artistId") long artistId) {

        ModelAndView modelAndView = new ModelAndView("moderator/add-artist");
        modelAndView.addObject("postUrl", "/mod/edit/artist/" + artistId);

        Optional<Artist> artist = artistService.findById(artistId);
        if (artist.isPresent()) {
            // Fill data
            modArtistForm.setId(artistId);
            modArtistForm.setName(artist.get().getName());
            modArtistForm.setBio(artist.get().getBio());

            // Add Artist Image id
            modelAndView.addObject("artistImgId",artist.get().getImgId());

            // Add Albums
            List<Album> albums = albumService.findByArtistId(artistId);
            List<ModAlbumForm> albumForms = new ArrayList<>();
            List<Long> albumImgId = new ArrayList<>();
            for (Album album : albums) {
                // Create ModAlbumForm
                ModAlbumForm albumForm = new ModAlbumForm();

                // Fill Data
                albumForm.setId(album.getId());
                albumForm.setTitle(album.getTitle());
                albumForm.setGenre(album.getGenre());

                // Add ModAlbumForm to List
                albumForms.add(albumForm);

                // Add Album Image id
                albumImgId.add(album.getImgId());
            }
            modelAndView.addObject("albumImgId",albumImgId);
            modArtistForm.setAlbum(albumForms);
        } else {
            LOGGER.debug("Error in *GET* '/mod/edit/artist' no artist with id {}", artistId);
            return new ModelAndView("redirect:/error");
        }
        return modelAndView;
    }

    @RequestMapping(path = "edit/artist/{artistId:\\d+}", method = RequestMethod.POST)
    public ModelAndView submitArtistForm(@ModelAttribute("modArtistForm") final ModArtistForm modArtistForm,
                                         @ModelAttribute("loggedUser") User loggedUser,
                                         @PathVariable(name = "artistId") long artistId,
                                         final BindingResult errors) {

        // Check if there are any validation errors
        if (errors.hasErrors()) {
            return editArtistForm(modArtistForm, loggedUser, artistId);
        }

        Optional<Artist> artist = artistService.findById(artistId);

        if(artist.isEmpty()) {
            LOGGER.debug("Error in *POST* '/mod/edit/artist' no artist with id {}", artistId);
            return new ModelAndView("redirect:/error");
        }

        if (modArtistForm.isDeleted()) {
            artistService.delete(artist.get());
            return new ModelAndView("redirect:/home");
        }

        // Update Artist
        artistService.update(artist.get(), convert(modArtistForm), modArtistForm.getArtistImage());

        // Update Artist Albums
        for (ModAlbumForm albumForm : modArtistForm.getAlbums()) {
            Optional<Album> album = albumService.findById(albumForm.getId());

            if (album.isPresent()) {
                if (albumForm.isDeleted()) {
                    // Delete Album and its Songs
                    albumService.delete(album.get());
                } else {
                    // Update Album
                    albumService.update(album.get(), convert(albumForm, artist.get()), albumForm.getAlbumImage());
                }
            } else if ( !albumForm.isDeleted()) {
                // Save new Album
                albumService.save(convert(albumForm, artist.get()), albumForm.getAlbumImage());
            }
        }

        ModelAndView modelAndView = new ModelAndView("redirect:/artist/" + artistId);
        modelAndView.addObject("artist", artist.get());
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

        Optional<Artist> artist = artistService.findById(artistId);
        if(artist.isEmpty()) {
            LOGGER.debug("Error in *POST* '/mod/add/artist'. No artist with id {}", artistId);
            return new ModelAndView("redirect:/error");
        }

        Album album = convert(modAlbumForm, artist.get());
        album.setId(albumService.save(album, modAlbumForm.getAlbumImage()));

        //  List of songs
        for (ModSongForm songForm : modAlbumForm.getSongs()) {
            if (!songForm.isDeleted()) {
                songService.save(convert(songForm, album));
            }
        }

        ModelAndView modelAndView = new ModelAndView("redirect:/artist/" + artistId);
        modelAndView.addObject("artist", artist.get());
        return modelAndView;
    }

    @RequestMapping(path = "edit/album/{albumId:\\d+}", method = RequestMethod.GET)
    public ModelAndView editAlbumForm(@PathVariable(name = "albumId") final long albumId,
                                     @ModelAttribute("modAlbumForm") final ModAlbumForm modAlbumForm,
                                     @ModelAttribute("loggedUser") User loggedUser) {

        ModelAndView modelAndView = new ModelAndView("moderator/add-album");
        modelAndView.addObject("postUrl", "/mod/edit/album/" + albumId);

        Optional<Album> album = albumService.findById(albumId);
        if (album.isEmpty()) {
            LOGGER.debug("Error in *GET* '/mod/edit/album' no album with id {}", albumId);
            return new ModelAndView("redirect:/error");
        }
        // Fill data
        modAlbumForm.setId(albumId);
        modAlbumForm.setTitle(album.get().getTitle());
        modAlbumForm.setGenre(album.get().getGenre());

        // Add Album Image id
        modelAndView.addObject("albumImgId",album.get().getImgId());

        // Add Songs
        List<Song> songs = songService.findByAlbumId(albumId);
        List<ModSongForm> songForms = new ArrayList<>();
        for (Song song : songs) {
            ModSongForm songForm = new ModSongForm();

            songForm.setId(song.getId());
            songForm.setTitle(song.getTitle());
            songForm.setDuration(song.getDuration());
            songForm.setTrackNumber(song.getTrackNumber());

            songForms.add(songForm);
        }

        modAlbumForm.setSongs(songForms);
        return modelAndView;
    }

    @RequestMapping(path = "edit/album/{albumId:\\d+}", method = RequestMethod.POST)
    public ModelAndView submitEditAlbumForm(@PathVariable(name = "albumId") final long albumId,
                                      @ModelAttribute("modAlbumForm") final ModAlbumForm modAlbumForm,
                                      @ModelAttribute("loggedUser") User loggedUser,
                                      final BindingResult errors) {

        // Check if there are any validation errors
        if (errors.hasErrors()) {
            return editAlbumForm(albumId, modAlbumForm, loggedUser);
        }

        Optional<Album> album = albumService.findById(albumId);

        if(album.isEmpty()) {
            LOGGER.debug("Error in *POST* '/mod/edit/album' no album with id {}", albumId);
            return new ModelAndView("redirect:/error");
        }

        // Update Album
        albumService.update(album.get(), convert(modAlbumForm, album.get().getArtist()), modAlbumForm.getAlbumImage());

        //Update Albums Songs
        for (ModSongForm songForm : modAlbumForm.getSongs()) {
            Optional<Song> song = songService.findById(songForm.getId());

            if (song.isPresent()) {
                if (songForm.isDeleted()) {
                    // Delete Song
                    songService.deleteById(songForm.getId());
                } else {
                    // Update Song
                    songService.update(song.get(), convert(songForm, album.get()));
                }
            } else if ( !songForm.isDeleted()) {
                // Save new Song
                songService.save(convert(songForm, album.get()));
            }
        }

        ModelAndView modelAndView = new ModelAndView("redirect:/album/" + albumId);
        modelAndView.addObject("album", album.get());
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

        Optional<Album> album = albumService.findById(albumId);

        if(album.isEmpty()) {
            LOGGER.debug("Error in *POST* '/add/album' no album with id {}", albumId);
            return new ModelAndView("redirect:/error");
        }

        Song song = convert(modSongForm, album.get());
        songService.save(song);

        ModelAndView modelAndView = new ModelAndView("redirect:/album/" + albumId);
        modelAndView.addObject("album", album.get());
        return modelAndView;
    }

    @RequestMapping(path = "/edit/song/{songId:\\d+}", method = RequestMethod.GET)
    public ModelAndView editSongForm(@PathVariable(name = "songId") final long songId,
                                    @ModelAttribute("modSongForm") final ModSongForm modSongForm,
                                    @ModelAttribute("loggedUser") User loggedUser) {

        ModelAndView modelAndView = new ModelAndView("moderator/add-song");
        modelAndView.addObject("postUrl", "/mod/edit/song/" + songId);

        Optional<Song> song = songService.findById(songId);
        if (song.isEmpty()) {
            LOGGER.debug("Error in *GET* '/mod/edit/song' no song with id {}", songId);
            return new ModelAndView("redirect:/error");
        }
        // Fill data
        modSongForm.setId(songId);
        modSongForm.setTitle(song.get().getTitle());
        modSongForm.setDuration(song.get().getDuration());
        modSongForm.setTrackNumber(song.get().getTrackNumber());

        return modelAndView;
    }

    @RequestMapping(path = "/edit/song/{songId:\\d+}", method = RequestMethod.POST)
    public ModelAndView submitEditSongForm(@PathVariable(name = "songId") final long songId,
                                     @ModelAttribute("modSongForm") final ModSongForm modSongForm,
                                     @ModelAttribute("loggedUser") User loggedUser,
                                     final BindingResult errors) {

        if (errors.hasErrors()) {
            return editSongForm(songId, modSongForm, loggedUser);
        }

        Optional<Song> song = songService.findById(songId);

        if (song.isEmpty()) {
            LOGGER.debug("Error in *POST* '/edit/song' no song with id {}", songId);
            return new ModelAndView("redirect:/error");
        }

        songService.update(song.get(), convert(modSongForm, song.get().getAlbum()));

        ModelAndView modelAndView = new ModelAndView("redirect:/song/" + songId);
        modelAndView.addObject("song", song.get());
        return modelAndView;
    }

    @RequestMapping(path = "/update-ratings", method = RequestMethod.GET)
    public ModelAndView updateAvgRatingForAll(@ModelAttribute("loggedUser") User loggedUser) {
        reviewService.updateAvgRatingForAll();
        return new ModelAndView("redirect:/");
    }

    private Artist convert(ModArtistForm modArtistForm) {
        return new Artist(modArtistForm.getName(), modArtistForm.getBio(), null);
    }

    private Album convert(ModAlbumForm modAlbumForm, Artist artist) {
        return new Album(modAlbumForm.getTitle(), modAlbumForm.getGenre(), null, artist);
    }

    private Song convert(ModSongForm modSongForm, Album album) {
        return new Song(modSongForm.getTitle(), modSongForm.getDuration(), modSongForm.getTrackNumber(), album);
    }
}
