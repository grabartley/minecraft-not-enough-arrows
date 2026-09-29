package com.grahambartley.notenougharrows.arrow;

import java.util.Objects;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public record ArrowEffect(Identifier effect, Delivery delivery) {

  public enum Delivery {
    STRUCK_TARGET,
    AREA
  }

  public ArrowEffect {
    Objects.requireNonNull(effect, "effect");
    Objects.requireNonNull(delivery, "delivery");
  }

  public static ArrowEffect onStruckTarget(final RegistryEntry<StatusEffect> effect) {
    return new ArrowEffect(idOf(effect), Delivery.STRUCK_TARGET);
  }

  public static ArrowEffect overArea(final RegistryEntry<StatusEffect> effect) {
    return new ArrowEffect(idOf(effect), Delivery.AREA);
  }

  private static Identifier idOf(final RegistryEntry<StatusEffect> effect) {
    Objects.requireNonNull(effect, "effect");
    return effect
        .getKey()
        .orElseThrow(() -> new IllegalArgumentException("Status effect is not registered"))
        .getValue();
  }
}
