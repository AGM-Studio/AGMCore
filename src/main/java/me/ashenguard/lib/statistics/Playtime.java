package me.ashenguard.lib.statistics;

import me.ashenguard.agmcore.AGMCore;
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

    public static String getPlaceholderValue(OfflinePlayer player, String value) {
        AGMCore.getMessenger().Debug("Placeholders", "Placeholder has been requested.", String.format("Player= §6%s", player.getName()), String.format("Placeholder= §6{AGMCore_Playtime%s}", value));

        if (value.equalsIgnoreCase("_TOTAL")) {
            long days = Playtime.getPlaytime(player, TimeUnit.DAYS);
            long hours = Playtime.getPlaytime(player, TimeUnit.HOURS);
            long minutes = Playtime.getPlaytime(player, TimeUnit.MINUTES);
            return String.format("%d day%s, %d:%d", days, days > 1 ? "s" : "", hours, minutes);
        }

        long playtime = getPlaytime(player, value);
        if (playtime >= 0) return String.valueOf(playtime);
        return String.valueOf(Playtime.getPlaytime(player));
    }

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
