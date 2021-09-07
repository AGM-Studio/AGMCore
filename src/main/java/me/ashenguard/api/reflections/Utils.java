package me.ashenguard.api.reflections;

import me.ashenguard.agmcore.AGMCore;

import java.lang.reflect.Field;

public class Utils {
    public static Object getField(String fieldName, Class<?> clazz, Object object) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch(Throwable throwable) {
            AGMCore.getMessenger().handleException(throwable);
        }
        return null;
    }
}
