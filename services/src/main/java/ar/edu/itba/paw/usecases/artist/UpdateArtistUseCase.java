package ar.edu.itba.paw.usecases.artist;

import ar.edu.itba.paw.domain.artist.Artist;
import ar.edu.itba.paw.domain.artist.ArtistId;
import ar.edu.itba.paw.domain.artist.ArtistRepository;
import ar.edu.itba.paw.exception.not_found.ArtistNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateArtistUseCase implements UpdateArtist {

    private final ArtistRepository artistRepository;

    @Autowired
    public UpdateArtistUseCase(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public Artist execute(UpdateArtistCommand command) {
        ArtistId artistId = new ArtistId(command.artistId());

        Artist artist = artistRepository.findById(artistId)
            .orElseThrow(() -> new ArtistNotFoundException(command.artistId()));

        if (command.name() != null || command.bio() != null) {
            String newName = command.name() != null ? command.name() : artist.getName();
            String newBio = command.bio() != null ? command.bio() : artist.getBio();
            artist.updateInfo(newName, newBio);
        }

        if (command.imageId() != null) {
            artist.updateImage(command.imageId());
        }

        return artistRepository.save(artist);
    }
}
