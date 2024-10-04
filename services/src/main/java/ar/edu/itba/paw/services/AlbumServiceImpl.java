package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
import ar.edu.itba.paw.persistence.AlbumDao;
import org.springframework.stereotype.Service;
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
    public Optional<Album> find(long id) {
        return albumDao.find(id);
    }

    public List<Album> findPaginated(FilterType filterType, int page, int pageSize) {
        return albumDao.findPaginated(filterType, pageSize, (page - 1) * pageSize);
    }

    @Override
    public List<Album> findByArtistId(long id) {return albumDao.findByArtistId(id);}

    @Override
    public List<Album> findAll() {
        return albumDao.findAll();
    }

    @Override
    public List<Album> findByTitleContaining(String sub) {
        return albumDao.findByTitleContaining(sub);
    }

    @Override
    public Album create(Album album) {
        album.setImgId(imageService.save((byte[]) null, false));
        return albumDao.create(album);
    }

    @Override
    public Album update(Album album) {
        return albumDao.update(album);
    }

    public boolean delete(long id) {
        Optional<Album> album = albumDao.find(id);
        if (album.isEmpty()) {
            return false;
        }

        imageService.delete(album.get().getImgId());
        return albumDao.delete(id);
    }

    @Override
    public boolean delete(Album album) {
        if (album.getId() == null || album.getImgId() == null)
            return false;
        imageService.delete(album.getImgId());
        return albumDao.delete(album.getId());
    }

    @Override
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
    public boolean createAll(List<AlbumDTO> albumsDTO, long artistId) {
        for (AlbumDTO albumDTO : albumsDTO) {
            if (!albumDTO.isDeleted()) {
                create(albumDTO, artistId);
            }
        }
        return true;
    }

    @Override
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

}
