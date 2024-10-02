package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.persistence.AlbumDao;
import ar.edu.itba.paw.persistence.ArtistDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumServiceImpl implements AlbumService {
    /*
        FIXME: Add required `business logic`
     */
    private final AlbumDao albumDao;
    private final ImageService imageService;

    public AlbumServiceImpl(AlbumDao albumDao, ImageService imageService) {
        this.albumDao = albumDao;
        this.imageService = imageService;
    }

    @Override
    public Optional<Album> findById(long id) {
        return albumDao.findById(id);
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
    public long save(Album album, MultipartFile imageFile) {
        album.setImgId(imageService.save(imageFile, false));
        return albumDao.save(album);
    }

    @Override
    public int update(Album album) {
        return albumDao.update(album);
    }

    @Override
    public int update(Album album, Album updatedAlbum, MultipartFile imageFile) {
        long imgId = imageService.update(album.getImgId(), imageFile);
        updatedAlbum.setImgId(imgId);
        if(updatedAlbum.getId() == null) {updatedAlbum.setId(imgId);}
        if( !album.equals(updatedAlbum) ) {
            album.setTitle(updatedAlbum.getTitle());
            album.setGenre(updatedAlbum.getGenre());
            album.setReleaseDate(updatedAlbum.getReleaseDate());
            album.setImgId(imgId);
            return albumDao.update(album);
        }
        return 0;
    }

    @Override
    public int delete(Album album) {
        imageService.delete(album.getImgId());
        return albumDao.deleteById(album.getId());
    }
}
