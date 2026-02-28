package ar.edu.itba.paw.usecases.artist;

import ar.edu.itba.paw.domain.artist.Artist;
import ar.edu.itba.paw.domain.artist.ArtistId;
import ar.edu.itba.paw.domain.artist.ArtistRepository;
import ar.edu.itba.paw.exception.not_found.ArtistNotFoundException;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.persistence.ArtistDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UpdateArtistRatingUseCase implements UpdateArtistRating {

    private final ArtistRepository artistRepository;
    private final ArtistDao artistDao;

    @Autowired
    public UpdateArtistRatingUseCase(ArtistRepository artistRepository, ArtistDao artistDao) {
        this.artistRepository = artistRepository;
        this.artistDao = artistDao;
    }

    @Override
    public void execute(Long artistId) {
        ArtistId id = new ArtistId(artistId);

        Artist artist = artistRepository.findById(id)
            .orElseThrow(() -> new ArtistNotFoundException(artistId));

        List<ArtistReview> reviews = artistDao.findReviewsByArtistId(artistId);

        double avgRating = reviews.stream()
            .mapToInt(ArtistReview::getRating)
            .average()
            .orElse(0.0);

        double roundedAvgRating = Math.round(avgRating * 100.0) / 100.0;
        int ratingCount = reviews.size();

        artist.updateRating(roundedAvgRating, ratingCount);
        artistRepository.save(artist);
    }
}
