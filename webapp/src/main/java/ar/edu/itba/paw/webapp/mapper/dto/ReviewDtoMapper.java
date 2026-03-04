package ar.edu.itba.paw.webapp.mapper.dto;

import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.domain.album.AlbumId;
import ar.edu.itba.paw.domain.album.AlbumRepository;
import ar.edu.itba.paw.domain.artist.Artist;
import ar.edu.itba.paw.domain.artist.ArtistId;
import ar.edu.itba.paw.domain.artist.ArtistRepository;
import ar.edu.itba.paw.domain.review.AlbumReview;
import ar.edu.itba.paw.domain.review.ArtistReview;
import ar.edu.itba.paw.domain.review.Review;
import ar.edu.itba.paw.domain.review.ReviewType;
import ar.edu.itba.paw.domain.review.SongReview;
import ar.edu.itba.paw.domain.song.Song;
import ar.edu.itba.paw.domain.song.SongId;
import ar.edu.itba.paw.domain.song.SongRepository;
import ar.edu.itba.paw.domain.user.User;
import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.user.UserRepository;
import ar.edu.itba.paw.webapp.dto.ReviewDTO;
import ar.edu.itba.paw.webapp.dto.links.ReviewLinksDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapper to convert between Review model and ReviewDTO
 */
@Component
public class ReviewDtoMapper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SongRepository songRepository;

    public ReviewDTO toDTO(Review review, UriInfo uriInfo) {
        if (review == null) {
            return null;
        }

        User user = null;
        if (review.getUserId() != null) {
            Optional<User> userOpt = userRepository.findById(review.getUserId());
            user = userOpt.orElse(null);
        }

        String itemName = null;
        Long itemImageId = null;
        ReviewType itemType = review.getType();

        if (review instanceof ArtistReview) {
            ArtistReview artistReview = (ArtistReview) review;
            Optional<Artist> artistOpt = artistRepository.findById(artistReview.getArtistId());
            if (artistOpt.isPresent()) {
                Artist artist = artistOpt.get();
                itemName = artist.getName();
                itemImageId = artist.getImageId();
            }
        } else if (review instanceof AlbumReview) {
            AlbumReview albumReview = (AlbumReview) review;
            Optional<Album> albumOpt = albumRepository.findById(albumReview.getAlbumId());
            if (albumOpt.isPresent()) {
                Album album = albumOpt.get();
                itemName = album.getTitle();
                itemImageId = album.getImageId();
            }
        } else if (review instanceof SongReview) {
            SongReview songReview = (SongReview) review;
            Optional<Song> songOpt = songRepository.findById(songReview.getSongId());
            if (songOpt.isPresent()) {
                Song song = songOpt.get();
                itemName = song.getTitle();
                if (song.getAlbumId() != null) {
                    Optional<Album> albumOpt = albumRepository.findById(new AlbumId(song.getAlbumId()));
                    if (albumOpt.isPresent()) {
                        itemImageId = albumOpt.get().getImageId();
                    }
                }
            }
        }

        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId() != null ? review.getId().getValue() : null);
        dto.setUserId(review.getUserId() != null ? review.getUserId().getValue() : null);
        dto.setUserImageId(user != null ? user.getImageId() : null);
        dto.setUsername(user != null ? user.getUsername() : null);
        dto.setTitle(review.getTitle());
        dto.setDescription(review.getDescription());
        dto.setRating(review.getRating() != null ? review.getRating().getValue() : null);
        dto.setCreatedAt(review.getCreatedAt());
        dto.setLikes(review.getLikes());
        dto.setIsBlocked(review.isBlocked());
        dto.setCommentAmount(review.getCommentAmount());
        dto.setItemType(itemType);
        dto.setItemId(review.getItemId());
        dto.setItemName(itemName);
        dto.setItemImageId(itemImageId);
        dto.setUserModerator(user != null ? user.isModerator() : null);
        dto.setUserVerified(user != null ? user.isVerified() : null);

        if (uriInfo != null) {
            ReviewLinksDTO links = new ReviewLinksDTO();

            links.setSelf(uriInfo.getBaseUriBuilder()
                    .path("reviews").path(String.valueOf(review.getId().getValue())).build());

            if (review.getUserId() != null) {
                links.setUser(uriInfo.getBaseUriBuilder()
                        .path("users").path(String.valueOf(review.getUserId().getValue())).build());
            }

            if (itemType != null && review.getItemId() != null) {
                String itemPath = getItemPath(itemType);
                links.setItem(uriInfo.getBaseUriBuilder()
                        .path(itemPath).path(String.valueOf(review.getItemId())).build());
            }

            links.setComments(uriInfo.getBaseUriBuilder()
                    .path("reviews").path(String.valueOf(review.getId().getValue())).path("comments").build());

            links.setLikes(uriInfo.getBaseUriBuilder()
                    .path("reviews").path(String.valueOf(review.getId().getValue())).path("likes").build());

            dto.setLinks(links);
        }

        return dto;
    }

    private String getItemPath(ReviewType itemType) {
        switch (itemType) {
            case ARTIST:
                return "artists";
            case ALBUM:
                return "albums";
            case SONG:
                return "songs";
            default:
                return "items";
        }
    }

    public List<ReviewDTO> toDTOList(List<Review> reviews, UriInfo uriInfo) {
        if (reviews == null) {
            return null;
        }

        return reviews.stream()
                .map(r -> toDTO(r, uriInfo))
                .collect(Collectors.toList());
    }

    public ReviewDTO toDTOLegacy(ar.edu.itba.paw.models.reviews.Review review, UriInfo uriInfo) {
        if (review == null) {
            return null;
        }

        User user = null;
        if (review.getUserId() != null) {
            Optional<User> userOpt = userRepository.findById(new UserId(review.getUserId()));
            user = userOpt.orElse(null);
        }

        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setUserId(review.getUserId());
        dto.setUserImageId(user != null ? user.getImageId() : null);
        dto.setUsername(user != null ? user.getUsername() : null);
        dto.setTitle(review.getTitle());
        dto.setDescription(review.getDescription());
        dto.setRating(review.getRating());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setLikes(review.getLikes());
        dto.setIsBlocked(review.isBlocked());
        dto.setCommentAmount(review.getCommentAmount() != null ? review.getCommentAmount() : 0);
        dto.setItemType(convertLegacyReviewType(review.getItemType()));
        dto.setItemId(review.getItemId());
        dto.setItemName(review.getItemName());
        dto.setItemImageId(review.getItemImage() != null ? review.getItemImage().getId() : null);
        dto.setUserModerator(user != null ? user.isModerator() : null);
        dto.setUserVerified(user != null ? user.isVerified() : null);

        if (uriInfo != null) {
            ReviewLinksDTO links = new ReviewLinksDTO();

            links.setSelf(uriInfo.getBaseUriBuilder()
                    .path("reviews").path(String.valueOf(review.getId())).build());

            if (review.getUserId() != null) {
                links.setUser(uriInfo.getBaseUriBuilder()
                        .path("users").path(String.valueOf(review.getUserId())).build());
            }

            if (review.getItemType() != null && review.getItemId() != null) {
                String itemPath = getItemPath(convertLegacyReviewType(review.getItemType()));
                links.setItem(uriInfo.getBaseUriBuilder()
                        .path(itemPath).path(String.valueOf(review.getItemId())).build());
            }

            links.setComments(uriInfo.getBaseUriBuilder()
                    .path("reviews").path(String.valueOf(review.getId())).path("comments").build());

            links.setLikes(uriInfo.getBaseUriBuilder()
                    .path("reviews").path(String.valueOf(review.getId())).path("likes").build());

            dto.setLinks(links);
        }

        return dto;
    }

    public List<ReviewDTO> toDTOListLegacy(List<ar.edu.itba.paw.models.reviews.Review> reviews, UriInfo uriInfo) {
        if (reviews == null) {
            return null;
        }

        return reviews.stream()
                .map(r -> toDTOLegacy(r, uriInfo))
                .collect(Collectors.toList());
    }

    private ReviewType convertLegacyReviewType(ar.edu.itba.paw.models.reviews.ReviewType legacyType) {
        if (legacyType == null) {
            return null;
        }
        return ReviewType.valueOf(legacyType.name());
    }
}
