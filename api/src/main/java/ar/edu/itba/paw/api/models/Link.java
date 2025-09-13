package ar.edu.itba.api.models;

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
    
    public Link(String href, String rel) {
        this.href = href;
        this.rel = rel;
    }
    
    public Link(String href, String rel, String title) {
        this.href = href;
        this.rel = rel;
        this.title = title;
    }
    
    public Link(String href, String rel, String title, String type, String method) {
        this.href = href;
        this.rel = rel;
        this.title = title;
        this.type = type;
        this.method = method;
    }
    
    // Static factory methods for common link types
    public static Link self(String href) {
        return new Link(href, "self");
    }
    
    public static Link next(String href) {
        return new Link(href, "next");
    }
    
    public static Link prev(String href) {
        return new Link(href, "prev");
    }
    
    public static Link first(String href) {
        return new Link(href, "first");
    }
    
    public static Link last(String href) {
        return new Link(href, "last");
    }
    
    public static Link collection(String href) {
        return new Link(href, "collection");
    }
    
    public static Link collection(String href, String title) {
        return new Link(href, "collection", title);
    }
    
    public static Link collection(String href, String rel, String title) {
        return new Link(href, rel, title);
    }
    
    public static Link item(String href) {
        return new Link(href, "item");
    }
    
    public static Link item(String href, String title) {
        return new Link(href, "item", title);
    }
    
    public static Link item(String href, String rel, String title) {
        return new Link(href, rel, title);
    }
    
    public static Link edit(String href) {
        return new Link(href, "edit", "Edit this resource", "application/json", "PUT");
    }
    
    public static Link delete(String href) {
        return new Link(href, "delete", "Delete this resource", null, "DELETE");
    }
    
    public static Link create(String href) {
        return new Link(href, "create", "Create new resource", "application/json", "POST");
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
    
    @Override
    public String toString() {
        return "Link{" +
                "href='" + href + '\'' +
                ", rel='" + rel + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", method='" + method + '\'' +
                '}';
    }
}
