package ar.edu.itba.paw.usecases.artist;

import ar.edu.itba.paw.domain.artist.Artist;
import ar.edu.itba.paw.domain.artist.ArtistId;
import ar.edu.itba.paw.domain.artist.ArtistRepository;
import ar.edu.itba.paw.domain.rating.RatingCalculator;
import ar.edu.itba.paw.domain.review.ArtistReview;
import ar.edu.itba.paw.domain.review.ReviewRepository;
import ar.edu.itba.paw.exception.not_found.ArtistNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UpdateArtistRatingUseCase implements UpdateArtistRating {

    private final ArtistRepository artistRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public UpdateArtistRatingUseCase(ArtistRepository artistRepository, ReviewRepository reviewRepository) {
        this.artistRepository = artistRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void execute(Long artistId) {
        ArtistId id = new ArtistId(artistId);

        Artist artist = artistRepository.findById(id)
            .orElseThrow(() -> new ArtistNotFoundException(artistId));

        List<ArtistReview> reviews = reviewRepository.findByArtistId(id, null, null);

        List<Integer> ratingValues = reviews.stream()
            .map(review -> review.getRating().getValue())
            .collect(Collectors.toList());

        double avgRating = RatingCalculator.calculateAverageFromIntegers(ratingValues);
        int ratingCount = reviews.size();

        artist.updateRating(avgRating, ratingCount);
        artistRepository.save(artist);
    }
}
