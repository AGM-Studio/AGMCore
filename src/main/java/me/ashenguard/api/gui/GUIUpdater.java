package me.ashenguard.api.gui;

@SuppressWarnings("unused")
public class GUIUpdater {
    private static GUIUpdater instance = null;
    public static GUIUpdater getInstance() {
        if (instance == null) instance = new GUIUpdater();
        return instance;
    }
    public static void setInstance(GUIUpdater instance) {
        if (GUIUpdater.instance != null) return;
        GUIUpdater.instance = instance;
    }

    public static void update(GUIInventory inventory) {
        instance.updateInventory(inventory);
    }

    protected void updateTitle(GUIInventory inventory) {
        // It's disabled as default (NO NMS POLICY), API extensions will activate it
    }
    protected void updateSlots(GUIInventory inventory) {
        inventory.design();
        inventory.getPlayer().updateInventory();
    }

    private void updateInventory(GUIInventory inventory) {
        updateSlots(inventory);
        updateTitle(inventory);
    }
}
