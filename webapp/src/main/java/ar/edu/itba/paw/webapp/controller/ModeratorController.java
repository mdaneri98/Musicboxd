package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.Song;

import ar.edu.itba.paw.services.AlbumService;
import ar.edu.itba.paw.services.ArtistService;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.services.SongService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@RequestMapping("/mod")
@Controller
public class ModeratorController {

    private final ImageService imageService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final SongService songService;

    public ModeratorController(ImageService imageService, ArtistService artistService, AlbumService albumService, SongService songService) {
        this.imageService = imageService;
        this.artistService = artistService;
        this.albumService = albumService;
        this.songService = songService;
    }

    @RequestMapping(path = "add/artist", method = RequestMethod.GET)
    public ModelAndView addArtistForm() {
        return new ModelAndView("moderator/add-artist");
    }

    @RequestMapping(path = "add/artist", method = RequestMethod.POST)
    public ModelAndView submitArtistForm(@ModelAttribute("artist") Artist artist,
                                         @RequestParam("file") MultipartFile file) {
        try {
            Image image = imageService.save(file.getBytes());
            artist.setImgId(image.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        long artistId = artistService.save(artist);
        ModelAndView modelAndView = new ModelAndView("redirect:/artist/" + artistId);
        modelAndView.addObject("artist", artist);
        return modelAndView;
    }

    @RequestMapping(path = "add/artist/{artistId:\\d+}/album", method = RequestMethod.GET)
    public ModelAndView addAlbumForm(@PathVariable(name = "artistId") long artistId) {
        return new ModelAndView("moderator/add-album").addObject(artistId);
    }

    @RequestMapping(path = "add/artist/{artistId:\\d+}/album", method = RequestMethod.POST)
    public ModelAndView submitAlbumForm(@PathVariable(name = "artistId") long artistId,
                                        @ModelAttribute("album") Album album,
                                        @RequestParam("file") MultipartFile file) {
        try {
            Image image = imageService.save(file.getBytes());
            album.setImgId(image.getId());
            albumService.save(album);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ModelAndView modelAndView = new ModelAndView("redirect:/artist/" + artistId);
        modelAndView.addObject("artist", artistService.findById(artistId).get());
        return modelAndView;
    }

    @RequestMapping(path = "/add/album/{albumId:\\d+}/song", method = RequestMethod.GET)
    public ModelAndView addSongForm(@PathVariable(name = "albumId") long albumId) {
        return new ModelAndView("moderator/add-song").addObject(albumId);
    }

    @RequestMapping(path = "add/album/{albumId:\\d+}/song", method = RequestMethod.POST)
    public ModelAndView submitSongForm(@PathVariable(name = "albumId") long albumId,
                                       @ModelAttribute("song") Song song,
                                       @RequestParam("file") MultipartFile file) {

        songService.save(song);

        ModelAndView modelAndView = new ModelAndView("redirect:/album/" + albumId);
        modelAndView.addObject("album", albumService.findById(albumId).get());
        return modelAndView;
    }
}
