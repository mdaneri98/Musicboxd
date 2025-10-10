package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.FilterType;

import java.util.List;

public interface CrudService<T> {
    T findById(Long id);

    List<T> findAll();

    List<T> findPaginated(FilterType filterType, int page, int pageSize);

    T create(T entity);

    T update(T entity);

    Boolean delete(Long id);
}