package main.java.com.netflix.repositories;

import java.util.List;

public interface Repository<T> {
    void save(T entity);
    T findById(Long id);
    List<T> findAll();
    void update(T entity);
    void delete(Long id);
}
