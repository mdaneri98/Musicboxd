package ar.edu.itba.paw.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * HATEOAS resource wrapper for collections with pagination support
 */
public class CollectionResource<T> extends Resource<List<T>> {
    
    @JsonProperty("items")
    private List<T> items;
    
    @JsonProperty("totalCount")
    private Long totalCount;
    
    @JsonProperty("pageSize")
    private Integer pageSize;
    
    @JsonProperty("currentPage")
    private Integer currentPage;
    
    @JsonProperty("totalPages")
    private Integer totalPages;
    
    public CollectionResource() {}
    
    public CollectionResource(List<T> items) {
        this.items = items;
    }
    
    public CollectionResource(List<T> items, Long totalCount, Integer pageSize, Integer currentPage) {
        this.items = items;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        this.totalPages = totalCount != null && pageSize != null && pageSize > 0 
            ? (int) Math.ceil((double) totalCount / pageSize) 
            : null;
    }
    
    @Override
    public List<T> getData() {
        return items;
    }
    
    // Pagination link helpers
    public void addPaginationLinks(String baseUrl, Integer currentPage, Integer totalPages) {
        if (currentPage != null && totalPages != null) {
            // First page
            if (currentPage > 1) {
                addLink(Link.first(baseUrl + "?page=1"));
            }
            
            // Previous page
            if (currentPage > 1) {
                addLink(Link.prev(baseUrl + "?page=" + (currentPage - 1)));
            }
            
            // Next page
            if (currentPage < totalPages) {
                addLink(Link.next(baseUrl + "?page=" + (currentPage + 1)));
            }
            
            // Last page
            if (currentPage < totalPages) {
                addLink(Link.last(baseUrl + "?page=" + totalPages));
            }
        }
    }
    
    // Getters and setters
    public List<T> getItems() {
        return items;
    }
    
    public void setItems(List<T> items) {
        this.items = items;
    }
    
    public Long getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
    
    public Integer getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    
    public Integer getCurrentPage() {
        return currentPage;
    }
    
    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }
    
    public Integer getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
}
