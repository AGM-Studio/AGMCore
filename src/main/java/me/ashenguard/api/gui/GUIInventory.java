package me.ashenguard.api.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public abstract class GUIInventory {
    public String title;
    protected Inventory inventory;
    protected Player player;
    protected GUI GUI;

    protected GUIInventory(GUI GUI, String title, Player player, Inventory inventory) {
        this.GUI = GUI;
        this.title = title;
        this.player = player;
        this.inventory = inventory;

        GUI.saveGUIInventory(player, this);
    }

    protected GUIInventory(GUI GUI, String title, Player player, int size) {
        this(GUI, title, player, Bukkit.createInventory(player, size, title));
    }

    public void show() {
        player.openInventory(inventory);
    }

    public void close() {
        player.closeInventory();
        GUI.removeGUIInventory(player);
    }

    public void reload() {
        design();
    };

    protected abstract void design();
    public abstract void click(InventoryClickEvent event);
}
