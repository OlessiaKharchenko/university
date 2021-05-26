package com.kharchenko.university.service;

import com.kharchenko.university.exception.EntityHasReferenceException;
import com.kharchenko.university.exception.EntityIsAlreadyExistsException;
import com.kharchenko.university.exception.EntityNotFoundException;
import com.kharchenko.university.exception.InvalidEntityFieldException;
import com.kharchenko.university.exception.InvalidTeacherException;
import com.kharchenko.university.exception.InvalidGroupException;

import java.util.List;

public interface GenericService<T, ID> {

    T add(T entity) throws EntityIsAlreadyExistsException, InvalidEntityFieldException, InvalidTeacherException,
            InvalidGroupException, EntityNotFoundException;

    List<T> getAll() throws EntityNotFoundException;

    T getById(ID id) throws EntityNotFoundException;

    void update(T entity) throws EntityIsAlreadyExistsException, EntityNotFoundException, InvalidEntityFieldException,
            InvalidTeacherException, InvalidGroupException;

    boolean deleteById(ID id) throws EntityNotFoundException, EntityHasReferenceException;

    void addAll(List<T> list) throws EntityIsAlreadyExistsException, InvalidEntityFieldException, InvalidTeacherException,
            InvalidGroupException, EntityNotFoundException;
}
