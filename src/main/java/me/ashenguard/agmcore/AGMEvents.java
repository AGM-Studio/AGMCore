package me.ashenguard.agmcore;

import me.ashenguard.lib.events.DayCycleEvent;
import me.ashenguard.lib.events.door.DoorManager;
import me.ashenguard.lib.events.equipment.ArmorListener;
import me.ashenguard.lib.events.equipment.DispenserListener;
import me.ashenguard.lib.spigot.GuardianManager;
import me.ashenguard.lib.spigot.PermanentEffectManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

@SuppressWarnings("unused")
public class AGMEvents implements Listener {
    private static final AGMCore core = AGMCore.getInstance();

    private static boolean isArmorEquipEventActive = false;
    private static boolean isPermanentEffectsActive = false;
    private static boolean isGuardiansActive = false;
    private static boolean isDayCycleActive = false;
    private static boolean isDoorActive = false;

    private static final int INTERVAL = 20;

    private static class DayCycleTask implements Runnable {
        @Override public void run() {
            for (World world: Bukkit.getServer().getWorlds()) {
                long delay = DayCycleEvent.Cycle.getCycle(world).getPassedTime(world);
                if (delay < INTERVAL) Bukkit.getServer().getPluginManager().callEvent(new DayCycleEvent(world));
            }
        }
    }
    private static BukkitTask DayCycleCaller = null;
    private static class GuardianCheckTask implements Runnable {
        @Override public void run() {
            GuardianManager.checkGuardians();
        }
    }
    private static BukkitTask GuardianCheck = null;

    public static void activateArmorEquipEvent() {
        if (isArmorEquipEventActive) return;
        core.getServer().getPluginManager().registerEvents(new ArmorListener(core), core);
        try {
            core.getServer().getPluginManager().registerEvents(new DispenserListener(), core);
        } catch (Throwable ignored) {}
        isArmorEquipEventActive = true;
    }
    public static void activatePermanentEffects() {
        if (isPermanentEffectsActive) return;
        core.getServer().getPluginManager().registerEvents(new PermanentEffectManager(), core);
        isPermanentEffectsActive = true;
    }
    public static void activateGuardians() {
        if (isGuardiansActive && GuardianCheck == null) return;
        core.getServer().getPluginManager().registerEvents(new GuardianManager(), core);
        GuardianCheck = Bukkit.getScheduler().runTaskTimer(AGMCore.getInstance(), new GuardianCheckTask(), 0, 60 * INTERVAL);
        isGuardiansActive = true;
    }
    public static void deactivateGuardians(boolean confirm) {
        if (!confirm || !isGuardiansActive || GuardianCheck == null) return;
        GuardianCheck.cancel();
        isGuardiansActive = false;
    }
    public static void activateDayCycleEvent() {
        if (isDayCycleActive && DayCycleCaller != null) return;
        DayCycleCaller = Bukkit.getScheduler().runTaskTimer(AGMCore.getInstance(), new DayCycleTask(), 0, INTERVAL);
        isDayCycleActive = true;
    }
    public static void deactivateDayCycleEvent(boolean confirm) {
        if (!confirm || DayCycleCaller == null) return;
        DayCycleCaller.cancel();
        isDayCycleActive = false;
    }
    public static void activateDoorEvent() {
        if (isDoorActive) return;
        core.getServer().getPluginManager().registerEvents(new DoorManager(), core);
        isDoorActive = true;
    }
}
