package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.FilterType;

import java.util.List;
import java.util.Optional;

public interface CrudService<T> {
    Optional<T> find(long id);

    List<T> findAll();

    List<T> findPaginated(FilterType filterType, int page, int pageSize);

    T create(T entity);

    T update(T entity);

    boolean delete(long id);
}