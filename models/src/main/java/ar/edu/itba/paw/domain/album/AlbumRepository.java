package ar.edu.itba.paw.domain.album;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface AlbumRepository {

    Optional<Album> findById(AlbumId id);

    Map<Long, Album> findByIds(Set<Long> ids);

    Album save(Album album);

    void delete(AlbumId id);

    List<Album> findAll(Integer page, Integer size);

    List<Album> findByArtistId(Long artistId);

    List<Album> findByTitleContaining(String substring, Integer page, Integer size);

    Long countAll();

    boolean hasUserReviewed(Long userId, AlbumId albumId);

    boolean deleteReviewsFromAlbum(AlbumId albumId);
}
