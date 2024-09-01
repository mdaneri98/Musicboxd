package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.Album;
import ar.edu.itba.paw.Artist;
import ar.edu.itba.paw.Song;
import ar.edu.itba.paw.reviews.AlbumReview;
import ar.edu.itba.paw.services.AlbumService;
import ar.edu.itba.paw.services.ArtistService;
import ar.edu.itba.paw.services.SongService;
import ar.edu.itba.paw.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/*
    No es un servlet.
 */

@Controller
public class HelloWorldController {

    private final UserService userService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final SongService songService;

    public HelloWorldController(UserService userService, ArtistService artistService, AlbumService albumService, SongService songService) {
        this.userService = userService;
        this.artistService = artistService;
        this.albumService = albumService;
        this.songService = songService;
    }

    @RequestMapping("/")
    public ModelAndView index() {
        final ModelAndView mav = new ModelAndView("index");

        mav.addObject("artists", artistService.findAll());
        return mav;
    }

    @RequestMapping("/artist/{artistId:\\d+}")
    public ModelAndView artist(@PathVariable(name = "artistId") long artistId) {
        final ModelAndView mav = new ModelAndView("artist");

        Artist artist = artistService.findById(artistId).get();
        List<Album> albums = albumService.findByArtistId(artistId);
        List<Song> songs = songService.findByArtistId(artistId);

        mav.addObject("artist", artist);
        mav.addObject("albums", albums);
        mav.addObject("songs", songs);
        return mav;
    }

    @RequestMapping("/album/{albumId:\\d+}")
    public ModelAndView album(@PathVariable(name = "albumId") long albumId) {
        final ModelAndView mav = new ModelAndView("album");

        Album album = albumService.findById(albumId).get();
        Artist artist = artistService.findById(album.getArtistId()).get();
        List<Song> songs = songService.findByAlbumId(albumId);
        List<AlbumReview> reviews =

        mav.addObject("album", album);
        mav.addObject("artist", artist);
        mav.addObject("songs", songs);
        mav.addObject("reviews", songs);
        return mav;
    }

    @RequestMapping("/song/{songId:\\d+}")
    public ModelAndView song(@PathVariable(name = "songId") long songId) {
        final ModelAndView mav = new ModelAndView("song");

        Song song = songService.findById(songId).get();
        List<Artist> artists = artistService.findBySongId(songId);
        Album album = albumService.findById(song.getAlbumId()).get();

        mav.addObject("album", album);
        mav.addObject("artists", artists);
        mav.addObject("song", song);
        return mav;
    }

    @RequestMapping("/review/artist/{artistId:\\d+}")
    public ModelAndView reviewArtist(@PathVariable(name = "artistId") long artistId) {
        final ModelAndView mav = new ModelAndView("reviews/artist_review");
        Artist artist = artistService.findById(artistId).get();
        mav.addObject("artist", artist);
        return mav;
    }

    @RequestMapping("/review/album/{albumId:\\d+}")
    public ModelAndView reviewAlbum(@PathVariable(name = "albumId") long albumId) {
        final ModelAndView mav = new ModelAndView("reviews/album_review");
        Album album = albumService.findById(albumId).get();
        mav.addObject("album", album);
        return mav;
    }

    @RequestMapping("/review/song/{songId:\\d+}")
    public ModelAndView reviewSong(@PathVariable(name = "songId") long songId) {
        final ModelAndView mav = new ModelAndView("reviews/song_review");
        Song song = songService.findById(songId).get();
        mav.addObject("song", song);
        return mav;
    }
}
