package me.ashenguard.lib.spigot;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

@SuppressWarnings({"unused"})
public class PermanentEffect {
    private final PotionEffectType effectType;
    private final boolean immunity;
    private final HashMap<LivingEntity, Integer> map = new HashMap<>();

    public PermanentEffect(PotionEffectType effectType) {
        this(effectType, false);
    }

    public PermanentEffect(PotionEffectType effectType, boolean immunity) {
        this.effectType = effectType;
        this.immunity = immunity;
    }

    public void addEntity(LivingEntity entity, int amplifier) {
        map.put(entity, amplifier);

        effect(entity);
        PermanentEffectManager.addEffect(this);
    }

    public void removeEntity(LivingEntity entity) {
        map.remove(entity);

        if (!isImmunity()) entity.removePotionEffect(effectType);
        if (map.size() == 0) PermanentEffectManager.removeEffect(this);
    }

    public boolean checkConditions() {
        return true;
    }

    public PotionEffectType getEffectType() {
        return effectType;
    }

    public boolean isImmunity() {
        return immunity;
    }

    public void effect(LivingEntity entity) {
        int amplifier = map.getOrDefault(entity, 0);
        if (amplifier == 0 || !checkConditions()) return;

        PotionEffect currentEffect = entity.getPotionEffect(effectType);

        if (isImmunity()) {
            if (currentEffect == null || currentEffect.getAmplifier() > amplifier) return;
            entity.removePotionEffect(effectType);
        } else {
            if (currentEffect != null && currentEffect.getAmplifier() > amplifier) return;
            if (currentEffect != null && currentEffect.getAmplifier() == amplifier && currentEffect.getDuration() > Integer.MAX_VALUE / 2) return;

            PotionEffect effect = new PotionEffect(effectType, Integer.MAX_VALUE, amplifier, false, false, false);
            entity.addPotionEffect(effect);
        }
    }
}
