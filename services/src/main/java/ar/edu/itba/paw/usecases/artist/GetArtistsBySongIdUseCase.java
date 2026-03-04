package ar.edu.itba.paw.usecases.artist;

import ar.edu.itba.paw.domain.artist.Artist;
import ar.edu.itba.paw.domain.artist.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetArtistsBySongIdUseCase implements GetArtistsBySongId {

    private final ArtistRepository artistRepository;

    @Autowired
    public GetArtistsBySongIdUseCase(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public List<Artist> execute(Long songId) {
        return artistRepository.findBySongId(songId);
    }
}
