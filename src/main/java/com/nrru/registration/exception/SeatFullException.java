package com.nrru.registration.exception;

public class SeatFullException extends RuntimeException {
    public SeatFullException(String message) {
        super(message);
    }
}