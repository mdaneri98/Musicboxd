package ar.edu.itba.paw.domain.artist;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ArtistRepository {

    Optional<Artist> findById(ArtistId id);

    Map<Long, Artist> findByIds(Set<Long> ids);

    Artist save(Artist artist);

    void delete(ArtistId id);

    List<Artist> findAll(Integer page, Integer size);

    List<Artist> findBySongId(Long songId);

    List<Artist> findByNameContaining(String substring, Integer page, Integer size);

    Long countAll();

    boolean hasUserReviewed(Long userId, ArtistId artistId);

    boolean deleteReviewsFromArtist(ArtistId artistId);
}
