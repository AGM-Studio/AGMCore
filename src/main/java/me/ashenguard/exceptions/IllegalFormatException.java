package me.ashenguard.exceptions;

public class IllegalFormatException extends IllegalArgumentException {
    public IllegalFormatException() {
        super();
    }

    public IllegalFormatException(String message) {
        super(message);
    }
}
