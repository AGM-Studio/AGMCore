package me.ashenguard.lib.statistics;

import me.ashenguard.agmcore.AGMCore;
import me.ashenguard.api.utils.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Playtime implements Listener {
    private final static List<String> TICKS = Arrays.asList("T", "TICK", "TICKS");
    private final static List<String> MICRO = Arrays.asList("MICRO", "MICROS", "MICROSECOND", "MICROSECONDS");
    private final static List<String> SECOND = Arrays.asList("S", "SEC", "SECS", "SECOND", "SECONDS");
    private final static List<String> MINUTE = Arrays.asList("M", "MIN", "MINS", "MINUTE", "MINUTES");
    private final static List<String> HOUR = Arrays.asList("H", "HOUR", "HOURS");
    private final static List<String> DAYS = Arrays.asList("D", "DAY", "DAYS");

    /**
     * Return the result of placeholder. <br><br>
     * %AGMCore_Playtime%             Return time in ticks        (Always overflow) <br>
     * <br>
     * %AGMCore_Playtime_Tick%        Return time in ticks        (No overflow - Add "_Total" to make it overflow) <br>
     * %AGMCore_Playtime_Micro%       Return time in microseconds (No overflow - Add "_Total" to make it overflow) <br>
     * %AGMCore_Playtime_Second%      Return time in seconds      (No overflow - Add "_Total" to make it overflow) <br>
     * %AGMCore_Playtime_Minute%      Return time in minutes      (No overflow - Add "_Total" to make it overflow) <br>
     * %AGMCore_Playtime_Hour%        Return time in hours        (No overflow - Add "_Total" to make it overflow) <br>
     * %AGMCore_Playtime_Day%         Return time in days  <br>
     * <br>
     * %AGMCore_Playtime_Total%       Return time with format     (5 days, 10:18:25) <br>
     *
     * @param player the player of placeholder
     * @param value  value of placeholder
     * @return the placeholder
     */
    @SuppressWarnings("DuplicatedCode")
    public static String getPlaceholderValue(OfflinePlayer player, String value) {
        AGMCore.getMessenger().debug("Placeholders", "Placeholder has been requested.", String.format("Player= §6%s", player.getName()), String.format("Placeholder= §6{AGMCore_Playtime%s}", value));

        if (value == null || value.length() < 1) return String.valueOf(getPlaytime(player));
        else if (value.equalsIgnoreCase("_TOTAL")) {
            long duration = Playtime.getPlaytime(player);
            
            long days = convert(duration, TimeUnit.DAYS, false);
            long hours = convert(duration, TimeUnit.HOURS, false);
            long minutes = convert(duration, TimeUnit.MINUTES, false);
            long seconds = convert(duration, TimeUnit.SECONDS, false);
            
            if (days > 0)
                return String.format("%d day%s, %d:%d:%d", days, days > 1 ? "s" : "", hours, minutes, seconds);
            else 
                return String.format("%d:%d:%d", hours, minutes, seconds);
        }

        boolean overflow = value.toUpperCase().endsWith("_TOTAL");
        int start = value.startsWith("_") ? 1 : 0;
        int index = value.indexOf("_", start);
        String unit = index > 0 ? value.substring(start, index) : value.substring(start);

        TimeUnit timeUnit;
        if (StringUtils.match(unit, MICRO, false)) timeUnit = TimeUnit.MICROSECONDS;
        else if (StringUtils.match(unit, SECOND, false)) timeUnit = TimeUnit.SECONDS;
        else if (StringUtils.match(unit, MINUTE, false)) timeUnit = TimeUnit.MINUTES;
        else if (StringUtils.match(unit, HOUR, false)) timeUnit = TimeUnit.MICROSECONDS;
        else if (StringUtils.match(unit, DAYS, false)) timeUnit = TimeUnit.MICROSECONDS;
        else if (StringUtils.match(unit, TICKS, false)) timeUnit = null;
        else return "INVALID_FORMAT";

        long playtime = Playtime.getPlaytime(player, timeUnit, overflow);
        return String.valueOf(playtime);
    }

    /**
     * @param player whose playtime is requested
     * @return the playtime in TICKS
     */
    public static long getPlaytime(OfflinePlayer player) {
        return player.getStatistic(Statistic.PLAY_ONE_MINUTE);
    }

    /**
     * @param player   whose playtime is requested
     * @param unit     the unit you want the playtime in
     * @param overflow if set to true it will allow time unit overflow
     * @return the playtime in the unit requested
     */
    public static long getPlaytime(OfflinePlayer player, TimeUnit unit, boolean overflow) {
        return convert(getPlaytime(player), unit, overflow);
    }

    /**
     * Converts time units
     *
     * @param duration the time in ticks
     * @param unit     the target unit
     * @param overflow if it should allow overflow
     * @return the conversion result
     */
    private static long convert(long duration, TimeUnit unit, boolean overflow) {
        if (unit == null)
            if (overflow) return duration;
            else return duration % 20;

        long result = unit.convert(duration / 20, TimeUnit.SECONDS);
        if (overflow) return result;
        return switch (unit) {
            case DAYS -> result;
            case HOURS -> result % 24;
            case SECONDS, MINUTES -> result % 60;
            default -> result % 1000;
        };
    }
}
