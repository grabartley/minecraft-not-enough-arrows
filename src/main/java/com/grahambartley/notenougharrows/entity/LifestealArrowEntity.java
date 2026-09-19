package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.combat.LifestealHeal;
import com.grahambartley.notenougharrows.config.LifestealArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LifestealArrowEntity extends BaseArrowEntity {
  private double healthBeforeHit;

  public LifestealArrowEntity(
      final EntityType<? extends LifestealArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public LifestealArrowEntity(
      final EntityType<? extends LifestealArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    healthBeforeHit = poolOf(entityHitResult.getEntity());
    return ArrowImpact.DEFAULT;
  }

  @Override
  protected void afterArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    final double dealt = healthBeforeHit - poolOf(entityHitResult.getEntity());
    if (!(shooter().orElse(null) instanceof LivingEntity shooter)) {
      return;
    }

    final LifestealArrowConfig lifesteal = ServerConfigService.get().combat().lifesteal();
    final float healed =
        LifestealHeal.amount(
            dealt,
            lifesteal.share(),
            lifesteal.maxHealPerHit(),
            shooter.getHealth(),
            shooter.getMaxHealth());
    if (healed > 0.0f) {
      shooter.heal(healed);
    }
  }

  private static double poolOf(final net.minecraft.entity.Entity struck) {
    return struck instanceof LivingEntity living
        ? living.getHealth() + living.getAbsorptionAmount()
        : 0.0;
  }
}
