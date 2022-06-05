package me.ashenguard.exceptions;

import me.ashenguard.api.versions.MCVersion;

public class InvalidServerVersion extends RuntimeException {
    public InvalidServerVersion() {
        super();
    }
    public InvalidServerVersion(String message) {
        super(message);
    }
    public InvalidServerVersion(String message, Throwable cause) {
        super(message, cause);
    }
    public InvalidServerVersion(Throwable cause) {
        super(cause);
    }

    public static void assertVersion(MCVersion version) {
        InvalidServerVersion.assertVersion(version, String.format("Version %s is required.", version.version.toString()));
    }
    public static void assertVersion(MCVersion version, String message) {
        if (MCVersion.getMCVersion() != version) throw new InvalidServerVersion(message);
    }
}
