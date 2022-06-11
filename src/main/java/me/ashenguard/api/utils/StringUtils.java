package me.ashenguard.api.utils;

import java.util.Collection;

public class StringUtils {
    /**
     * Will match the value in the list to see if it exists
     *
     * @param value     the value to match
     * @param list      the collection to look into
     * @param sensitive if the match is case sensitive
     * @return if the value is in the collection
     */
    public static boolean match(String value, Collection<String> list, boolean sensitive) {
        if (sensitive) for (String item : list) {
            if (item.equals(value)) return true;
        }
        else for (String item : list) {
            if (item.equalsIgnoreCase(value)) return true;
        }
        return false;
    }
}
