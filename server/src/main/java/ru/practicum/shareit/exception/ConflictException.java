package ru.practicum.shareit.exception;

public class ConflictException extends Exception {
    public ConflictException() {
    }

    public ConflictException(String message) {
        super(message);
    }
}
