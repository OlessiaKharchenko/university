package com.kharchenko.university.exception;

public class InvalidTeacherException extends RuntimeException {

    public InvalidTeacherException(String message) {
        super(message);
    }
}