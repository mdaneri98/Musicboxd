package ar.edu.itba.paw.services.mappers;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.dtos.UserDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

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
        dto.setImageId(user.getImage().getId());
        dto.setFollowersAmount(user.getFollowersAmount());
        dto.setFollowingAmount(user.getFollowingAmount());
        dto.setReviewsAmount(user.getReviewAmount());
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

    public User toEntity(UserDTO dto) {
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
        user.setReviewAmount(dto.getReviewsAmount());
        user.setCreatedAt(dto.getCreatedAt());
        user.setUpdatedAt(dto.getUpdatedAt());
        user.setVerified(dto.getVerified());
        user.setModerator(dto.getModerator());
        user.setPreferredLanguage(dto.getPreferredLanguage());
        user.setTheme(dto.getPreferredTheme());
        user.setFollowNotificationsEnabled(dto.getHasFollowNotificationsEnabled());
        user.setLikeNotificationsEnabled(dto.getHasLikeNotificationsEnabled());
        user.setCommentNotificationsEnabled(dto.getHasCommentsNotificationsEnabled());
        user.setReviewNotificationsEnabled(dto.getHasReviewsNotificationsEnabled());

        return user;
    }

    public List<User> toEntityList(List<UserDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
