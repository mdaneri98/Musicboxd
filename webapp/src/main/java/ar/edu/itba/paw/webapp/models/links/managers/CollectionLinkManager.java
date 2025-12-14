package ar.edu.itba.paw.webapp.models.links.managers;

public class CollectionLinkManager {
    Boolean create;
    Boolean delete;
    Boolean edit;
    Boolean search;
    Boolean pagination;

    public CollectionLinkManager(Boolean create, Boolean delete, Boolean edit, Boolean search, Boolean pagination) {
        this.create = create;
        this.delete = delete;
        this.edit = edit;
        this.search = search;
        this.pagination = pagination;
    }

    public Boolean getCreate() {
        return create;
    }

    public Boolean getDelete() {
        return delete;
    }
    
    public Boolean getEdit() {
        return edit;
    }

    public Boolean getSearch() {
        return search;
    }

    public Boolean getPagination() {
        return pagination;
    }
}
