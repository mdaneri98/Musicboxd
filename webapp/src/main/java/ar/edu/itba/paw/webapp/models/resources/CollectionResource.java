package ar.edu.itba.paw.webapp.models.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HATEOAS resource wrapper for collections with pagination support
 */
public class CollectionResource<T> extends Resource<List<T>> {
    
    @JsonIgnore
    private Long totalCount;
    
    @JsonIgnore
    private Integer pageSize;
    
    @JsonIgnore
    private Integer currentPage;
    
    @JsonIgnore
    private Integer totalPages;
    
    public CollectionResource() {
        super();
    }
    
    public CollectionResource(List<T> items) {
        super();
        if (items != null && !items.isEmpty()) {
            addEmbedded("items", items);
        }
    }
    
    public CollectionResource(List<T> items, Long totalCount, Integer pageSize, Integer currentPage) {
        super();
        if (items != null && !items.isEmpty()) {
            addEmbedded("items", items);
        }
        
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        this.totalPages = totalCount != null && pageSize != null && pageSize > 0 
            ? (int) Math.ceil((double) totalCount / pageSize) 
            : null;
    }
    
    @JsonIgnore
    public List<T> getData() {
        Map<String, Object> embedded = getEmbedded();
        if (embedded != null && embedded.containsKey("items")) {
            @SuppressWarnings("unchecked")
            List<T> items = (List<T>) embedded.get("items");
            return items;
        }
        return null;
    }
    
    
    @JsonIgnore
    public Long getTotalCount() {
        return totalCount;
    }
    
    @JsonIgnore
    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
    
    @JsonIgnore
    public Integer getPageSize() {
        return pageSize;
    }
    
    @JsonIgnore
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    
    @JsonIgnore
    public Integer getCurrentPage() {
        return currentPage;
    }
    
    @JsonIgnore
    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }
    
    @JsonIgnore
    public Integer getTotalPages() {
        return totalPages;
    }
    
    @JsonIgnore
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
}
