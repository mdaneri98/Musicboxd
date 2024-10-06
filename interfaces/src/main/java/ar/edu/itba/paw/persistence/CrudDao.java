package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.FilterType;

import java.util.List;
import java.util.Optional;

public interface CrudDao<T> {
    Optional<T> find(long id);

    List<T> findAll();

    List<T> findPaginated(FilterType filterType, int limit, int offset);

    T create(T entity);

    T update(T entity);

    boolean delete(long id);
}