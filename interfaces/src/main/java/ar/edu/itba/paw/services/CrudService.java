package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.FilterType;

import java.util.List;

public interface CrudService<T> {
    T findById(Long id, Long loggedUserId);

    List<T> findAll();

    List<T> findPaginated(FilterType filterType, Integer page, Integer pageSize);

    T create(T entity);

    T update(T entity);

    Boolean delete(Long id);
}