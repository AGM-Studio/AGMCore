package me.ashenguard.api.utils;

import java.util.concurrent.Callable;

public class SafeCallable<V> {
    private final Callable<V> callable;
    private final V def;

    public SafeCallable(Callable<V> callable, V def) {
        this.callable = callable;
        this.def = def;
    }

    public V call() {
        try {
            return callable.call();
        } catch (Throwable ignored) {
            return def;
        }
    }
}
