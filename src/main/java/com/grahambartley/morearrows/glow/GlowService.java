package com.grahambartley.morearrows.glow;

import com.grahambartley.morearrows.server.ServerConfigService;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public final class GlowService {
  private static final int AMPLIFIER = 0;
  private static final boolean AMBIENT = false;
  private static final boolean SHOW_PARTICLES = false;
  private static final boolean SHOW_ICON = true;

  private GlowService() {}

  public static boolean mark(
      final ServerWorld world, @Nullable final Entity target, @Nullable final Entity source) {
    return mark(world, target, source, ServerConfigService.get().utility().glowDurationTicks());
  }

  public static boolean mark(
      final ServerWorld world,
      @Nullable final Entity target,
      @Nullable final Entity source,
      final int durationTicks) {
    if (world == null || durationTicks <= 0 || !(target instanceof LivingEntity living)) {
      return false;
    }
    return living.addStatusEffect(
        new StatusEffectInstance(
            StatusEffects.GLOWING, durationTicks, AMPLIFIER, AMBIENT, SHOW_PARTICLES, SHOW_ICON),
        source);
  }
}
