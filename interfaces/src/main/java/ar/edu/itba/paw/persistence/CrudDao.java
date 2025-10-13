package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.FilterType;

import java.util.List;
import java.util.Optional;

public interface CrudDao<T> {
    Optional<T> findById(Long id);

    List<T> findAll();

    List<T> findPaginated(FilterType filterType, Integer page, Integer pageSize);

    T create(T entity);

    T update(T entity);

    Boolean delete(Long id);
}