package me.ashenguard.exceptions;

public class BlockTypeException extends RuntimeException {
    public BlockTypeException() {
        super();
    }
    public BlockTypeException(String message) {
        super(message);
    }
    public BlockTypeException(String message, Throwable cause) {
        super(message, cause);
    }
    public BlockTypeException(Throwable cause) {
        super(cause);
    }
}