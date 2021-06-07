package com.kharchenko.university.service;

import java.util.List;

public interface GenericService<T, ID> {

    T add(T entity);

    List<T> getAll();

    T getById(ID id);

    void update(T entity);

    boolean deleteById(ID id);

    void addAll(List<T> list);
}

