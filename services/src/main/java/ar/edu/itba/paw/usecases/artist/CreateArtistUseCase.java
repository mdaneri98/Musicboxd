package ar.edu.itba.paw.usecases.artist;

import ar.edu.itba.paw.domain.artist.Artist;
import ar.edu.itba.paw.domain.artist.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateArtistUseCase implements CreateArtist {

    private final ArtistRepository artistRepository;

    @Autowired
    public CreateArtistUseCase(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public Artist execute(CreateArtistCommand command) {
        Artist artist = Artist.create(command.name(), command.bio(), command.imageId());
        return artistRepository.save(artist);
    }
}
