package me.ashenguard.lib.statistics;

import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Playtime implements Listener {
    private final static List<String> MINUTE = Arrays.asList("_MIN", "_MINUTE", "_M");
    private final static List<String> HOUR = Arrays.asList("_HOUR", "_H");
    private final static List<String> DAYS = Arrays.asList("_DAY", "_D");

    public static long getPlaytime(OfflinePlayer player) {
        return player.getStatistic(Statistic.PLAY_ONE_MINUTE);
    }
    public static long getPlaytime(OfflinePlayer player, TimeUnit unit) {
        return unit.convert(getPlaytime(player), TimeUnit.MINUTES);
    }
    public static long getPlaytime(OfflinePlayer player, String unit) {
        String value1 = unit.toUpperCase();
        if (MINUTE.contains(value1)) return Playtime.getPlaytime(player, TimeUnit.MINUTES) % 60;
        if (HOUR.contains(value1)) return Playtime.getPlaytime(player, TimeUnit.HOURS) % 24;
        if (DAYS.contains(value1)) return Playtime.getPlaytime(player, TimeUnit.DAYS);
        if (value1.startsWith("_TOTAL")) {
            String value2 = value1.substring(6);
            if (MINUTE.contains(value2)) return Playtime.getPlaytime(player, TimeUnit.MINUTES);
            if (HOUR.contains(value2)) return Playtime.getPlaytime(player, TimeUnit.HOURS);
            if (DAYS.contains(value2)) return Playtime.getPlaytime(player, TimeUnit.DAYS);
        }
        return -1;
    }
}
