package com.kharchenko.university.dao;

import java.util.List;
import java.util.Optional;

public interface GenericDao<T, ID> {

    T add(T entity);

    List<T> getAll();

    Optional<T> getById(ID id);

    void update(T entity);

    boolean deleteById(ID id);

    void addAll(List<T> list);
}
