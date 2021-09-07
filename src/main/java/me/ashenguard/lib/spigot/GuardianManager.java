package me.ashenguard.lib.spigot;

import me.ashenguard.agmcore.AGMEvents;
import me.ashenguard.lib.events.guardian.GuardianRemoveEvent;
import me.ashenguard.lib.events.guardian.GuardianSpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GuardianManager implements Listener {
    private final static List<Guardian> guards = new ArrayList<>();

    public static Guardian spawnGuardian(LivingEntity target, EntityType type, double range) {
        final Location location = target.getLocation();
        final World world = location.getWorld() == null ? target.getWorld() : location.getWorld();

        Vector offset = new Vector(1, 0, 0).rotateAroundY(Math.random() * 2 * Math.PI).multiply(Math.random() * range);
        location.add(offset);
        while (!location.getBlock().isEmpty()) location.add(0, 0.5, 1);

        Entity entity = world.spawnEntity(target.getLocation(), type, false);
        Creature creature;
        if (entity instanceof Creature) {
            creature = (Creature) entity;
            creature.setRemoveWhenFarAway(false);
        } else {
            entity.remove();
            return null;
        }

        Guardian guardian = new Guardian(target, creature);
        GuardianSpawnEvent event = new GuardianSpawnEvent(guardian, location);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled())  {
            guardian.getEntity().remove();
            return null;
        }

        guards.add(guardian);
        AGMEvents.activateGuardians();
        return guardian;
    }

    public static void removeGuardian(Guardian guardian) {
        removeGuardian(guardian, GuardianRemoveEvent.Reason.CUSTOM);
    }
    private static void removeGuardian(Guardian guardian, GuardianRemoveEvent.Reason reason) {
        guardian.getEntity().remove();
        guards.remove(guardian);

        Bukkit.getServer().getPluginManager().callEvent(
                new GuardianRemoveEvent(guardian, reason)
        );
    }

    public static boolean isGuardian(Entity entity) {
        return guards.stream().anyMatch(guardian -> guardian.getEntity() == entity);
    }

    public static LivingEntity getGuardianTarget(Entity entity) {
        return guards.stream().filter(guardian -> guardian.getEntity() == entity).map(Guardian::getTarget).findAny().orElse(null);
    }

    public static List<Guardian> getGuardians(LivingEntity entity) {
        return guards.stream().filter(guardian -> guardian.getTarget() == entity).collect(Collectors.toList());
    }

    public static void checkGuardians() {
        guards.stream().filter(guardian -> !guardian.getEntity().isValid()).forEach(guardian -> removeGuardian(guardian, GuardianRemoveEvent.Reason.UNKNOWN));
    }

    @EventHandler
    public void onTargeting(EntityTargetEvent event) {
        Entity entity = event.getEntity();
        Entity target = event.getTarget();
        TargetReason reason = event.getReason();
        if (target == null) return;

        boolean isEntityGuardian = isGuardian(entity);
        boolean isTargetGuardian = isGuardian(target);

        if (isEntityGuardian && isTargetGuardian) {
            LivingEntity entityGuard = getGuardianTarget(entity);
            LivingEntity targetGuard = getGuardianTarget(target);

            if (entityGuard == targetGuard) event.setCancelled(true);
        } else if (isEntityGuardian) {
            LivingEntity entityGuard = getGuardianTarget(entity);

            if (target == entityGuard) event.setCancelled(true);
            if (reason != TargetReason.OWNER_ATTACKED_TARGET && reason != TargetReason.TARGET_ATTACKED_OWNER && reason != TargetReason.CUSTOM && reason != TargetReason.TARGET_ATTACKED_ENTITY)
                event.setCancelled(true);
        } else if (isTargetGuardian) {
            if (reason == TargetReason.CLOSEST_ENTITY || reason == TargetReason.RANDOM_TARGET || reason == TargetReason.DEFEND_VILLAGE)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity attacker = null;
        if (event.getDamager() instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) event.getDamager()).getShooter();
            if (shooter instanceof LivingEntity)
                attacker = (LivingEntity) shooter;
        } else if (event.getDamager() instanceof LivingEntity) {
            attacker = (LivingEntity) event.getDamager();
        } else return;
        LivingEntity entity = (LivingEntity) event.getEntity();

        List<Guardian> guardians = getGuardians(entity);
        for (Guardian guardian: guardians) guardian.attack(attacker);

        if (isGuardian(entity) && getGuardianTarget(entity) == attacker) event.setCancelled(true);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        guards.stream().filter(guardian -> event.getEntity() == guardian.getEntity()).forEach(guardian -> removeGuardian(guardian, GuardianRemoveEvent.Reason.DIED));
        guards.stream().filter(guardian -> event.getEntity() == guardian.getTarget()).forEach(guardian -> removeGuardian(guardian, GuardianRemoveEvent.Reason.TARGET_DIED));
        if (isGuardian(event.getEntity())) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }
    }
}
