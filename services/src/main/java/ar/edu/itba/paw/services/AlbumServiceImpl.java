package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
import ar.edu.itba.paw.persistence.AlbumDao;
import ar.edu.itba.paw.persistence.ArtistDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.logging.Filter;

@Service
public class AlbumServiceImpl implements AlbumService {
    /*
        FIXME: Add required `business logic`
     */
    private final AlbumDao albumDao;
    private final ImageService imageService;
    private final SongService songService;

    public AlbumServiceImpl(AlbumDao albumDao, ImageService imageService, SongService songService) {
        this.albumDao = albumDao;
        this.imageService = imageService;
        this.songService = songService;
    }

    @Override
    public Optional<Album> findById(long id) {
        return albumDao.findById(id);
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
    public long save(Album album) {
        album.setImgId(imageService.save((byte[]) null, false));
        return albumDao.save(album);
    }

    @Override
    public int update(Album album) {
        return albumDao.update(album);
    }

    @Override
    public int deleteById(long id) {
        Optional<Album> album = albumDao.findById(id);
        if (album.isEmpty()) {
            return 0;
        }

        imageService.delete(album.get().getImgId());
        return albumDao.deleteById(id);
    }

    @Override
    public int delete(Album album) {
        imageService.delete(album.getImgId());
        return albumDao.deleteById(album.getId());
    }


    //************************************************************************** Testing
    @Override
    public Album save(AlbumDTO albumDTO, Artist artist) {
        long imgId = imageService.save(albumDTO.getImage(), false);
        Album album = new Album(albumDTO.getId(), albumDTO.getTitle(), imgId, albumDTO.getGenre(), artist, albumDTO.getReleaseDate());

        album = albumDao.saveX(album);

        if (albumDTO.getSongs() != null) {
            songService.save(albumDTO.getSongs(), album);
        }
        return album;
    }

    @Override
    public boolean save(List<AlbumDTO> albumsDTO, Artist artist) {
        for (AlbumDTO albumDTO : albumsDTO) {
            if (!albumDTO.isDeleted()) {
                save(albumDTO, artist);
            }
        }
        return true;
    }

    @Override
    public Album update(AlbumDTO albumDTO, Artist artist) {
        long imgId = imageService.update(albumDTO.getImgId(), albumDTO.getImage());
        Album album = new Album(albumDTO.getId(), albumDTO.getTitle(), imgId, albumDTO.getGenre(), artist, albumDTO.getReleaseDate());

        album = albumDao.updateX(album);

        if (albumDTO.getSongs() != null) {
            songService.update(albumDTO.getSongs(), album);
        }
        return album;
    }

    @Override
    public boolean update(List<AlbumDTO> albumsDTO, Artist artist) {
        for (AlbumDTO albumDTO : albumsDTO) {
            if (albumDTO.getId() != 0) {
                if (albumDTO.isDeleted()) {
                    delete(new Album(albumDTO.getId(), albumDTO.getTitle(), albumDTO.getImgId(), albumDTO.getGenre(), artist, albumDTO.getReleaseDate()));
                } else {
                    update(albumDTO, artist);
                }
            } else {
                if (!albumDTO.isDeleted()) {
                    save(albumDTO, artist);
                }
            }
        }
        return true;
    }

}
