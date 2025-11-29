package ar.edu.itba.paw.api.mapper.dto;

import ar.edu.itba.paw.api.dto.UserDTO;
import ar.edu.itba.paw.api.form.UserProfileForm;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert UserProfileForm to User model
 */
@Component
public class UserProfileFormMapper {

    public User toModel(UserProfileForm form) {
        if (form == null) {
            return null;
        }
        
        User user = new User();
        user.setUsername(form.getUsername());
        user.setName(form.getName());
        user.setBio(form.getBio());
        // Store imageId temporarily - UserServiceImpl will use ImageService to get the full Image
        if (form.getImageId() != null) {
            ar.edu.itba.paw.models.Image image = new ar.edu.itba.paw.models.Image(form.getImageId(), null);
            user.setImage(image);
        }
        
        return user;
    }

    /**
     * @deprecated Use toModel instead. This is kept for backward compatibility.
     */
    @Deprecated
    public UserDTO toDTO(UserProfileForm form) {
        if (form == null) {
            return null;
        }
        
        UserDTO dto = new UserDTO();
        dto.setUsername(form.getUsername());
        dto.setName(form.getName());
        dto.setBio(form.getBio());
        dto.setImageId(form.getImageId());
        
        return dto;
    }
}
