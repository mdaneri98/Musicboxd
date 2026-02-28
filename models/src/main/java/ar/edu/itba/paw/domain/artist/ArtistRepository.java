package ar.edu.itba.paw.domain.artist;

import java.util.List;
import java.util.Optional;

public interface ArtistRepository {

    Optional<Artist> findById(ArtistId id);

    Artist save(Artist artist);

    void delete(ArtistId id);

    List<Artist> findAll(Integer page, Integer size);

    List<Artist> findBySongId(Long songId);

    List<Artist> findByNameContaining(String substring, Integer page, Integer size);

    Long countAll();

    boolean hasUserReviewed(Long userId, ArtistId artistId);

    boolean deleteReviewsFromArtist(ArtistId artistId);
}
