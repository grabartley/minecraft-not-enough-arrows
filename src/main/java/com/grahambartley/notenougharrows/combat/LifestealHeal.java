package com.grahambartley.notenougharrows.combat;

public final class LifestealHeal {

  private LifestealHeal() {}

  public static float amount(
      final double damageDealt,
      final float share,
      final float maxHealPerHit,
      final float currentHealth,
      final float maxHealth) {
    if (damageDealt <= 0.0 || share <= 0.0f || maxHealPerHit <= 0.0f) {
      return 0.0f;
    }

    final float headroom = maxHealth - currentHealth;
    if (headroom <= 0.0f) {
      return 0.0f;
    }

    final float earned = (float) (damageDealt * share);
    return Math.min(Math.min(earned, maxHealPerHit), headroom);
  }
}
