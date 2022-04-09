package me.ashenguard.exceptions;

public class PluginNotEnabled extends RuntimeException {
    public PluginNotEnabled() {
        super();
    }
    public PluginNotEnabled(String message) {
        super(message);
    }
    public PluginNotEnabled(String message, Throwable cause) {
        super(message, cause);
    }
    public PluginNotEnabled(Throwable cause) {
        super(cause);
    }
}
