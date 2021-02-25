package me.ashenguard.api.utils.extra;

import java.util.*;
import java.util.function.Function;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class RandomCollection<E> {
    private final NavigableMap<Double, E> map = new TreeMap<>();
    private final Random random;
    private double total = 0;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public RandomCollection(Function<E, Double> weightFunction, Collection<E> items) {
        this(new Random(), weightFunction, items);
    }

    public RandomCollection(Random random, Function<E, Double> weightFunction, Collection<E> items) {
        this.random = random;
        for (E item: items) add(weightFunction.apply(item), item);
    }

    @SafeVarargs
    public final RandomCollection<E> addAll(Map.Entry<Double, E>... items) {
        for (Map.Entry<Double, E> item: items) add(item);
        return this;
    }

    public RandomCollection<E> add(Map.Entry<Double, E> item) {
        return add(item.getKey(), item.getValue());
    }

    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) return this;
        map.put(total, result);
        total += weight;
        return this;
    }

    public E next() {
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }
}