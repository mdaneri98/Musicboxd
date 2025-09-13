package ar.edu.itba.api.mapper;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.api.models.UserResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserResourceMapper implements ResourceMapper<User, UserResource> {

    /**
     * Converts a single User entity to UserResource with HATEOAS links
     */
    public UserResource toResource(User user, String baseUrl) {
        UserResource resource = new UserResource(user);
        addUserLinks(resource, baseUrl, user.getId());
        return resource;
    }

    /**
     * Converts a list of User entities to UserResource list with HATEOAS links
     */
    public List<UserResource> toResourceList(List<User> users, String baseUrl) {
        return users.stream()
                .map(user -> toResource(user, baseUrl))
                .collect(Collectors.toList());
    }

    /**
     * Adds HATEOAS links to a UserResource
     */
    private void addUserLinks(UserResource resource, String baseUrl, Long userId) {
        // Self link
        resource.addLink("self", baseUrl + "/users/" + userId);
        
        // Related resources links
        resource.addLink("reviews", baseUrl + "/users/" + userId + "/reviews");
        resource.addLink("followers", baseUrl + "/users/" + userId + "/followers");
        resource.addLink("following", baseUrl + "/users/" + userId + "/following");
        
        // Action links (if user is authenticated and can perform actions)
        resource.addLink("update", baseUrl + "/users/" + userId, "PUT");
        resource.addLink("delete", baseUrl + "/users/" + userId, "DELETE");
    }
}
