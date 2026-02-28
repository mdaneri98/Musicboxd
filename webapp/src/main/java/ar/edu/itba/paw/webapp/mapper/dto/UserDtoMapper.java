package ar.edu.itba.paw.webapp.mapper.dto;

import ar.edu.itba.paw.domain.user.User;
import ar.edu.itba.paw.infrastructure.jpa.UserJpaEntity;
import ar.edu.itba.paw.webapp.dto.UserDTO;
import ar.edu.itba.paw.webapp.dto.links.UserLinksDTO;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

/**
 * Mapper to convert between User model and UserDTO
 */
@Component
public class UserDtoMapper {

    public UserDTO toDTO(User user, UriInfo uriInfo) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId().getValue());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setBio(user.getBio());
        dto.setImageId(user.getImageId());
        dto.setFollowersAmount(user.getFollowersAmount());
        dto.setFollowingAmount(user.getFollowingAmount());
        dto.setReviewAmount(user.getReviewAmount());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setVerified(user.isVerified());
        dto.setModerator(user.isModerator());
        dto.setPreferredLanguage(user.getPreferredLanguage());
        dto.setPreferredTheme(user.getTheme());
        dto.setHasFollowNotificationsEnabled(user.getFollowNotificationsEnabled());
        dto.setHasLikeNotificationsEnabled(user.getLikeNotificationsEnabled());
        dto.setHasCommentsNotificationsEnabled(user.getCommentNotificationsEnabled());
        dto.setHasReviewsNotificationsEnabled(user.getReviewNotificationsEnabled());

        if (uriInfo != null) {
            UserLinksDTO links = new UserLinksDTO();

            links.setSelf(uriInfo.getBaseUriBuilder()
                    .path("users").path(String.valueOf(user.getId().getValue())).build());

            if (user.getImageId() != null) {
                links.setImage(uriInfo.getBaseUriBuilder()
                        .path("images").path(String.valueOf(user.getImageId())).build());
            }

            links.setReviews(uriInfo.getBaseUriBuilder()
                    .path("users").path(String.valueOf(user.getId().getValue())).path("reviews").build());

            links.setFollowers(uriInfo.getBaseUriBuilder()
                    .path("users").path(String.valueOf(user.getId().getValue())).path("followers").build());

            links.setFollowing(uriInfo.getBaseUriBuilder()
                    .path("users").path(String.valueOf(user.getId().getValue())).path("followings").build());

            links.setFavoriteArtists(uriInfo.getBaseUriBuilder()
                    .path("users").path(String.valueOf(user.getId().getValue())).path("favorites").path("artists").build());

            links.setFavoriteAlbums(uriInfo.getBaseUriBuilder()
                    .path("users").path(String.valueOf(user.getId().getValue())).path("favorites").path("albums").build());

            links.setFavoriteSongs(uriInfo.getBaseUriBuilder()
                    .path("users").path(String.valueOf(user.getId().getValue())).path("favorites").path("songs").build());

            dto.setLinks(links);
        }

        return dto;
    }

    public List<UserDTO> toDTOList(List<User> users, UriInfo uriInfo) {
        if (users == null) {
            return null;
        }

        return users.stream()
                .map(u -> toDTO(u, uriInfo))
                .collect(Collectors.toList());
    }

    public UserJpaEntity toModel(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        UserJpaEntity user = new UserJpaEntity();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getUsername() + "@temp.com"); // Email not in DTO
        user.setPassword(""); // Password not in DTO
        user.setName(dto.getName());
        user.setBio(dto.getBio());
        user.setImageId(null); // Handle separately
        user.setFollowerAmount(dto.getFollowersAmount());
        user.setFollowingAmount(dto.getFollowingAmount());
        user.setReviewAmount(dto.getReviewAmount());
        user.setCreatedAt(dto.getCreatedAt());
        user.setUpdatedAt(dto.getUpdatedAt());
        user.setVerified(dto.getVerified() != null ? dto.getVerified() : false);
        user.setIsModerator(dto.getModerator());
        user.setPreferredLanguage(dto.getPreferredLanguage());
        user.setTheme(dto.getPreferredTheme());
        user.setFollowNotificationsEnabled(dto.getHasFollowNotificationsEnabled());
        user.setLikeNotificationsEnabled(dto.getHasLikeNotificationsEnabled());
        user.setCommentNotificationsEnabled(dto.getHasCommentsNotificationsEnabled());
        user.setReviewNotificationsEnabled(dto.getHasReviewsNotificationsEnabled());

        return user;
    }

    public List<UserJpaEntity> toModelList(List<UserDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public static void mergeConfigToModel(UserJpaEntity user, UserDTO dto) {
        if (dto == null) {
            return;
        }

        setFieldIfNotNull(user::setPreferredLanguage, dto.getPreferredLanguage());
        setFieldIfNotNull(user::setTheme, dto.getPreferredTheme());
        setFieldIfNotNull(user::setFollowNotificationsEnabled, dto.getHasFollowNotificationsEnabled());
        setFieldIfNotNull(user::setLikeNotificationsEnabled, dto.getHasLikeNotificationsEnabled());
        setFieldIfNotNull(user::setCommentNotificationsEnabled, dto.getHasCommentsNotificationsEnabled());
        setFieldIfNotNull(user::setReviewNotificationsEnabled, dto.getHasReviewsNotificationsEnabled());
        setFieldIfNotNull(user::setUpdatedAt, LocalDateTime.now());

    }

    private static <T> void setFieldIfNotNull(java.util.function.Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
