package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
import ar.edu.itba.paw.models.dtos.SongDTO;
import java.util.List;

public interface SongService extends CrudService<SongDTO> {

    List<SongDTO> findByArtistId(Long id, Integer pageNum, Integer pageSize);
    List<SongDTO> findByAlbumId(Long id);
    List<SongDTO> findByTitleContaining(String sub);
    List<ReviewDTO> findReviewsBySongId(Long songId);
    Boolean createAll(List<SongDTO> songsDTO, Album album);
    SongDTO update(SongDTO songDTO);
    Boolean updateAll(List<SongDTO> songsDTO, Album album);
    Boolean updateRating(Long songId, Double newRating, Integer newRatingAmount);
    Boolean hasUserReviewed(Long userId, Long songId);
    Boolean deleteReviewsFromSong(Long id);
}
