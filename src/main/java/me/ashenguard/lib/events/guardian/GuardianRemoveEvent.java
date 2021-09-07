package me.ashenguard.lib.events.guardian;

import me.ashenguard.lib.spigot.Guardian;
import org.jetbrains.annotations.NotNull;

public class GuardianRemoveEvent extends GuardianEvent {
    private final Reason reason;

    public GuardianRemoveEvent(@NotNull Guardian guardian, Reason reason) {
        super(guardian);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }

    public enum Reason {
        /**
         * Because the guardian has been died
         */
        DIED,
        /**
         * Because the target has been killed
         */
        TARGET_DIED,
        /**
         * Because of a custom reason
         */
        CUSTOM,
        /**
         * Because of the time out
         */
        TIME_OUT,
        /**
         * Because of an unknown issue
         */
        UNKNOWN
    }
}
