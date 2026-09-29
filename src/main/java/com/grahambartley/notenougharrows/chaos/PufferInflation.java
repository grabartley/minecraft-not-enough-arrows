package com.grahambartley.notenougharrows.chaos;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

public final class PufferInflation {
  public static final Identifier MODIFIER_ID =
      Identifier.of(NotEnoughArrows.MOD_ID, "puffer_inflation");
  public static final double KNOCKBACK_MULTIPLIER = 2.0;

  private static final double COLLISION_MARGIN = 1.0E-7;

  private PufferInflation() {}

  public static Optional<Double> inflate(final LivingEntity target) {
    if (target == null || !target.isAlive()) {
      return Optional.empty();
    }
    final EntityAttributeInstance scale = scaleOf(target);
    if (scale == null || scale.hasModifier(MODIFIER_ID)) {
      return Optional.empty();
    }
    final Optional<Double> factor =
        InflationSteps.largestThatFits(candidate -> fitsWhereItStands(target, candidate));
    factor.ifPresent(
        chosen ->
            scale.addTemporaryModifier(
                new EntityAttributeModifier(
                    MODIFIER_ID,
                    chosen - 1.0,
                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
    return factor;
  }

  public static boolean deflate(final LivingEntity target) {
    final EntityAttributeInstance scale = scaleOf(target);
    return scale != null && scale.removeModifier(MODIFIER_ID);
  }

  public static boolean isInflated(final LivingEntity target) {
    final EntityAttributeInstance scale = scaleOf(target);
    return scale != null && scale.hasModifier(MODIFIER_ID);
  }

  public static double knockbackFor(final LivingEntity target, final double strength) {
    return isInflated(target) ? strength * KNOCKBACK_MULTIPLIER : strength;
  }

  static boolean fitsWhereItStands(final LivingEntity target, final double factor) {
    final Box inflated =
        target.getDimensions(target.getPose()).scaled((float) factor).getBoxAt(target.getPos());
    return target.getWorld().isSpaceEmpty(target, inflated.contract(COLLISION_MARGIN));
  }

  private static EntityAttributeInstance scaleOf(final LivingEntity target) {
    return target == null ? null : target.getAttributeInstance(EntityAttributes.GENERIC_SCALE);
  }
}
