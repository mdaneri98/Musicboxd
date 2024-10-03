package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;

import java.util.List;
import java.util.Optional;

public interface CrudDao<T> {
    Optional<T> findById(long id);

    List<T> findAll();

    List<T> findPaginated(FilterType filterType, int limit, int offset);

    long save(T entity);

    int update(T entity);

    int deleteById(long id);
}