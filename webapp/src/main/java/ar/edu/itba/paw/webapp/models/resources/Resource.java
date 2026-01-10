package ar.edu.itba.paw.webapp.models.resources;

import ar.edu.itba.paw.webapp.models.links.Link;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base class for HATEOAS resources that wraps domain objects with links
 */
public class Resource<T> {
    
    @JsonProperty("_links")
    private List<Link> links = new ArrayList<>();
    
    @JsonProperty("_embedded")
    private Map<String, Object> embedded;
    
    public Resource() {}
    
    // Link management methods
    public void addLink(Link link) {
        if (links == null) {
            links = new ArrayList<>();
        }
        links.add(link);
    }
    
    public void addLink(String href, String rel, String title, String method) {
        addLink(Link.createLink(href, rel, title, method));
    }
    
    public void addSelfLink(String href) {
        addLink(Link.createLink(href, "self", "Self Link", "GET"));
    }
    
    public void addEditLink(String href) {
        addLink(Link.createLink(href, "edit", "Edit Link", "PUT"));
    }
    
    public void addDeleteLink(String href) {
        addLink(Link.createLink(href, "delete", "Delete Link", "DELETE"));
    }
    
    public void addCollectionLink(String href) {
        addLink(Link.createLink(href, "collection", "Collection Link", "GET"));
    }

    public void addCreateLink(String href) {
        addLink(Link.createLink(href, "create", "Create Link", "POST"));
    }
    
    // Getters and setters
    public List<Link> getLinks() {
        return links;
    }
    
    public void setLinks(List<Link> links) {
        this.links = links;
    }
    
    public Map<String, Object> getEmbedded() {
        return embedded;
    }
    
    public void setEmbedded(Map<String, Object> embedded) {
        this.embedded = embedded;
    }
    
    public void addEmbedded(String key, Object value) {
        if (embedded == null) {
            embedded = new java.util.HashMap<>();
        }
        embedded.put(key, value);
    }
}
