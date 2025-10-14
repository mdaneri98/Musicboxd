package ar.edu.itba.paw.services.mappers;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AlbumMapper {

    @Autowired
    private SongMapper songMapper;


    public AlbumDTO toDTO(Album album) {
        if (album == null) {
            return null;
        }

        AlbumDTO dto = new AlbumDTO();
        dto.setId(album.getId());
        dto.setTitle(album.getTitle());
        dto.setGenre(album.getGenre());
        dto.setReleaseDate(album.getReleaseDate());
        dto.setImageId(album.getImage() != null ? album.getImage().getId() : null);
        dto.setArtistId(album.getArtist() != null ? album.getArtist().getId() : null);
        dto.setArtistName(album.getArtist() != null ? album.getArtist().getName() : null);
        dto.setRatingCount(album.getRatingCount());
        dto.setAvgRating(album.getAvgRating());
        dto.setCreatedAt(album.getCreatedAt());
        dto.setUpdatedAt(album.getUpdatedAt());

        return dto;
    }

    public List<AlbumDTO> toDTOList(List<Album> albums) {
        if (albums == null) {
            return null;
        }

        return albums.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Album toEntity(AlbumDTO dto) {
        if (dto == null) {
            return null;
        }

        Album album = new Album();
        album.setId(dto.getId());
        album.setTitle(dto.getTitle());
        album.setGenre(dto.getGenre());
        album.setReleaseDate(dto.getReleaseDate());
        album.setRatingCount(dto.getRatingCount());
        album.setAvgRating(dto.getAvgRating());
        album.setCreatedAt(dto.getCreatedAt());
        album.setUpdatedAt(dto.getUpdatedAt());

        return album;
    }

    public List<Album> toEntityList(List<AlbumDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

}

