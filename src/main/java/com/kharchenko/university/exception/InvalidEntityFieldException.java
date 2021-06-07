package com.kharchenko.university.exception;

public class InvalidEntityFieldException extends RuntimeException {

    public InvalidEntityFieldException(String message) {
        super(message);
    }
}
