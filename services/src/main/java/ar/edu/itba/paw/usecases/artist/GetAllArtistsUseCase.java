package ar.edu.itba.paw.usecases.artist;

import ar.edu.itba.paw.domain.artist.Artist;
import ar.edu.itba.paw.domain.artist.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetAllArtistsUseCase implements GetAllArtists {

    private final ArtistRepository artistRepository;

    @Autowired
    public GetAllArtistsUseCase(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public List<Artist> execute(Integer page, Integer size) {
        return artistRepository.findAll(page, size);
    }

    @Override
    public Long count() {
        return artistRepository.countAll();
    }
}
