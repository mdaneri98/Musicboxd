package ar.edu.itba.paw.usecases.artist;

import ar.edu.itba.paw.domain.artist.Artist;
import ar.edu.itba.paw.domain.artist.ArtistId;
import ar.edu.itba.paw.domain.artist.ArtistRepository;
import ar.edu.itba.paw.exception.not_found.ArtistNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetArtistUseCase implements GetArtist {

    private final ArtistRepository artistRepository;

    @Autowired
    public GetArtistUseCase(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public Artist execute(Long artistId) {
        return artistRepository.findById(new ArtistId(artistId))
            .orElseThrow(() -> new ArtistNotFoundException(artistId));
    }
}
