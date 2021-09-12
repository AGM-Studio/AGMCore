package me.ashenguard.lib.spigot;

import me.ashenguard.lib.events.guardian.GuardianTargetEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class Guardian {
    private @NotNull LivingEntity target;
    private final @NotNull Creature entity;

    protected Guardian(@NotNull LivingEntity target, @NotNull Creature entity) {
        this.target = target;
        this.entity = entity;
    }

    public @NotNull LivingEntity getTarget() {
        return target;
    }
    public void setTarget(@NotNull LivingEntity target) {
        this.target = target;
    }

    public @NotNull Creature getEntity() {
        return entity;
    }

    public void teleport(Location location) {
        if (location == null) return;
        this.entity.teleport(location);
    }

    public void attack(LivingEntity entity) {
        if (entity == null) return;
        if (this.entity.getTarget() != null) return;

        GuardianTargetEntityEvent event = new GuardianTargetEntityEvent(this, entity);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        this.entity.setTarget(entity);
    }
}
