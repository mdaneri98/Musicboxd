package ar.edu.itba.paw.api.mapper.resource;

import ar.edu.itba.paw.models.dtos.UserDTO;
import ar.edu.itba.paw.api.models.resources.UserResource;
import ar.edu.itba.paw.api.models.links.managers.UserLinkManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserResourceMapper implements ResourceMapper<UserDTO, UserResource> {

    @Autowired
    private UserLinkManager userLinkManager;

    @Override
    public UserResource toResource(UserDTO userDTO, String baseUrl) {
        UserResource resource = new UserResource(userDTO);
        userLinkManager.addUserLinks(resource, baseUrl, userDTO.getId());
        return resource;
    }

    @Override
    public List<UserResource> toResourceList(List<UserDTO> userDTOs, String baseUrl) {
        return userDTOs.stream()
                .map(userDTO -> toResource(userDTO, baseUrl))
                .collect(Collectors.toList());
    }
}
