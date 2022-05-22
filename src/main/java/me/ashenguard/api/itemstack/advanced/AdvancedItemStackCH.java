package me.ashenguard.api.itemstack.advanced;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import me.ashenguard.api.utils.SafeCallable;
import me.ashenguard.api.versions.MCVersion;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AdvancedItemStackCH extends AdvancedItemStack {
    private final String texture;
    private final UUID uuid;

    private static UUID safeUUIDFromString(String string) {
        try {
            return UUID.fromString(string);
        } catch (IllegalArgumentException exception) {
            return UUID.randomUUID();
        }
    }

    public AdvancedItemStackCH(String texture, UUID uuid, String name, List<String> lore, boolean glow, SafeCallable<Integer> amount) {
        super(name, lore, glow, amount);

        this.uuid = uuid;
        this.texture = texture;
    }
    
    public static AdvancedItemStackCH fromSection(ConfigurationSection section) {
        return new AdvancedItemStackCH(
                section.getString("Material.Texture", section.getString("Material.Value", null)),
                safeUUIDFromString(section.getString("Material.UUID", "")),
                section.getString("Name", null),
                section.isList("Lore") ? section.getStringList("Lore") : null,
                section.getBoolean("Glow", false),
                AdvancedItemStack.amountFromString(section.getString("Amount"))
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
