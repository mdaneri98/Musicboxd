package ar.edu.itba.paw.api.mapper.dto;

import ar.edu.itba.paw.api.dto.CreateUserDTO;
import ar.edu.itba.paw.api.form.UserForm;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert UserForm to CreateUserDTO
 */
@Component
public class UserFormMapper {

    public CreateUserDTO toDTO(UserForm form) {
        if (form == null) {
            return null;
        }
        
        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername(form.getUsername());
        dto.setEmail(form.getEmail());
        dto.setPassword(form.getPassword());
        
        return dto;
    }
}
