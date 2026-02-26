package ar.edu.itba.paw.domain.album;

import ar.edu.itba.paw.domain.artist.ArtistId;

import java.util.List;
import java.util.Optional;

/**
 * Repository port interface for Album aggregate.
 * Defines the contract that the domain expects from the persistence layer.
 *
 * Part of the Hexagonal Architecture migration (Phase 4).
 */
public interface AlbumRepository {

    /**
     * Find an album by its ID.
     *
     * @param id Album identifier
     * @return Optional containing the album if found
     */
    Optional<Album> findById(AlbumId id);

    /**
     * Find all albums by an artist.
     *
     * @param artistId Artist identifier
     * @return List of albums by the artist
     */
    List<Album> findByArtist(ArtistId artistId);

    /**
     * Find albums by title containing a substring.
     *
     * @param substring Substring to search for in titles
     * @param page Page number (1-indexed)
     * @param size Page size
     * @return List of matching albums
     */
    List<Album> findByTitleContaining(String substring, Integer page, Integer size);

    /**
     * Find albums with pagination and filtering.
     *
     * @param page Page number (0-indexed offset)
     * @param size Page size
     * @return List of albums
     */
    List<Album> findPaginated(Integer page, Integer size);

    /**
     * Save an album (create or update).
     *
     * @param album Album to save
     * @return Saved album with ID assigned
     */
    Album save(Album album);

    /**
     * Delete an album.
     *
     * @param id Album identifier
     * @return true if deleted successfully
     */
    Boolean delete(AlbumId id);

    /**
     * Update the rating of an album.
     *
     * @param albumId Album identifier
     * @param avgRating New average rating
     * @param ratingCount New rating count
     * @return true if updated successfully
     */
    Boolean updateRating(AlbumId albumId, double avgRating, int ratingCount);

    /**
     * Check if a user has reviewed an album.
     *
     * @param userId User ID
     * @param albumId Album identifier
     * @return true if the user has reviewed the album
     */
    Boolean hasUserReviewed(Long userId, AlbumId albumId);

    /**
     * Count all albums.
     *
     * @return Total number of albums
     */
    Long countAll();
}
