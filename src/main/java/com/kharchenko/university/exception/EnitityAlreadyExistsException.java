package com.kharchenko.university.exception;

public class EnitityAlreadyExistsException extends RuntimeException {

    public EnitityAlreadyExistsException(String message) {
        super(message);
    }
}