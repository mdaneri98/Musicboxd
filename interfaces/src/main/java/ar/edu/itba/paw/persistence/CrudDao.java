package ar.edu.itba.paw.persistence;

import java.util.List;
import java.util.Optional;

public interface CrudDao<T> {
    Optional<T> findById(long id);

    List<T> findAll();

    long save(T entity);

    int update(T entity);

    int deleteById(long id);
}