package me.ashenguard.exceptions;

public class InstanceAssertionError extends AssertionError {
    public InstanceAssertionError() {
        super();
    }

    public InstanceAssertionError(String message) {
        super(message);
    }

    public InstanceAssertionError(String message, Throwable cause) {
        super(message, cause);
    }

    public InstanceAssertionError(Throwable cause) {
        super(cause);
    }

    public static void check(Object object, Class<?> cls) {
        if (cls.isInstance(object)) throw new InstanceAssertionError();
    }
    public static void check(Object object, Class<?> cls, String message) {
        if (cls.isInstance(object)) throw new InstanceAssertionError(message);
    }
}