package ar.edu.itba.paw.webapp.mapper.dto;

import ar.edu.itba.paw.webapp.dto.UserDTO;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

/**
 * Mapper to convert between User model and UserDTO
 */
@Component
public class UserDtoMapper {

    public UserDTO toDTO(User user) {
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
        dto.setFollowed(user.getIsFollowed());
        
        return dto;
    }

    public List<UserDTO> toDTOList(List<User> users) {
        if (users == null) {
            return null;
        }

        return users.stream()
                .map(this::toDTO)
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
        // Note: Image needs to be set separately using ImageService if full conversion is needed
        // user.setImage(...) - requires Image object, not just ID
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
