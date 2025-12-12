package ar.edu.itba.paw.api.mapper.dto;

import ar.edu.itba.paw.api.dto.SongDTO;
import ar.edu.itba.paw.models.Song;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper to convert between Song model and SongDTO
 */
@Component
public class SongDtoMapper {

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
        dto.setReleaseDate(song.getAlbum() != null && song.getAlbum().getReleaseDate() != null ? song.getAlbum().getReleaseDate() : null);
        if (song.getArtists() != null && !song.getArtists().isEmpty()) {
            dto.setArtistId(song.getArtists().get(0).getId());
        }
        dto.setIsReviewed(song.getIsReviewed());
        dto.setIsFavorite(song.getIsFavorite());
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

    public Song toModel(SongDTO dto) {
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

    public List<Song> toModelList(List<SongDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }
}

