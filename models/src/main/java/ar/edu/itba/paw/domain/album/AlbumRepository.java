package ar.edu.itba.paw.domain.album;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository {

    Optional<Album> findById(AlbumId id);

    Album save(Album album);

    void delete(AlbumId id);

    List<Album> findAll(Integer page, Integer size);

    List<Album> findByArtistId(Long artistId);

    List<Album> findByTitleContaining(String substring, Integer page, Integer size);

    Long countAll();

    boolean hasUserReviewed(Long userId, AlbumId albumId);

    boolean deleteReviewsFromAlbum(AlbumId albumId);
}
