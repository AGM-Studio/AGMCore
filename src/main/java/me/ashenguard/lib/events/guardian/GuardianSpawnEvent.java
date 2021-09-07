package me.ashenguard.lib.events.guardian;

import me.ashenguard.lib.spigot.Guardian;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class GuardianSpawnEvent extends GuardianEvent implements Cancellable {
    private final Location spawnLocation;
    private boolean cancel = false;

    public GuardianSpawnEvent(@NotNull Guardian guardian, Location spawnLocation) {
        super(guardian);
        this.spawnLocation = spawnLocation;
    }

    public LivingEntity getTarget() {
        return this.getGuardian().getTarget();
    }

    public void setTarget(LivingEntity target) {
        this.getGuardian().setTarget(target);
    }

    public Location getSpawnLocation() {
        return spawnLocation.clone();
    }

    @Override public boolean isCancelled() {
        return cancel;
    }

    @Override public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
