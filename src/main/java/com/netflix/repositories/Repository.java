package com.netflix.repositories;

import java.util.List;

public interface Repository<T> {
    void save(T entity);
    T findById(int id);
    List<T> findAll();
    void update(T entity) throws Exception;
    void delete(int id) throws Exception;
}
