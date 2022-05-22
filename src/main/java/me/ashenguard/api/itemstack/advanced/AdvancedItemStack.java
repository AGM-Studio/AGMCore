package me.ashenguard.api.itemstack.advanced;

import me.ashenguard.api.messenger.PHManager;
import me.ashenguard.api.utils.SafeCallable;
import me.ashenguard.api.versions.MCVersion;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AdvancedItemStack {
    protected static final ItemStack NULL_ITEM = new ItemStack(Material.STONE);
    protected static AdvancedItemStack NULL = null;

    protected static final String PLAYER_HEAD = "Player_Head";
    protected static final String CUSTOM_HEAD = "Custom_Head";

    protected static SafeCallable<Integer> amountFromString(String value) {
        try {
            Matcher randomize = Pattern.compile("\\d+-\\d+").matcher(value);
            if (randomize.find()) {
                Matcher integer = Pattern.compile("\\d+").matcher(randomize.group());

                int min = integer.find() ? Integer.parseInt(integer.group()) : 1;
                int max = integer.find() ? Integer.parseInt(integer.group()) : 1;
                return new SafeCallable<>(() -> new Random().nextInt(max + 1) + min, min);
            }
            Matcher integer = Pattern.compile("\\d+").matcher(value);
            int amount = integer.find() ? Integer.parseInt(integer.group()) : 1;
            return new SafeCallable<>(() -> amount, amount);
        } catch (Throwable ignored) {
            return ONE;
        }
    }
    protected static final SafeCallable<Integer> ONE = new SafeCallable<>(() -> 1, 1);

    private final String name;
    private final List<String> lore;
    private final SafeCallable<Integer> amount;
    private final boolean glow;
    private final int cmd;

    protected ItemStack basic = null;

    public ItemStack getItem() {
        return getItem(null);
    }
    public ItemStack getItem(OfflinePlayer player) {
        if (basic == null) createBasicItem();

        ItemStack item = basic.clone();
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return item;
        if (name != null) itemMeta.setDisplayName(PHManager.translate(player, name));
        if (lore != null) itemMeta.setLore(PHManager.translate(player, lore));
        item.setItemMeta(itemMeta);
        item.setAmount(amount.call());
        return item;
    }
    protected abstract @NotNull ItemStack getBasicItem();
    protected void createBasicItem() {
        try {
            basic = getBasicItem();
            ItemMeta itemMeta = basic.getItemMeta();
            if (glow && itemMeta != null) {
                itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            if (cmd >= 0 && !MCVersion.getMCVersion().isLowerThan(MCVersion.V1_14) && itemMeta != null) {
                itemMeta.setCustomModelData(cmd);
            }
            basic.setItemMeta(itemMeta);
        } catch (Throwable ignored) {
            basic = NULL_ITEM.clone();
        }
    }

    protected AdvancedItemStack(String name, List<String> lore, boolean glow, SafeCallable<Integer> amount, int cmd) {
        this.name = name;
        this.lore = lore;
        this.amount = amount;
        this.glow = glow;
        this.cmd = cmd;
    }

    protected AdvancedItemStack(String name, List<String> lore, boolean glow, SafeCallable<Integer> amount) {
        this(name, lore, glow, amount, -1);
    }

    public static @Nullable AdvancedItemStack fromSection(ConfigurationSection section) {
        if (section == null) return null;
        String id = section.getString("Material.ID", null);
        if (id == null) return null;
        return switch (id.toUpperCase()) {
            case PLAYER_HEAD -> AdvancedItemStackPH.fromSection(section);
            case CUSTOM_HEAD -> AdvancedItemStackCH.fromSection(section);
            default -> AdvancedItemStackBare.fromSection(section);
        };
    }
    public static @NotNull AdvancedItemStack nullItem() {
        if (NULL == null) NULL = new AdvancedItemStackBare(Material.STONE);
        return NULL;
    }
}
