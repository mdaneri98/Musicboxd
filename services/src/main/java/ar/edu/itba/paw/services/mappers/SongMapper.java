package ar.edu.itba.paw.services.mappers;

import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.dtos.SongDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SongMapper {

    public SongDTO toDTO(Song song) {
        if (song == null) {
            return null;
        }

        SongDTO dto = new SongDTO();
        dto.setId(song.getId());
        dto.setTitle(song.getTitle());
        dto.setDuration(song.getDuration());
        dto.setTrackNumber(song.getTrackNumber());
        dto.setAlbumId(song.getAlbum() != null ? song.getAlbum().getId() : null);
        dto.setAlbumTitle(song.getAlbum() != null ? song.getAlbum().getTitle() : null);
        dto.setAlbumImageId(song.getAlbum() != null && song.getAlbum().getImage() != null 
                ? song.getAlbum().getImage().getId() : null);
        dto.setRatingCount(song.getRatingCount());
        dto.setAvgRating(song.getAvgRating());
        dto.setCreatedAt(song.getCreatedAt());
        dto.setUpdatedAt(song.getUpdatedAt());
        dto.setArtistId(song.getArtists().getFirst().getId());

        return dto;
    }

    public List<SongDTO> toDTOList(List<Song> songs) {
        if (songs == null) {
            return null;
        }

        return songs.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Song toEntity(SongDTO dto) {
        if (dto == null) {
            return null;
        }

        Song song = new Song();
        song.setId(dto.getId());
        song.setTitle(dto.getTitle());
        song.setDuration(dto.getDuration());
        song.setTrackNumber(dto.getTrackNumber());
        song.setRatingCount(dto.getRatingCount());
        song.setAvgRating(dto.getAvgRating());
        song.setCreatedAt(dto.getCreatedAt());
        song.setUpdatedAt(dto.getUpdatedAt());
        return song;
    }

    public List<Song> toEntityList(List<SongDTO> dtos, Album album) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(dto -> toEntity(dto))
                .collect(Collectors.toList());
    }
}

