package ar.edu.itba.paw.usecases.artist;

import ar.edu.itba.paw.domain.artist.Artist;

public interface CreateArtist {
    Artist execute(CreateArtistCommand command);
}
