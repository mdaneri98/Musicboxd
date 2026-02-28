package ar.edu.itba.paw.webapp.mapper.dto;

import ar.edu.itba.paw.infrastructure.jpa.UserJpaEntity;
import ar.edu.itba.paw.webapp.dto.UserDTO;
import ar.edu.itba.paw.webapp.form.UserProfileForm;
import ar.edu.itba.paw.models.Image;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert UserProfileForm to User model
 */
@Component
public class UserProfileFormMapper {

    public UserJpaEntity toModel(UserProfileForm form) {
        if (form == null) {
            return null;
        }

        UserJpaEntity user = new UserJpaEntity();
        user.setUsername(form.getUsername());
        user.setName(form.getName());
        user.setBio(form.getBio());
        user.setImageId(form.getImageId());

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
