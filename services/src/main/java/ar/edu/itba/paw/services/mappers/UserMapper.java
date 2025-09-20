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
        dto.setImageId(user.getImage() != null ? user.getImage().getId() : null);
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
}
