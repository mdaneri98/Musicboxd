package ar.edu.itba.paw.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base class for HATEOAS resources that wraps domain objects with links
 */
public abstract class Resource<T> {
    
    @JsonProperty("_links")
    private List<Link> links = new ArrayList<>();
    
    @JsonProperty("_embedded")
    private Map<String, Object> embedded;
    
    public Resource() {}
    
    public Resource(T data) {
        // Subclasses will implement how to extract data from the domain object
    }
    
    // Abstract method to get the actual data
    public abstract T getData();
    
    // Link management methods
    public void addLink(Link link) {
        if (links == null) {
            links = new ArrayList<>();
        }
        links.add(link);
    }
    
    public void addLink(String href, String rel) {
        addLink(new Link(href, rel));
    }
    
    public void addLink(String href, String rel, String title) {
        addLink(new Link(href, rel, title));
    }
    
    public void addLink(String href, String rel, String title, String type, String method) {
        addLink(new Link(href, rel, title, type, method));
    }
    
    public void addSelfLink(String href) {
        addLink(Link.self(href));
    }
    
    public void addEditLink(String href) {
        addLink(Link.edit(href));
    }
    
    public void addDeleteLink(String href) {
        addLink(Link.delete(href));
    }
    
    public void addCollectionLink(String href) {
        addLink(Link.collection(href));
    }
    
    public void addItemLink(String href) {
        addLink(Link.item(href));
    }
    
    public void addCreateLink(String href) {
        addLink(Link.create(href));
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
