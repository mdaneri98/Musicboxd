package ar.edu.itba.paw.services;

import java.util.List;
import java.util.Optional;

public interface CrudService<T> {
    Optional<T> findById(long id);

    List<T> findAll();

    List<T> findPaginated(int limit, int offset);

    long save(T entity);

    int update(T entity);

    int deleteById(long id);
}