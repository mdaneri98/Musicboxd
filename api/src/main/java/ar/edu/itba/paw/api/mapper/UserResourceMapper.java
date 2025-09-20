package ar.edu.itba.paw.api.mapper;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.api.models.UserResource;
import ar.edu.itba.paw.api.utils.UserLinkManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserResourceMapper implements ResourceMapper<User, UserResource> {

    @Autowired
    private UserLinkManager userLinkManager;

    public UserResource toResource(User user, String baseUrl) {
        UserResource resource = new UserResource(user);
        userLinkManager.addUserLinks(resource, baseUrl, user.getId());
        return resource;
    }

    public List<UserResource> toResourceList(List<User> users, String baseUrl) {
        return users.stream()
                .map(user -> toResource(user, baseUrl))
                .collect(Collectors.toList());
    }
}
