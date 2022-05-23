package me.ashenguard.api.itemstack;

import me.ashenguard.api.itemstack.advanced.AdvancedItemStack;
import me.ashenguard.api.messenger.PHManager;
import me.ashenguard.api.versions.MCVersion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings({"unused"})
public class ItemMaker {
    public static ItemStack designItem(ItemStack item, OfflinePlayer player, String name, List<String> lore, boolean glow, int amount) {
        return designItem(item, player, name, lore, glow, amount, -1);
    }
    public static ItemStack designItem(ItemStack item, OfflinePlayer player, String name, List<String> lore, boolean glow, int amount, int customModelData) {
        return designItem(item, name == null ? null : PHManager.translate(player, name), lore == null ? null : PHManager.translate(player, lore), glow, amount, customModelData);
    }
    public static ItemStack designItem(ItemStack item, String name, List<String> lore, boolean glow, int amount) {
        return designItem(item, name, lore, glow, amount, -1);
    }
    public static ItemStack designItem(ItemStack item, String name, List<String> lore, boolean glow, int amount, int customModelData) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return item;
        if (name != null) itemMeta.setDisplayName(name);
        if (lore != null) itemMeta.setLore(lore);
        if (glow) {
            itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        if (customModelData >= 0 && !MCVersion.getMCVersion().isLowerThan(MCVersion.V1_14)) {
            itemMeta.setCustomModelData(customModelData);
        }
        item.setItemMeta(itemMeta);
        item.setAmount(amount);
        
        return item;
    }
    /*
    public static ItemStack getItemStack(OfflinePlayer player, String path) {
        return Optional.of(getItemStack(player, AGMCore.getGUIManager().getConfigurationSection(path))).orElse(NULL.clone());
    }
    */
    public static ItemStack getItemStack(OfflinePlayer player, @NotNull ConfigurationSection section) {
        AdvancedItemStack item = AdvancedItemStack.fromSection(section);
        return item == null ? null : item.getItem(player);
    }
    public static ItemStack getItemStack(@NotNull ItemStack itemStack, OfflinePlayer player, String name, List<String> lore) {
        return ItemMaker.getItemStack(itemStack, player, name, lore, false, -1);
    }
    public static ItemStack getItemStack(@NotNull ItemStack itemStack, OfflinePlayer player, String name, List<String> lore, boolean glow) {
        return ItemMaker.designItem(itemStack, player, name, lore, glow, 1, -1);
    }
    public static ItemStack getItemStack(@NotNull ItemStack itemStack, OfflinePlayer player, String name, List<String> lore, int customModelData) {
        return ItemMaker.designItem(itemStack, player, name, lore, false, 1, customModelData);
    }
    public static ItemStack getItemStack(@NotNull ItemStack itemStack, OfflinePlayer player, String name, List<String> lore, boolean glow, int customModelData) {
        return ItemMaker.designItem(itemStack, player, name, lore, glow, 1, customModelData);
    }
}
