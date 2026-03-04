package ar.edu.itba.paw.usecases.album;

import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.domain.album.AlbumId;
import ar.edu.itba.paw.domain.album.AlbumRepository;
import ar.edu.itba.paw.domain.rating.RatingCalculator;
import ar.edu.itba.paw.domain.review.AlbumReview;
import ar.edu.itba.paw.domain.review.ReviewRepository;
import ar.edu.itba.paw.exception.not_found.AlbumNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UpdateAlbumRatingUseCase implements UpdateAlbumRating {

    private final AlbumRepository albumRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public UpdateAlbumRatingUseCase(AlbumRepository albumRepository, ReviewRepository reviewRepository) {
        this.albumRepository = albumRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void execute(Long albumId) {
        AlbumId id = new AlbumId(albumId);

        Album album = albumRepository.findById(id)
            .orElseThrow(() -> new AlbumNotFoundException(albumId));

        List<AlbumReview> reviews = reviewRepository.findByAlbumId(id, null, null);

        List<Integer> ratingValues = reviews.stream()
            .map(review -> review.getRating().getValue())
            .collect(Collectors.toList());

        double avgRating = RatingCalculator.calculateAverageFromIntegers(ratingValues);
        int ratingCount = reviews.size();

        album.updateRating(avgRating, ratingCount);
        albumRepository.save(album);
    }
}
