package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
import ar.edu.itba.paw.models.dtos.SongDTO;
import java.util.List;

public interface SongService extends CrudService<SongDTO> {

    List<SongDTO> findByArtistId(Long id, Integer pageNum, Integer pageSize);

    List<SongDTO> findByAlbumId(Long id);

    List<SongDTO> findByTitleContaining(String sub, Integer pageSize, Integer pageNum);

    List<ReviewDTO> findReviewsBySongId(Long songId);

    Boolean createAll(List<SongDTO> songsDTO, Album album);

    Boolean updateAll(List<SongDTO> songsDTO, Album album);

    Boolean hasUserReviewed(Long userId, Long songId);

    Boolean updateRating(Long songId);

    Long countAll();
}
