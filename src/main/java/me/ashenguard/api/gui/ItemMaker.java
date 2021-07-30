package me.ashenguard.api.gui;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTListCompound;
import me.ashenguard.api.messenger.PHManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"deprecation", "unused"})
public class ItemMaker {
    public static final ItemStack NULL = new ItemStack(Material.STONE);
    public static final String PLAYER_HEAD = "PLAYER_HEAD";
    public static final String CUSTOM_HEAD = "CUSTOM_HEAD";

    public final OfflinePlayer player;
    public final String id;

    public String value = null;
    public short data = 0;
    public UUID uuid = UUID.randomUUID();
    public String name = null;
    public List<String> lore = null;
    public boolean glow = false;
    public String amount = "1";

    public ItemMaker(OfflinePlayer player, String id) {
        this.player = player;
        this.id = id;
    }

    public ItemMaker(OfflinePlayer player, String id, String value, short data) {
        this.player = player;
        this.id = id;
        this.value = value;
        this.data = data;
    }

    public ItemStack createItem() {
        ItemStack item = createBareItem();
        return designItem(item);
    }

    public static ItemMaker fromConfig(OfflinePlayer player, ConfigurationSection section) {
        String ID = section.getString("Material.ID", null);
        String value = section.getString("Material.Value", null);
        short data = (short) section.getInt("Material.Data", section.getInt("Material.Value", 0));

        ItemMaker im = new ItemMaker(player, ID, value, data);

        String uuidString = section.getString("Material.UUID", "");
        try { im.uuid = UUID.fromString(uuidString); } catch (Throwable ignored) {}

        im.name = section.getString("Name", null);
        im.lore = section.isList("Lore") ? section.getStringList("Lore") : null;
        im.glow = section.getBoolean("Glow", false);
        im.amount = section.getString("Amount", "1");

        return im;
    }

    public static ItemStack designItem(ItemStack item, OfflinePlayer player, String name, List<String> lore, boolean glow, int amount) {
        return designItem(item, name == null ? null : PHManager.translate(player, name), lore == null ? null : PHManager.translate(player, lore), glow, amount);
    }

    public static ItemStack designItem(ItemStack item, String name, List<String> lore, boolean glow, int amount) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return item;
        if (name != null) itemMeta.setDisplayName(name);
        if (lore != null) itemMeta.setLore(lore);
        if (glow) {
            itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 0, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(itemMeta);
        item.setAmount(amount);

        return item;
    }

    private ItemStack designItem(ItemStack item) {
        return designItem(item, player, name, lore, glow, getItemAmount());
    }

    private int getItemAmount() {
        return getItemAmount(amount);
    }

    public static int getItemAmount(String amount) {
        Matcher randomize = Pattern.compile("\\d+-\\d+").matcher(amount);
        if (randomize.find()) {
            Matcher integer = Pattern.compile("\\d+").matcher(randomize.group());

            int min = integer.find() ? Integer.parseInt(integer.group()) : 1;
            int max = integer.find() ? Integer.parseInt(integer.group()) : 1;
            return new Random().nextInt(max + 1) + min;
        }
        Matcher integer = Pattern.compile("\\d+").matcher(amount);
        return integer.find() ? Integer.parseInt(integer.group()) : 1;
    }

    private ItemStack createBareItem() {
        if (id == null) return NULL.clone();
        return switch (id.toUpperCase()) {
            case PLAYER_HEAD -> getPlayerHead(player, value == null ? "self" : value);
            case CUSTOM_HEAD -> getCustomHead(uuid, value);
            default -> createSimpleItem(id, data);
        };
    }

    public static ItemStack createSimpleItem(String id) {
        return createSimpleItem(id, (short) 0);
    }
    public static ItemStack createSimpleItem(String id, short data) {
        Material material = XMaterial.matchXMaterial(id.toUpperCase()).orElse(XMaterial.STONE).parseMaterial();
        if (material == null) return NULL.clone();
        return XMaterial.isNewVersion() ? new ItemStack(material) : new ItemStack(material, 1, data);
    }
    public static ItemStack getCustomHead(String uuid, String value) {
        if (uuid == null) return getCustomHead((UUID) null, value);
        try {
            return getCustomHead(UUID.fromString(uuid), value);
        } catch (Throwable ignored) {
            return getCustomHead((UUID) null, value);
        }
    }
    public static ItemStack getCustomHead(UUID uuid, String value) {
        ItemStack item = XMaterial.PLAYER_HEAD.parseItem();
        if (item == null) return null;
        NBTItem nbt = new NBTItem(item, true);

        NBTCompound skull = nbt.addCompound("SkullOwner");
        skull.setString("Id", uuid == null ? UUID.randomUUID().toString() : uuid.toString());

        NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
        texture.setString("Value",  value);

        return item;
    }
    public static ItemStack getPlayerHead(OfflinePlayer player, String username) {
        ItemStack item = XMaterial.PLAYER_HEAD.parseItem();
        if (item == null) return null;

        OfflinePlayer target = username.equals("self") && player != null ? player : Bukkit.getOfflinePlayer(username);
        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        if (skullMeta == null) return item;
        skullMeta.setOwningPlayer(target);
        item.setItemMeta(skullMeta);

        return item;
    }
}
