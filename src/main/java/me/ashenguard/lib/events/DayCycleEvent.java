package me.ashenguard.lib.events;

import me.ashenguard.agmcore.AGMEvents;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class DayCycleEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Cycle cycle;
    private final World world;

    public static void activate() {
        AGMEvents.activateDayCycleEvent();
    }

    public DayCycleEvent(World world) {
        this.world = world;
        this.cycle = Cycle.getCycle(world);
    }

    @Override public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Cycle getCycle() {
        return cycle;
    }
    public World getWorld() {
        return world;
    }

    public enum Cycle { // 0 1000 6000 9000 12000 14000 18000 23000
        Sunrise(23000L, true),
        Morning(1000L, true),
        Noon(6000L, true),
        Afternoon(9000L, true),
        Sunset(12000L, true),
        Night(14000L, false),
        Midnight(18000L, false);

        private final long start;
        private final boolean day;
        Cycle(long start, boolean day) {
            this.start = start;
            this.day = day;
        }

        public long getPassedTime(World world) {
            return getPassedTime(world.getTime());
        }
        public long getPassedTime(long time) {
            return (time - start) % 24000L;
        }
        
        public static Cycle getCycle(World world) {
            return getCycle(world.getTime());
        }
        public static Cycle getCycle(long time) {
            if (time >= Midnight.start) return Midnight;
            if (time >= Night.start) return Night;
            if (time >= Sunset.start) return Sunset;
            if (time >= Afternoon.start) return Afternoon;
            if (time >= Noon.start) return Noon;
            if (time >= Morning.start) return Morning;
            return Sunrise;
        }

        public boolean isDay() {
            return day;
        }
    }
}
