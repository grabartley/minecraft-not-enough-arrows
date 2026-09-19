package com.grahambartley.notenougharrows.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;

public final class StatusArrowImpact {
  public static final int BASE_AMPLIFIER = 0;

  private StatusArrowImpact() {}

  public static boolean lasts(final int durationTicks) {
    return durationTicks > 0;
  }

  public static boolean apply(
      final Entity struck,
      final RegistryEntry<StatusEffect> effect,
      final int durationTicks,
      final Entity source) {
    if (!(struck instanceof LivingEntity living) || !lasts(durationTicks)) {
      return false;
    }
    return living.addStatusEffect(
        new StatusEffectInstance(effect, durationTicks, BASE_AMPLIFIER), source);
  }
}
