package me.ashenguard.agmcore;

import me.ashenguard.lib.spigot.PermanentEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class PermanentEffectManager implements Listener {
    private final static List<PermanentEffect> PERMANENT_EFFECTS = new ArrayList<>();

    public static void addEffect(PermanentEffect effect) {
        if (PERMANENT_EFFECTS.contains(effect)) return;
        PERMANENT_EFFECTS.add(effect);
    }
    public static void removeEffect(PermanentEffect effect) {
        PERMANENT_EFFECTS.remove(effect);
    }

    @EventHandler
    public void onEffectRemove(EntityPotionEffectEvent event) {
        if (PERMANENT_EFFECTS.size() == 0) return;
        PotionEffect oldEffect = event.getOldEffect();
        PotionEffect newEffect = event.getNewEffect();
        PotionEffectType type = oldEffect != null ? oldEffect.getType() : newEffect != null ? newEffect.getType() : null;

        for (PermanentEffect permanentEffect:PERMANENT_EFFECTS) {
            if (!permanentEffect.getEffectType().equals(type)) continue;
            permanentEffect.effect((LivingEntity) event.getEntity());
        }
    }
}
