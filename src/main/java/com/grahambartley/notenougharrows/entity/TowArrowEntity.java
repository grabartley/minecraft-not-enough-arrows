package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.tow.TowService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TowArrowEntity extends BaseArrowEntity {
  private static final double NO_DAMAGE = 0.0;

  public TowArrowEntity(final EntityType<? extends TowArrowEntity> entityType, final World world) {
    super(entityType, world);
    setDamage(NO_DAMAGE);
  }

  public TowArrowEntity(
      final EntityType<? extends TowArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
    setDamage(NO_DAMAGE);
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    TowService.start(world, shootingPlayer().orElse(null), entityHitResult.getEntity());
    return ArrowImpact.DISCARD;
  }
}
