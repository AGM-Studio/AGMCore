package me.ashenguard.api.commands;

public class AGMCommandException extends RuntimeException {
    public final boolean handle;

    public AGMCommandException() {
        super();
        this.handle = false;
    }
    public AGMCommandException(String message) {
        super(message);
        this.handle = false;
    }
    public AGMCommandException(String message, Throwable cause) {
        super(message, cause);
        this.handle = false;
    }
    public AGMCommandException(Throwable cause) {
        this(cause.getMessage(), cause);
    }
    public AGMCommandException(boolean handle) {
        super();
        this.handle = handle;
    }
    public AGMCommandException(String message, boolean handle) {
        super(message);
        this.handle = handle;
    }
    public AGMCommandException(String message, Throwable cause, boolean handle) {
        super(message, cause);
        this.handle = handle;
    }
    public AGMCommandException(Throwable cause, boolean handle) {
        this(cause.getMessage(), cause, handle);
    }
}
