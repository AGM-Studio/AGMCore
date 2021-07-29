package me.ashenguard.api.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

@SuppressWarnings("unused")
public abstract class GUIInventory {
    public final String title;
    protected final Inventory inventory;
    protected final Player player;
    protected final GUI GUI;
    protected final boolean inventoryOnly;
    protected final boolean cancelAlways;

    protected GUIInventory(GUI GUI, String title, Player player, Inventory inventory, boolean inventoryOnly, boolean cancelAlways) {
        this.GUI = GUI;
        this.title = title;
        this.player = player;
        this.inventory = inventory;
        this.inventoryOnly = inventoryOnly;
        this.cancelAlways = cancelAlways;
    }

    protected GUIInventory(GUI GUI, String title, Player player, Inventory inventory) {
        this(GUI, title, player, inventory, true, true);
    }

    protected GUIInventory(GUI GUI, String title, Player player, int size, boolean inventoryOnly, boolean cancelAlways) {
        this(GUI, title, player, Bukkit.createInventory(player, size, title), inventoryOnly, cancelAlways);
    }

    protected GUIInventory(GUI GUI, String title, Player player, int size) {
        this(GUI, title, player, Bukkit.createInventory(player, size, title));
    }

    public void show() {
        reload();
        player.openInventory(inventory);
        GUI.saveGUIInventory(player, this);
        GUI.messenger.Debug("GUI", "New inventory detected", "Player= §6" + player.getName(), "Inventory= §6" + title);
    }

    public void close() {
        player.closeInventory();
        GUI.removeGUIInventory(player);
    }

    public void reload() {
        design();
    }

    protected abstract void design();
    public abstract void click(InventoryClickEvent event);
}
