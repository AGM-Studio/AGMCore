package me.ashenguard.api.gui;

import me.ashenguard.api.itemstack.placeholder.PlaceholderItemStack;
import me.ashenguard.api.utils.Pair;
import me.ashenguard.utils.TriConsumer;
import me.ashenguard.utils.TriFunction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.*;

public class GUIInventorySlot {
    protected final String key;
    protected final Integer slot;
    private int offset = 0;

    protected Action action = (i, e, a) -> true;
    protected Check check = i -> false;

    protected final List<PlaceholderItemStack> items = new ArrayList<>();
    protected final List<PlaceholderItemStack> alts = new ArrayList<>();

    public GUIInventorySlot(String key, int slot) {
        this.key = key;
        this.slot = slot;
    }

    public GUIInventorySlot setItems(Collection<Pair<PlaceholderItemStack, Integer>> items) {
        if (this.items.size() > 0) this.items.clear();
        for (Pair<PlaceholderItemStack, Integer> pair: items)
            this.items.addAll(Collections.nCopies(pair.getValue(), pair.getKey()));

        return this;
    }
    public GUIInventorySlot setAltItems(Collection<Pair<PlaceholderItemStack, Integer>> items) {
        if (this.alts.size() > 0) this.alts.clear();
        for (Pair<PlaceholderItemStack, Integer> pair: items)
            this.alts.addAll(Collections.nCopies(pair.getValue(), pair.getKey()));

        return this;
    }

    public GUIInventorySlot addItems(Collection<Pair<PlaceholderItemStack, Integer>> items) {
        for (Pair<PlaceholderItemStack, Integer> pair: items)
            this.items.addAll(Collections.nCopies(pair.getValue(), pair.getKey()));

        return this;
    }
    public GUIInventorySlot addAltItems(Collection<Pair<PlaceholderItemStack, Integer>> items) {
        for (Pair<PlaceholderItemStack, Integer> pair: items)
            this.alts.addAll(Collections.nCopies(pair.getValue(), pair.getKey()));

        return this;
    }

    public GUIInventorySlot addItem(PlaceholderItemStack item, int count) {
        this.items.addAll(Collections.nCopies(count, item));

        return this;
    }
    public GUIInventorySlot addAltItem(PlaceholderItemStack item, int count) {
        this.alts.addAll(Collections.nCopies(count, item));

        return this;
    }

    public GUIInventorySlot addItem(PlaceholderItemStack item) {
        this.items.add(item);

        return this;
    }
    public GUIInventorySlot addAltItem(PlaceholderItemStack item) {
        this.alts.add(item);

        return this;
    }

    public GUIInventorySlot withOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public PlaceholderItemStack getItem(GUIPlayerInventory inventory) {
        List<PlaceholderItemStack> list = check != null && check.apply(inventory) ? alts : items;
        return list.get((GUIManager.getTick() + offset) % list.size());
    }

    public void setAction(@NotNull Action action) {
        this.action = action;
    }
    public void setAltCheck(@NotNull Check check) {
        this.check = check;
    }
    public boolean runAction(GUIPlayerInventory inventory, InventoryClickEvent event) {
        return this.action.apply(inventory, event, check != null && check.apply(inventory));
    }

    public interface Action extends TriFunction<GUIPlayerInventory, InventoryClickEvent, Boolean, Boolean> {
        static Action fromFunction(Function<InventoryClickEvent, Boolean> function) {
            return (i, e, a) -> function.apply(e);
        }
        static Action fromBiFunction(BiFunction<GUIPlayerInventory, InventoryClickEvent, Boolean> function) {
            return (i, e, a) -> function.apply(i, e);
        }
        static Action fromBiFunctionAlt(BiFunction<InventoryClickEvent, Boolean, Boolean> function) {
            return (i, e, a) -> function.apply(e, a);
        }
        static Action fromConsumer(Consumer<InventoryClickEvent> consumer) {
            return (i, e, a) -> {
                consumer.accept(e);
                return true;
            };
        }
        static Action fromConsumer(BiConsumer<GUIPlayerInventory, InventoryClickEvent> consumer) {
            return (i, e, a) -> {
                consumer.accept(i, e);
                return true;
            };
        }
        static Action fromConsumerAlt(BiConsumer<InventoryClickEvent, Boolean> consumer) {
            return (i, e, a) -> {
                consumer.accept(e, a);
                return true;
            };
        }
        static Action fromConsumer(TriConsumer<GUIPlayerInventory, InventoryClickEvent, Boolean> consumer) {
            return (i, e, a) -> {
                consumer.accept(i, e, a);
                return true;
            };
        }
    }
    public interface Check extends Function<GUIPlayerInventory, Boolean> {
        static Check fromSupplier(Supplier<Boolean> supplier) {
            return i -> supplier.get();
        }
    }
}
