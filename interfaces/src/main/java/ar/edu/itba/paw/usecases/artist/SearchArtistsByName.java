package ar.edu.itba.paw.usecases.artist;

import ar.edu.itba.paw.domain.artist.Artist;

import java.util.List;

public interface SearchArtistsByName {
    List<Artist> execute(String nameSubstring, Integer page, Integer size);
}
