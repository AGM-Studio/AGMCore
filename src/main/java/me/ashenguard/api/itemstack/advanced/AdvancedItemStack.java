package me.ashenguard.api.itemstack.advanced;

import me.ashenguard.api.messenger.PHManager;
import me.ashenguard.api.utils.SafeCallable;
import me.ashenguard.api.versions.MCVersion;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
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

    public final String name;
    public final List<String> lore;
    public final SafeCallable<Integer> amount;
    public final boolean glow;
    public final int cmd;
    public final Map<NamespacedKey, Integer> enchants;

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
        for (Map.Entry<NamespacedKey, Integer> entry: enchants.entrySet()) {
            Enchantment enchantment = Enchantment.getByKey(entry.getKey());
            if (enchantment != null) itemMeta.addEnchant(enchantment, entry.getValue() < 0 ? enchantment.getMaxLevel() : entry.getValue(), true);
        }
        item.setItemMeta(itemMeta);
        item.setAmount(amount.call());

        applyModifier(item, this);
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

    protected AdvancedItemStack(String name, List<String> lore, boolean glow, SafeCallable<Integer> amount, int cmd, Map<NamespacedKey, Integer> enchants) {
        this.name = name;
        this.lore = lore;
        this.amount = amount;
        this.glow = glow;
        this.cmd = cmd;
        this.enchants = enchants;
    }

    protected AdvancedItemStack(String name, List<String> lore, boolean glow, SafeCallable<Integer> amount) {
        this(name, lore, glow, amount, -1, new HashMap<>());
    }

    public static @Nullable AdvancedItemStack fromSection(ConfigurationSection section) {
        if (section == null) return null;
        String id = section.getString("Material.ID", section.getString("Material", null));
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

    private static final List<BiConsumer<ItemStack, AdvancedItemStack>> modifiers = new ArrayList<>();

    private static void applyModifier(ItemStack item, AdvancedItemStack advanced) {
        if (item == null) return;
        for (BiConsumer<ItemStack, AdvancedItemStack> modifier: modifiers) modifier.accept(item, advanced);
    }

    public static void addModifier(@NotNull BiConsumer<ItemStack, AdvancedItemStack> modifier) {
        modifiers.add(modifier);
    }
}
