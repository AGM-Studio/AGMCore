package me.ashenguard.exceptions;

public class IllegalTypeException extends IllegalArgumentException {
    public IllegalTypeException() {
        super();
    }
    public IllegalTypeException(String message) {
        super(message);
    }
}
