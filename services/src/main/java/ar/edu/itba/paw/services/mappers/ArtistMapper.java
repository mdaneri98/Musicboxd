package ar.edu.itba.paw.services.mappers;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArtistMapper {

    public ArtistDTO toDTO(Artist artist) {
        if (artist == null) {
            return null;
        }

        ArtistDTO dto = new ArtistDTO();
        dto.setId(artist.getId());
        dto.setName(artist.getName());
        dto.setBio(artist.getBio());
        dto.setImageId(artist.getImage() != null ? artist.getImage().getId() : null);
        dto.setRatingCount(artist.getRatingCount());
        dto.setAvgRating(artist.getAvgRating());
        dto.setCreatedAt(artist.getCreatedAt());
        dto.setUpdatedAt(artist.getUpdatedAt());

        return dto;
    }

    public List<ArtistDTO> toDTOList(List<Artist> artists) {
        if (artists == null) {
            return null;
        }

        return artists.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Artist toEntity(ArtistDTO dto) {
        if (dto == null) {
            return null;
        }

        Artist artist = new Artist();
        artist.setId(dto.getId());
        artist.setName(dto.getName());
        artist.setBio(dto.getBio());
        artist.setRatingCount(dto.getRatingCount());
        artist.setAvgRating(dto.getAvgRating());
        artist.setCreatedAt(dto.getCreatedAt());
        artist.setUpdatedAt(dto.getUpdatedAt());

        return artist;
    }

    public List<Artist> toEntityList(List<ArtistDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

}

