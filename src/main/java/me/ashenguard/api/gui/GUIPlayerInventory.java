package me.ashenguard.api.gui;

import me.ashenguard.api.messenger.PlaceholderManager;
import me.ashenguard.api.placeholder.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GUIPlayerInventory {
    private final GUIInventory parent;
    private final Player player;
    private final Inventory inventory;

    protected final Map<Integer, GUIInventorySlot> slots;
    protected final Set<Placeholder> placeholders = new HashSet<>();

    public GUIPlayerInventory(GUIInventory gui, Player player, Object... extras) {
        if (!gui.isLoaded()) gui.load();

        this.parent = gui;
        this.player = player;
        this.slots = gui.getSlotMapFor(player, extras);
        this.placeholders.addAll(gui.placeholders);

        this.inventory = Bukkit.createInventory(player, getSize(), getTitle());
    }

    public String getTitle() {
        return PlaceholderManager.translate(player, parent.title, placeholders);
    }

    public int getSize() {
        return parent.size * 9;
    }

    public void open() {
        this.design();
        this.player.openInventory(this.inventory);
    }

    public void design() {
        for (int i = 0; i < getSize(); i++) {
            GUIInventorySlot slot = slots.get(i);
            this.inventory.setItem(i, slot == null ? null : slot.getItem(this).getItem(player, placeholders));
        }
    }

    public boolean trigger(InventoryClickEvent event) {
        GUIInventorySlot slot = slots.get(event.getSlot());
        return slot == null || slot.runAction(this, event);
    }

    public Player getPlayer() {
        return player;
    }
    public Inventory getInventory() {
        return inventory;
    }
    public GUIInventory getParent() {
        return parent;
    }

    public Map<Integer, GUIInventorySlot> getSlotMap() {
        return slots;
    }
}
