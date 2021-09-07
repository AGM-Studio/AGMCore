package me.ashenguard.lib.events.guardian;

import me.ashenguard.lib.spigot.Guardian;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class GuardianEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Guardian guardian;

    @Override public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public GuardianEvent(@NotNull Guardian guardian) {
        this.guardian = guardian;
    }

    public Guardian getGuardian() {
        return guardian;
    }
}
