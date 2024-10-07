package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.*;

import ar.edu.itba.paw.models.dtos.AlbumDTO;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import ar.edu.itba.paw.models.dtos.SongDTO;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.form.ModAlbumForm;
import ar.edu.itba.paw.webapp.form.ModArtistForm;
import ar.edu.itba.paw.webapp.form.ModSongForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RequestMapping("/mod")
@Controller
public class ModeratorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModeratorController.class);

    private final ArtistService artistService;
    private final AlbumService albumService;
    private final SongService songService;
    private final ReviewService reviewService;
    private final MessageSource messageSource;
    private final ImageService imageService;

    public ModeratorController(ArtistService artistService, AlbumService albumService, SongService songService, ReviewService reviewService, MessageSource messageSource, ImageService imageService) {
        this.artistService = artistService;
        this.albumService = albumService;
        this.songService = songService;
        this.reviewService = reviewService;
        this.messageSource = messageSource;
        this.imageService = imageService;
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public ModelAndView home() {
        return new ModelAndView("/moderator/m_home");
    }

    @RequestMapping(value = "/block/{reviewId:\\d+}", method = RequestMethod.GET)
    public ModelAndView block(@PathVariable(name = "reviewId") final long reviewId) {
        reviewService.block(reviewId);
        LOGGER.info("Review with ID {} has been blocked", reviewId);
        return new ModelAndView("redirect:/");
    }

    @RequestMapping(value = "/unblock/{reviewId:\\d+}", method = RequestMethod.GET)
    public ModelAndView unblock(@PathVariable(name = "reviewId") final long reviewId) {
        reviewService.unblock(reviewId);
        LOGGER.info("Review with ID {} has been unblocked", reviewId);
        return new ModelAndView("redirect:/");
    }

    @RequestMapping(path = "/add/artist", method = RequestMethod.GET)
    public ModelAndView addArtistForm(@ModelAttribute("modArtistForm") final ModArtistForm modArtistForm,
                                      @ModelAttribute("loggedUser") User loggedUser) {
        ModelAndView modelAndView = new ModelAndView("moderator/add-artist");
        modelAndView.addObject("postUrl", "/mod/add/artist");
        modelAndView.addObject("defaultImgId", imageService.getDefaultImgId());
        return modelAndView;
    }

    @RequestMapping(path = "/add/artist", method = RequestMethod.POST)
    public ModelAndView submitArtistForm(@Valid @ModelAttribute("modArtistForm") final ModArtistForm modArtistForm,
                                         final BindingResult errors,
                                         @ModelAttribute("loggedUser") User loggedUser) {

        // Check if there are any validation errors
        if (errors.hasErrors()) {
            return addArtistForm(modArtistForm, loggedUser);
        }

        Artist artist = artistService.create(transformArtistToDTO(modArtistForm));
        LOGGER.info("New artist created with ID: {}", artist.getId());

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
        modelAndView.addObject("defaultImgId", imageService.getDefaultImgId());

        Optional<Artist> artist = artistService.find(artistId);
        if (artist.isEmpty()) {
            LOGGER.debug("Error in *GET* '/mod/edit/artist' no artist with id {}", artistId);
            String errorMessage = messageSource.getMessage("error.artist.find", null, LocaleContextHolder.getLocale());
            return new ModelAndView("redirect:/?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        }

        // Fill data
        modArtistForm.setId(artistId);
        modArtistForm.setName(artist.get().getName());
        modArtistForm.setBio(artist.get().getBio());
        modArtistForm.setArtistImgId(artist.get().getImgId());

        // Add Albums
        List<Album> albums = albumService.findByArtistId(artistId);
        List<ModAlbumForm> albumForms = new ArrayList<>();
        for (Album album : albums) {
            ModAlbumForm albumForm = new ModAlbumForm();

            albumForm.setId(album.getId());
            albumForm.setTitle(album.getTitle());
            albumForm.setGenre(album.getGenre());
            albumForm.setAlbumImageId(album.getImgId());
            albumForm.setReleaseDate(album.getReleaseDate());

            albumForms.add(albumForm);
        }
        modArtistForm.setAlbum(albumForms);
        return modelAndView;
    }

    @RequestMapping(path = "edit/artist/{artistId:\\d+}", method = RequestMethod.POST)
    public ModelAndView submitArtistForm(@Valid @ModelAttribute("modArtistForm") final ModArtistForm modArtistForm,
                                         final BindingResult errors,
                                         @ModelAttribute("loggedUser") User loggedUser,
                                         @PathVariable(name = "artistId") long artistId) {

        if (errors.hasErrors())
            return editArtistForm(modArtistForm, loggedUser, artistId);

        Artist artist = artistService.update(transformArtistToDTO(modArtistForm));
        LOGGER.info("Artist with ID {} has been updated", artistId);

        ModelAndView modelAndView = new ModelAndView("redirect:/artist/" + artistId);
        modelAndView.addObject("artist", artist);
        return modelAndView;
    }

    @RequestMapping(path = "add/artist/{artistId:\\d+}/album", method = RequestMethod.GET)
    public ModelAndView addAlbumForm(@PathVariable(name = "artistId") final long artistId,
                                     @ModelAttribute("modAlbumForm") final ModAlbumForm modAlbumForm,
                                     @ModelAttribute("loggedUser") User loggedUser) {
        Optional<Artist> artist = artistService.find(artistId);
        if(artist.isEmpty()) {
            LOGGER.debug("Error in *GET* '/mod/add/artist/{}/album'. No artist with id {}", artistId, artistId);
            String errorMessage = messageSource.getMessage("error.artist.find", null, LocaleContextHolder.getLocale());
            return new ModelAndView("redirect:/?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        }

        ModelAndView modelAndView = new ModelAndView("moderator/add-album").addObject(artistId);
        modelAndView.addObject("postUrl", "/mod/add/artist/" + artistId + "/album");
        modelAndView.addObject("defaultImgId", imageService.getDefaultImgId());
        return modelAndView;
    }

    @RequestMapping(path = "add/artist/{artistId:\\d+}/album", method = RequestMethod.POST)
    public ModelAndView submitAlbumForm(@PathVariable(name = "artistId") final long artistId,
                                        @Valid @ModelAttribute("modAlbumForm") final ModAlbumForm modAlbumForm,
                                        final BindingResult errors,
                                        @ModelAttribute("loggedUser") User loggedUser) {
        if (errors.hasErrors())
            return addAlbumForm(artistId, modAlbumForm, loggedUser);

        Album album = albumService.create(transformAlbumToDTO(modAlbumForm), artistId);
        LOGGER.info("New album created with ID: {} for artist with ID: {}", album.getId(), artistId);

        ModelAndView modelAndView = new ModelAndView("redirect:/album/" + album.getId());
        modelAndView.addObject("album", album);
        return modelAndView;
    }

    @RequestMapping(path = "edit/album/{albumId:\\d+}", method = RequestMethod.GET)
    public ModelAndView editAlbumForm(@PathVariable(name = "albumId") final long albumId,
                                     @ModelAttribute("modAlbumForm") final ModAlbumForm modAlbumForm,
                                     @ModelAttribute("loggedUser") User loggedUser) {

        ModelAndView modelAndView = new ModelAndView("moderator/add-album");
        modelAndView.addObject("postUrl", "/mod/edit/album/" + albumId);
        modelAndView.addObject("defaultImgId", imageService.getDefaultImgId());

        Optional<Album> album = albumService.find(albumId);
        if (album.isEmpty()) {
            LOGGER.debug("Error in *GET* '/mod/edit/album' no album with id {}", albumId);
            String errorMessage = messageSource.getMessage("error.album.find", null, LocaleContextHolder.getLocale());
            return new ModelAndView("redirect:/?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        }

        // Fill data
        modAlbumForm.setId(albumId);
        modAlbumForm.setTitle(album.get().getTitle());
        modAlbumForm.setGenre(album.get().getGenre());
        modAlbumForm.setReleaseDate(album.get().getReleaseDate());
        modAlbumForm.setAlbumImageId(album.get().getImgId());
        modAlbumForm.setArtistId(album.get().getArtist().getId());

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
                                            @Valid @ModelAttribute("modAlbumForm") final ModAlbumForm modAlbumForm,
                                            final BindingResult errors,
                                            @ModelAttribute("loggedUser") User loggedUser) {

        if (errors.hasErrors())
            return editAlbumForm(albumId, modAlbumForm, loggedUser);

        Album newAlbum = albumService.update(transformAlbumToDTO(modAlbumForm));
        LOGGER.info("Album with ID {} has been updated", albumId);

        ModelAndView modelAndView = new ModelAndView("redirect:/album/" + albumId);
        modelAndView.addObject("album", newAlbum);
        return modelAndView;
    }

    @RequestMapping(path = "/add/album/{albumId:\\d+}/song", method = RequestMethod.GET)
    public ModelAndView addSongForm(@PathVariable(name = "albumId") final long albumId,
                                    @ModelAttribute("modSongForm") final ModSongForm modSongForm,
                                    @ModelAttribute("loggedUser") User loggedUser) {

        Optional<Album> album = albumService.find(albumId);

        if(album.isEmpty()) {
            LOGGER.debug("Error in *GET* 'mod/add/album/{}/song'. No album with id {}", albumId, albumId);
            return new ModelAndView("redirect:/?error=" + URLEncoder.encode("The album doesn't exist.", StandardCharsets.UTF_8));
        }

        ModelAndView modelAndView = new ModelAndView("moderator/add-song").addObject(albumId);
        modelAndView.addObject("postUrl", "/mod/add/album/" + albumId + "/song");
        return modelAndView;
    }

    @RequestMapping(path = "add/album/{albumId:\\d+}/song", method = RequestMethod.POST)
    public ModelAndView submitSongForm(@PathVariable(name = "albumId") final long albumId,
                                       @Valid @ModelAttribute("modSongForm") final ModSongForm modSongForm,
                                       final BindingResult errors,
                                       @ModelAttribute("loggedUser") User loggedUser) {
        if (errors.hasErrors())
            return addSongForm(albumId, modSongForm, loggedUser);

        Song song = songService.create(transformSongToDTO(modSongForm), new Album(albumId));
        LOGGER.info("New song created with ID: {} for album with ID: {}", song.getId(), albumId);

        ModelAndView modelAndView = new ModelAndView("redirect:/song/" + song.getId());
        modelAndView.addObject("song", song);
        return modelAndView;
    }

    @RequestMapping(path = "/edit/song/{songId:\\d+}", method = RequestMethod.GET)
    public ModelAndView editSongForm(@PathVariable(name = "songId") final long songId,
                                    @ModelAttribute("modSongForm") final ModSongForm modSongForm,
                                    @ModelAttribute("loggedUser") User loggedUser) {
        ModelAndView modelAndView = new ModelAndView("moderator/add-song");
        modelAndView.addObject("postUrl", "/mod/edit/song/" + songId);

        Optional<Song> song = songService.find(songId);
        if (song.isEmpty()) {
            LOGGER.debug("Error in *GET* '/mod/edit/song' no song with id {}", songId);
            String errorMessage = messageSource.getMessage("error.song.find", null, LocaleContextHolder.getLocale());
            return new ModelAndView("redirect:/?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        }

        modSongForm.setId(songId);
        modSongForm.setTitle(song.get().getTitle());
        modSongForm.setDuration(song.get().getDuration());
        modSongForm.setTrackNumber(song.get().getTrackNumber());
        modSongForm.setAlbumId(song.get().getAlbum().getId());

        return modelAndView;
    }

    @RequestMapping(path = "/edit/song/{songId:\\d+}", method = RequestMethod.POST)
    public ModelAndView submitEditSongForm(@PathVariable(name = "songId") final long songId,
                                           @Valid @ModelAttribute("modSongForm") final ModSongForm modSongForm,
                                           final BindingResult errors,
                                           @ModelAttribute("loggedUser") User loggedUser) {
        if (errors.hasErrors())
            return editSongForm(songId, modSongForm, loggedUser);

        Song newSong = songService.update(transformSongToDTO(modSongForm), new Album(modSongForm.getAlbumId()));
        LOGGER.info("Song with ID {} has been updated", songId);

        ModelAndView modelAndView = new ModelAndView("redirect:/song/" + songId);
        modelAndView.addObject("song", newSong);
        return modelAndView;
    }

    @RequestMapping(path = "/delete/artist/{artistId:\\d+}")
    public ModelAndView deleteArtist(@PathVariable(name = "artistId") final long artistId) {
        artistService.delete(artistId);
        LOGGER.info("Artist with ID {} has been deleted", artistId);
        return new ModelAndView("redirect:/home");
    }

    @RequestMapping(path = "delete/album/{albumId:\\d+}")
    public ModelAndView deleteAlbum(@PathVariable(name = "albumId") final long albumId) {
        Optional<Album> albumOptional = albumService.find(albumId);
        if (albumOptional.isEmpty()) {
            String errorMessage = messageSource.getMessage("error.album.find", null, LocaleContextHolder.getLocale());
            return new ModelAndView("redirect:/?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        }

        albumService.delete(albumOptional.get());
        LOGGER.info("Album with ID {} has been deleted", albumId);
        return new ModelAndView("redirect:/artist/" + albumOptional.get().getArtist().getId());
    }

    @RequestMapping(path = "delete/song/{songId:\\d+}")
    public ModelAndView deleteSong(@PathVariable(name = "songId") final long songId) {
        Optional<Song> songOptional = songService.find(songId);
        if (songOptional.isEmpty() ) {
            String errorMessage = messageSource.getMessage("error.song.find", null, LocaleContextHolder.getLocale());
            return new ModelAndView("redirect:/?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        }
        songService.delete(songId);
        LOGGER.info("Song with ID {} has been deleted", songId);
        return new ModelAndView("redirect:/artist/" + songOptional.get().getAlbum().getId());
    }

    @RequestMapping(path = "/update-ratings", method = RequestMethod.GET)
    public ModelAndView updateAvgRatingForAll(@ModelAttribute("loggedUser") User loggedUser) {
        reviewService.updateAvgRatingForAll();
        LOGGER.info("Average ratings have been updated for all items");
        return new ModelAndView("redirect:/");
    }

    private ArtistDTO transformArtistToDTO(ModArtistForm modArtistForm) {
        if (modArtistForm == null) { return null; }
        return new ArtistDTO(
                modArtistForm.getId(),
                modArtistForm.getName(),
                modArtistForm.getBio(),
                modArtistForm.getArtistImgId(),
                getBytes(modArtistForm.getArtistImage()),
                ( modArtistForm.getAlbums() != null ?
                        modArtistForm.getAlbums().stream().map(this::transformAlbumToDTO).collect(Collectors.toList())
                        : null),
                modArtistForm.isDeleted()
        );
    }

    private AlbumDTO transformAlbumToDTO(ModAlbumForm modAlbumForm) {
        if ( modAlbumForm == null ) { return null; }
        return new AlbumDTO(
                modAlbumForm.getId(),
                modAlbumForm.getTitle(),
                modAlbumForm.getGenre(),
                modAlbumForm.getReleaseDate(),
                modAlbumForm.getAlbumImageId(),
                getBytes(modAlbumForm.getAlbumImage()),
                ( modAlbumForm.getSongs() != null ?
                        modAlbumForm.getSongs().stream().map(this::transformSongToDTO).collect(Collectors.toList())
                        : null),
                modAlbumForm.isDeleted()
        );
    }

    private SongDTO transformSongToDTO(ModSongForm modSongForm) {
        if ( modSongForm == null ) { return null; }
        return new SongDTO(
                modSongForm.getId(),
                modSongForm.getTitle(),
                modSongForm.getDuration(),
                modSongForm.getTrackNumber(),
                modSongForm.isDeleted()
        );
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
