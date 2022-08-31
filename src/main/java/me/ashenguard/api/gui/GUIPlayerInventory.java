package me.ashenguard.api.gui;

import me.ashenguard.api.messenger.PlaceholderManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Map;

public class GUIPlayerInventory {
    private final GUIInventory parent;
    private final Player player;
    private final Inventory inventory;

    private final Map<Integer, GUIInventorySlot> slots;

    public GUIPlayerInventory(GUIInventory gui, Player player, Object... extras) {
        if (gui.isLoaded()) gui.load();

        this.parent = gui;
        this.player = player;
        this.slots = gui.getSlotMapFor(player, extras);

        this.inventory = Bukkit.createInventory(player, getSize(), getTitle());
    }

    public String getTitle() {
        return PlaceholderManager.translate(player, parent.title);
    }

    public int getSize() {
        return inventory.getSize();
    }

    public void open() {
        this.design();
        this.player.openInventory(this.inventory);
    }

    public void design() {
        for (int i = 0; i < parent.size * 9; i++) {
            GUIInventorySlot slot = slots.get(i);
            if (slot == null) continue;

            this.inventory.setItem(i, slot.getItem().getItem(player, parent.placeholders));
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
