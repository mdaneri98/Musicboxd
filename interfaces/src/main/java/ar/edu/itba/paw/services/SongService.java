package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.reviews.Review;

import java.util.List;

public interface SongService extends CrudService<Song> {

    List<Song> findByArtistId(Long id, FilterType filterType, Integer pageNum, Integer pageSize);

    List<Song> findByAlbumId(Long id);

    List<Song> findByTitleContaining(String sub, Integer page, Integer size);

    List<Review> findReviewsBySongId(Long songId);

    Boolean createAll(List<Song> songs, Album album);

    Boolean updateAll(List<Song> songs, Album album);

    Boolean hasUserReviewed(Long userId, Long songId);

    Boolean updateRating(Long songId);

    Long countAll();


}
