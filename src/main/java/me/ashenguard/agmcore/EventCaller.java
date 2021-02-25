package me.ashenguard.agmcore;

import me.ashenguard.lib.events.DayCycleEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public class EventCaller implements Listener {
    private static final int INTERVAL = 20;
    private static class DayCycleTask implements Runnable {
        @Override public void run() {
            for (World world: Bukkit.getServer().getWorlds()) {
                long delay = DayCycleEvent.Cycle.getCycle(world).getPassedTime(world);
                if (delay < INTERVAL) Bukkit.getServer().getPluginManager().callEvent(new DayCycleEvent(world));
            }
        }
    }
    public static BukkitTask DayCycleCaller = Bukkit.getScheduler()
            .runTaskTimer(AGMCore.getInstance(), new DayCycleTask(), 0, INTERVAL);

    public static void start() {}
    public static void stop(boolean confirm) {
        if (!confirm) return;
        DayCycleCaller.cancel();
    }
}
