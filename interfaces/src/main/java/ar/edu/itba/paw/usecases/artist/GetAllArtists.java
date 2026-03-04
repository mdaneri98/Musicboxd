package ar.edu.itba.paw.usecases.artist;

import ar.edu.itba.paw.domain.artist.Artist;

import java.util.List;

public interface GetAllArtists {
    List<Artist> execute(Integer page, Integer size);
    Long count();
}
