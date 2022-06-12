package me.ashenguard.exceptions;

/**
  @deprecated instead use {@link NullAssertionError}
 */
@SuppressWarnings("unused")
public class NullValue extends RuntimeException {
    public NullValue() {
        super("Value is Null.");
    }
    public NullValue(String message) {
        super(message);
    }
    public NullValue(String message, Throwable cause) {
        super(message, cause);
    }
    public NullValue(Throwable cause) {
        super(cause);
    }

    protected NullValue(String message, Throwable cause,
                        boolean enableSuppression,
                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static void check(String name, Object obj) throws NullValue {
        if (obj == null) throw new NullValue(String.format("%s is Null.", name));
    }
}
