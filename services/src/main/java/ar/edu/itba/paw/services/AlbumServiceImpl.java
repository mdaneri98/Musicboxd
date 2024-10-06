package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
import ar.edu.itba.paw.persistence.AlbumDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumServiceImpl implements AlbumService {
    private final AlbumDao albumDao;
    private final ImageService imageService;
    private final SongService songService;

    public AlbumServiceImpl(AlbumDao albumDao, ImageService imageService, SongService songService) {
        this.albumDao = albumDao;
        this.imageService = imageService;
        this.songService = songService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Album> find(long id) {
        return albumDao.find(id);
    }

    @Transactional(readOnly = true)
    public List<Album> findPaginated(FilterType filterType, int page, int pageSize) {
        return albumDao.findPaginated(filterType, pageSize, (page - 1) * pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Album> findByArtistId(long id) {return albumDao.findByArtistId(id);}

    @Override
    @Transactional(readOnly = true)
    public List<Album> findAll() {
        return albumDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Album> findByTitleContaining(String sub) {
        return albumDao.findByTitleContaining(sub);
    }

    @Override
    @Transactional
    public Album create(Album album) {
        album.setImgId(imageService.save((byte[]) null, false));
        return albumDao.create(album);
    }

    @Override
    @Transactional
    public Album update(Album album) {
        return albumDao.update(album);
    }

    @Transactional
    public boolean delete(long id) {
        Optional<Album> album = albumDao.find(id);
        if (album.isEmpty()) {
            return false;
        }
        imageService.delete(album.get().getImgId());
        return albumDao.delete(id);
    }

    @Override
    @Transactional
    public boolean delete(Album album) {
        if (album.getId() == null || album.getImgId() == null)
            return false;
        imageService.delete(album.getImgId());
        return albumDao.delete(album.getId());
    }

    @Override
    @Transactional
    public Album create(AlbumDTO albumDTO, long artistId) {
        long imgId = imageService.save(albumDTO.getImage(), false);
        Album album = new Album(0L, albumDTO.getTitle(), imgId, albumDTO.getGenre(), new Artist(artistId), albumDTO.getReleaseDate());

        album = albumDao.create(album);

        if (albumDTO.getSongs() != null) {
            songService.createAll(albumDTO.getSongs(), album);
        }
        return album;
    }

    @Override
    @Transactional
    public boolean createAll(List<AlbumDTO> albumsDTO, long artistId) {
        for (AlbumDTO albumDTO : albumsDTO) {
            if (!albumDTO.isDeleted()) {
                create(albumDTO, artistId);
            }
        }
        return true;
    }

    @Override
    @Transactional
    public Album update(AlbumDTO albumDTO) {
        long imgId = imageService.update(albumDTO.getImgId(), albumDTO.getImage());

        Album album = albumDao.find(albumDTO.getId()).get();//new Album(albumDTO.getId(), albumDTO.getTitle(), imgId, albumDTO.getGenre(), artist, albumDTO.getReleaseDate());
        album.setTitle(albumDTO.getTitle());
        album.setImgId(imgId);
        album.setGenre(albumDTO.getGenre());

        album = albumDao.update(album);

        if (albumDTO.getSongs() != null) {
            songService.updateAll(albumDTO.getSongs(), album);
        }
        return album;
    }

    @Override
    @Transactional
    public boolean updateAll(List<AlbumDTO> albumsDTO, long artistId) {
        for (AlbumDTO albumDTO : albumsDTO) {
            if (albumDTO.getId() != 0) {
                if (albumDTO.isDeleted()) {
                    delete(new Album(albumDTO.getId(), albumDTO.getTitle(), albumDTO.getImgId(), albumDTO.getGenre(), new Artist(artistId), albumDTO.getReleaseDate()));
                } else {
                    update(albumDTO);
                }
            } else {
                if (!albumDTO.isDeleted()) {
                    create(albumDTO, artistId);
                }
            }
        }
        return true;
    }

    @Override
    @Transactional
    public boolean updateRating(long albumId, float newRating, int newRatingAmount) {
        return albumDao.updateRating(albumId, newRating, newRatingAmount);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReviewed(long userId, long albumId) {
        return albumDao.hasUserReviewed(userId, albumId);
    }

}
