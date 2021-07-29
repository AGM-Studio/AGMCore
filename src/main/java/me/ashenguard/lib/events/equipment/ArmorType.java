package me.ashenguard.lib.events.equipment;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public enum ArmorType{
    HELMET(5, new HelmetMethod()),
    CHESTPLATE(6, new ChestplateMethod()),
    LEGGINGS(7, new LeggingsMethod()),
    BOOTS(8, new BootsMethod());

    private final int slot;
    private final SlotMethod method;

    public abstract static class SlotMethod {
        public abstract void set(PlayerInventory inv, ItemStack item);
        public abstract ItemStack get(PlayerInventory inv);
    }
    private static class HelmetMethod extends SlotMethod {
        @Override public void set(PlayerInventory inv, ItemStack item) {
            inv.setHelmet(item);
        }
        @Override public ItemStack get(PlayerInventory inv) {
            return inv.getHelmet();
        }
    }
    private static class ChestplateMethod extends SlotMethod {
        @Override public void set(PlayerInventory inv, ItemStack item) {
            inv.setChestplate(item);
        }
        @Override public ItemStack get(PlayerInventory inv) {
            return inv.getChestplate();
        }
    }
    private static class LeggingsMethod extends SlotMethod {
        @Override public void set(PlayerInventory inv, ItemStack item) {
            inv.setLeggings(item);
        }
        @Override public ItemStack get(PlayerInventory inv) {
            return inv.getLeggings();
        }
    }
    private static class BootsMethod extends SlotMethod {
        @Override public void set(PlayerInventory inv, ItemStack item) {
            inv.setBoots(item);
        }
        @Override public ItemStack get(PlayerInventory inv) {
            return inv.getBoots();
        }
    }

    ArmorType(int slot, SlotMethod method){
        this.slot = slot;
        this.method = method;
    }

    public static ArmorType matchType(final ItemStack item){
        if(item == null || item.getType().equals(Material.AIR)) return null;
        String type = item.getType().name();
        if(type.endsWith("_HELMET") || type.endsWith("_SKULL") || type.endsWith("_HEAD")) return HELMET;
        else if(type.endsWith("_CHESTPLATE") || type.equals("ELYTRA")) return CHESTPLATE;
        else if(type.endsWith("_LEGGINGS")) return LEGGINGS;
        else if(type.endsWith("_BOOTS")) return BOOTS;
        else return null;
    }

    public ItemStack getItemStack(HumanEntity player) {
        return method.get(player.getInventory());
    }
    public void setItemStack(HumanEntity player, ItemStack item) {
        method.set(player.getInventory(), item);
    }

    public int getSlot(){
        return slot;
    }
}