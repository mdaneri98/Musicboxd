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
public class DeleteArtistUseCase implements DeleteArtist {

    private final ArtistRepository artistRepository;

    @Autowired
    public DeleteArtistUseCase(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public void execute(DeleteArtistCommand command) {
        ArtistId artistId = new ArtistId(command.artistId());

        Artist artist = artistRepository.findById(artistId)
            .orElseThrow(() -> new ArtistNotFoundException(command.artistId()));

        artistRepository.deleteReviewsFromArtist(artistId);
        artistRepository.delete(artistId);
    }
}
