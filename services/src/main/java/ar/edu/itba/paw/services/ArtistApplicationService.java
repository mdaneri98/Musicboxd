package ar.edu.itba.paw.services;

import ar.edu.itba.paw.domain.artist.Artist;
import ar.edu.itba.paw.usecases.artist.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtistApplicationService {

    private final GetArtist getArtist;
    private final GetAllArtists getAllArtists;
    private final CreateArtist createArtist;
    private final UpdateArtist updateArtist;
    private final DeleteArtist deleteArtist;
    private final SearchArtistsByName searchArtistsByName;
    private final GetArtistsBySongId getArtistsBySongId;
    private final UpdateArtistRating updateArtistRating;

    @Autowired
    public ArtistApplicationService(GetArtist getArtist,
                                     GetAllArtists getAllArtists,
                                     CreateArtist createArtist,
                                     UpdateArtist updateArtist,
                                     DeleteArtist deleteArtist,
                                     SearchArtistsByName searchArtistsByName,
                                     GetArtistsBySongId getArtistsBySongId,
                                     UpdateArtistRating updateArtistRating) {
        this.getArtist = getArtist;
        this.getAllArtists = getAllArtists;
        this.createArtist = createArtist;
        this.updateArtist = updateArtist;
        this.deleteArtist = deleteArtist;
        this.searchArtistsByName = searchArtistsByName;
        this.getArtistsBySongId = getArtistsBySongId;
        this.updateArtistRating = updateArtistRating;
    }

    public Artist getById(Long artistId) {
        return getArtist.execute(artistId);
    }

    public List<Artist> getAll(Integer page, Integer size) {
        return getAllArtists.execute(page, size);
    }

    public Long count() {
        return getAllArtists.count();
    }

    public Artist create(CreateArtistCommand command) {
        return createArtist.execute(command);
    }

    public Artist update(UpdateArtistCommand command) {
        return updateArtist.execute(command);
    }

    public void delete(DeleteArtistCommand command) {
        deleteArtist.execute(command);
    }

    public List<Artist> searchByName(String nameSubstring, Integer page, Integer size) {
        return searchArtistsByName.execute(nameSubstring, page, size);
    }

    public List<Artist> getBySongId(Long songId) {
        return getArtistsBySongId.execute(songId);
    }

    public void updateRating(Long artistId) {
        updateArtistRating.execute(artistId);
    }
}
