package me.ashenguard.exceptions;

public class NullAssertionError extends AssertionError {
    public NullAssertionError() {
        super();
    }

    public NullAssertionError(String message) {
        super(message);
    }

    public NullAssertionError(String message, Throwable cause) {
        super(message, cause);
    }

    public NullAssertionError(Throwable cause) {
        super(cause);
    }

    public static void check(Object obj) throws NullAssertionError {
        if (obj == null) throw new NullAssertionError();
    }
    public static void check(String message, Object obj) throws NullAssertionError {
        if (obj == null) throw new NullAssertionError(message);
    }
    public static void checkVariable(String name, Object obj) throws NullAssertionError {
        if (obj == null) throw new NullAssertionError(String.format("%s is Null", name));
    }
}