package me.ashenguard.exceptions;

@SuppressWarnings("unused")
public class ConstructorNotFound extends RuntimeException {
    public ConstructorNotFound() {
        super("No proper constructor was found in the class.");
    }
    public ConstructorNotFound(Class<?> clazz) {
        super(String.format("No proper constructor was found in the class %s.", clazz.getName()));
    }
    public ConstructorNotFound(String message) {
        super(message);
    }
    public ConstructorNotFound(String message, Throwable cause) {
        super(message, cause);
    }
    public ConstructorNotFound(Throwable cause) {
        super(cause);
    }

    protected ConstructorNotFound(String message, Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
