package ar.edu.itba.paw.webapp.mapper.dto;

import ar.edu.itba.paw.webapp.dto.UserDTO;
import ar.edu.itba.paw.models.User;
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
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
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

        // Build HATEOAS links
        if (uriInfo != null) {
            dto.setSelf(uriInfo.getBaseUriBuilder()
                    .path("users").path(String.valueOf(user.getId())).build());

            if (user.getImageId() != null) {
                dto.setImage(uriInfo.getBaseUriBuilder()
                        .path("images").path(String.valueOf(user.getImageId())).build());
            }

            dto.setReviews(uriInfo.getBaseUriBuilder()
                    .path("users").path(String.valueOf(user.getId())).path("reviews").build());

            dto.setFollowers(uriInfo.getBaseUriBuilder()
                    .path("users").path(String.valueOf(user.getId())).path("followers").build());

            dto.setFollowing(uriInfo.getBaseUriBuilder()
                    .path("users").path(String.valueOf(user.getId())).path("following").build());

            dto.setFavoriteArtists(uriInfo.getBaseUriBuilder()
                    .path("users").path(String.valueOf(user.getId())).path("favorites").path("artists").build());

            dto.setFavoriteAlbums(uriInfo.getBaseUriBuilder()
                    .path("users").path(String.valueOf(user.getId())).path("favorites").path("albums").build());

            dto.setFavoriteSongs(uriInfo.getBaseUriBuilder()
                    .path("users").path(String.valueOf(user.getId())).path("favorites").path("songs").build());
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

    public User toModel(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setBio(dto.getBio());
        user.setFollowersAmount(dto.getFollowersAmount());
        user.setFollowingAmount(dto.getFollowingAmount());
        user.setReviewAmount(dto.getReviewAmount());
        user.setCreatedAt(dto.getCreatedAt());
        user.setUpdatedAt(dto.getUpdatedAt());
        user.setVerified(dto.getVerified() != null ? dto.getVerified() : false);
        user.setModerator(dto.getModerator());
        user.setPreferredLanguage(dto.getPreferredLanguage());
        user.setTheme(dto.getPreferredTheme());
        user.setFollowNotificationsEnabled(dto.getHasFollowNotificationsEnabled());
        user.setLikeNotificationsEnabled(dto.getHasLikeNotificationsEnabled());
        user.setCommentNotificationsEnabled(dto.getHasCommentsNotificationsEnabled());
        user.setReviewNotificationsEnabled(dto.getHasReviewsNotificationsEnabled());

        return user;
    }

    public List<User> toModelList(List<UserDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public static void mergeConfigToModel(User user, UserDTO dto) {
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
