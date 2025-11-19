package ar.edu.itba.paw.api.mapper.dto;

import ar.edu.itba.paw.api.form.UserProfileForm;
import ar.edu.itba.paw.models.dtos.UserDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert UserProfileForm to UserDTO
 */
@Component
public class UserProfileFormMapper {

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

