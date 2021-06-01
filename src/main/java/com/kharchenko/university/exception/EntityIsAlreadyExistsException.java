package com.kharchenko.university.exception;

public class EntityIsAlreadyExistsException extends Exception {

    public EntityIsAlreadyExistsException(String message) {
        super(message);
    }
}