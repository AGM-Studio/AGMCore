package me.ashenguard.lib.events.guardian;

import me.ashenguard.lib.spigot.Guardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class GuardianTargetEntityEvent extends GuardianEvent implements Cancellable {
    private final LivingEntity entity;
    private boolean cancel = false;

    public GuardianTargetEntityEvent(@NotNull Guardian guardian, LivingEntity entity) {
        super(guardian);
        this.entity = entity;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public LivingEntity getEntity() {
        return entity;
    }
}
