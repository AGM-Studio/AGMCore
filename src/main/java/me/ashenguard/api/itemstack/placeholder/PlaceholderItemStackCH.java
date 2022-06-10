package me.ashenguard.api.itemstack.placeholder;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import me.ashenguard.api.Configuration;
import me.ashenguard.api.utils.SafeCallable;
import me.ashenguard.api.versions.MCVersion;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlaceholderItemStackCH extends PlaceholderItemStack {
    protected final String texture;
    protected final UUID uuid;

    private static UUID safeUUIDFromString(String string) {
        try {
            return UUID.fromString(string);
        } catch (IllegalArgumentException exception) {
            return UUID.randomUUID();
        }
    }

    public PlaceholderItemStackCH(String texture, UUID uuid, String name, List<String> lore, boolean glow, SafeCallable<Integer> amount) {
        super(name, lore, glow, amount);

        this.uuid = uuid;
        this.texture = texture;
    }
    public PlaceholderItemStackCH(String texture, String uuid, String name, List<String> lore, boolean glow, SafeCallable<Integer> amount) {
        this(texture, safeUUIDFromString(uuid), name, lore, glow, amount);
    }
    
    public static PlaceholderItemStackCH fromSection(ConfigurationSection section) {
        return new PlaceholderItemStackCH(
                Configuration.getString(section, Arrays.asList("CustomHead", "Material.Texture", "Material.Value"), null),
                Configuration.getString(section, Arrays.asList("UUID", "Material.UUID"), ""),
                section.getString("Name", null),
                section.isList("Lore") ? section.getStringList("Lore") : null,
                section.getBoolean("Glow", false),
                PlaceholderItemStack.amountFromString(section.getString("Amount"))
        );
    }

    @Override
    public @NotNull ItemStack getBasicItem() {
        ItemStack item = Optional.ofNullable(XMaterial.PLAYER_HEAD.parseItem()).orElse(new ItemStack(Material.PLAYER_HEAD));
        NBTItem nbt = new NBTItem(item, true);

        NBTCompound skull = nbt.addCompound("SkullOwner");
        if (MCVersion.getMCVersion().isLowerThan(MCVersion.V1_16))
            skull.setString("Id", uuid == null ? UUID.randomUUID().toString() : uuid.toString());
        else
            skull.setUUID("Id", uuid == null ? UUID.randomUUID() : uuid);

        skull.addCompound("Properties").getCompoundList("textures").addCompound().setString("Value", texture);
        return item;
    }
}
