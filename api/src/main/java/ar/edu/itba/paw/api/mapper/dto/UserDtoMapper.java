package ar.edu.itba.paw.api.mapper.dto;

import ar.edu.itba.paw.api.dto.UserDTO;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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
        dto.setPreferredTheme(user.getPreferredTheme());
        dto.setHasFollowNotificationsEnabled(user.hasFollowNotificationsEnabled());
        dto.setHasLikeNotificationsEnabled(user.hasLikeNotificationsEnabled());
        dto.setHasCommentsNotificationsEnabled(user.hasCommentsNotificationsEnabled());
        dto.setHasReviewsNotificationsEnabled(user.hasReviewsNotificationsEnabled());

        return dto;
    }

    public UserDTO toDTO(User user, Boolean isFollowed) {
        UserDTO dto = toDTO(user);
        if (dto != null) {
            dto.setFollowed(isFollowed);
        }
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
        user.setImageId(dto.getImageId());
        user.setFollowersAmount(dto.getFollowersAmount());
        user.setFollowingAmount(dto.getFollowingAmount());
        user.setReviewAmount(dto.getReviewAmount());
        user.setCreatedAt(dto.getCreatedAt());
        user.setUpdatedAt(dto.getUpdatedAt());
        user.setVerified(dto.getVerified());
        user.setModerator(dto.getModerator());
        user.setPreferredLanguage(dto.getPreferredLanguage());
        user.setPreferredTheme(dto.getPreferredTheme());
        user.setHasFollowNotificationsEnabled(dto.getHasFollowNotificationsEnabled());
        user.setHasLikeNotificationsEnabled(dto.getHasLikeNotificationsEnabled());
        user.setHasCommentsNotificationsEnabled(dto.getHasCommentsNotificationsEnabled());
        user.setHasReviewsNotificationsEnabled(dto.getHasReviewsNotificationsEnabled());

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
}

