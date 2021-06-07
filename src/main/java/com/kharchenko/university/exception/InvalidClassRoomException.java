package com.kharchenko.university.exception;

public class InvalidClassRoomException extends RuntimeException {

    public InvalidClassRoomException(String message) {
        super(message);
    }
}