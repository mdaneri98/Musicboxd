package ar.edu.itba.paw.usecases.album;

import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.domain.album.AlbumId;
import ar.edu.itba.paw.domain.album.AlbumRepository;
import ar.edu.itba.paw.exception.not_found.AlbumNotFoundException;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.persistence.AlbumDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UpdateAlbumRatingUseCase implements UpdateAlbumRating {

    private final AlbumRepository albumRepository;
    private final AlbumDao albumDao;

    @Autowired
    public UpdateAlbumRatingUseCase(AlbumRepository albumRepository, AlbumDao albumDao) {
        this.albumRepository = albumRepository;
        this.albumDao = albumDao;
    }

    @Override
    public void execute(Long albumId) {
        AlbumId id = new AlbumId(albumId);

        Album album = albumRepository.findById(id)
            .orElseThrow(() -> new AlbumNotFoundException(albumId));

        List<AlbumReview> reviews = albumDao.findReviewsByAlbumId(albumId);

        double avgRating = reviews.stream()
            .mapToInt(AlbumReview::getRating)
            .average()
            .orElse(0.0);

        double roundedAvgRating = Math.round(avgRating * 100.0) / 100.0;
        int ratingCount = reviews.size();

        album.updateRating(roundedAvgRating, ratingCount);
        albumRepository.save(album);
    }
}
