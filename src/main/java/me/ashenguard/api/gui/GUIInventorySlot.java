package me.ashenguard.api.gui;

import me.ashenguard.api.itemstack.placeholder.PlaceholderItemStack;
import me.ashenguard.api.utils.Pair;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("UnusedReturnValue")
public class GUIInventorySlot {
    private Function<InventoryClickEvent, Boolean> action = null;
    private final List<PlaceholderItemStack> items = new ArrayList<>();

    public final int slot;

    public GUIInventorySlot(int slot) {
        this.slot = slot;
    }

    public GUIInventorySlot setAction(Function<InventoryClickEvent, Boolean> action) {
        this.action = action;
        return this;
    }
    public boolean runAction(InventoryClickEvent event) {
        if (action == null) return true;
        return action.apply(event);
    }

    public GUIInventorySlot setItems(Collection<Pair<PlaceholderItemStack, Integer>> items) {
        if (this.items.size() > 0) this.items.clear();
        for (Pair<PlaceholderItemStack, Integer> pair: items)
            this.items.addAll(Collections.nCopies(pair.getValue(), pair.getKey()));

        return this;
    }

    public GUIInventorySlot addItems(Collection<Pair<PlaceholderItemStack, Integer>> items) {
        for (Pair<PlaceholderItemStack, Integer> pair: items)
            this.items.addAll(Collections.nCopies(pair.getValue(), pair.getKey()));

        return this;
    }

    public GUIInventorySlot addItem(PlaceholderItemStack item, int count) {
        this.items.addAll(Collections.nCopies(count, item));

        return this;
    }

    public GUIInventorySlot addItem(PlaceholderItemStack item) {
        this.items.add(item);

        return this;
    }


    public GUIInventorySlot withOffset(int offset) {
        offset = Math.min(this.items.size(), offset);
        Collections.rotate(items, offset);
        return this;
    }

    public void update(GUIInventory guiInventory, int tick) {
        ItemStack item = items.get(tick % items.size()).getItem(guiInventory.getPlayer());
        guiInventory.inventory.setItem(slot, item);
    }
}
