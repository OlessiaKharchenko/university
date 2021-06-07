package com.kharchenko.university.exception;

public class EntityHasReferenceException extends RuntimeException {

    public EntityHasReferenceException(String message) {
        super(message);
    }
}