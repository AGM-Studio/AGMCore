package me.ashenguard.lib.statistics;

import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Livetime {
    private final static List<String> MINUTE = Arrays.asList("_MIN", "_MINUTE", "_M");
    private final static List<String> HOUR = Arrays.asList("_HOUR", "_H");
    private final static List<String> DAYS = Arrays.asList("_DAY", "_D");

    public static long getLivetime(OfflinePlayer player) {
        return player.getStatistic(Statistic.TIME_SINCE_DEATH) / 60;
    }
    public static long getLivetime(OfflinePlayer player, TimeUnit unit) {
        return unit.convert(getLivetime(player), TimeUnit.MINUTES);
    }
    public static long getLivetime(OfflinePlayer player, String unit) {
        String value1 = unit.toUpperCase();
        if (MINUTE.contains(value1)) return Livetime.getLivetime(player, TimeUnit.MINUTES) % 60;
        if (HOUR.contains(value1)) return Livetime.getLivetime(player, TimeUnit.HOURS) % 24;
        if (DAYS.contains(value1)) return Livetime.getLivetime(player, TimeUnit.DAYS);
        if (value1.startsWith("_TOTAL")) {
            String value2 = value1.substring(6);
            if (MINUTE.contains(value2)) return Livetime.getLivetime(player, TimeUnit.MINUTES);
            if (HOUR.contains(value2)) return Livetime.getLivetime(player, TimeUnit.HOURS);
            if (DAYS.contains(value2)) return Livetime.getLivetime(player, TimeUnit.DAYS);
        }
        return -1;
    }
}
