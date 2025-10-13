package ar.edu.itba.paw.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents a HATEOAS link with href, rel, and optional title and type
 */
public class Link {
    
    @JsonProperty("href")
    private String href;
    
    @JsonProperty("rel")
    private String rel;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("method")
    private String method;
    
    public Link() {}
    
    private Link(String href, String title, String type, String method) {
        this.href = href;
        this.title = title;
        this.method = method;
        this.type = type;
    }
    
    private Link(String href, String rel, String title, String type, String method) {
        this.href = href;
        this.rel = rel;
        this.title = title;
        this.type = type;
        this.method = method;
    }


    public static Link createLink(String href, String rel, String title, String method) {
        return new Link(href, rel, title, "application/json", method);
    }
    public static Link createLink(String href, String title, String method) {
        return new Link(href, title, "application/json", method);
    }
    
    // Getters and setters
    public String getHref() {
        return href;
    }
    
    public void setHref(String href) {
        this.href = href;
    }
    
    public String getRel() {
        return rel;
    }
    
    public void setRel(String rel) {
        this.rel = rel;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Link link)) return false;
        return Objects.equals(href, link.href) && 
               Objects.equals(rel, link.rel);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(href, rel);
    }
}
