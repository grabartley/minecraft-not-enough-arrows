package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.zipline.ZiplineOutcome;
import com.grahambartley.notenougharrows.zipline.ZiplineService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ZiplineArrowEntity extends BaseArrowEntity {

  public ZiplineArrowEntity(
      final EntityType<? extends ZiplineArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public ZiplineArrowEntity(
      final EntityType<? extends ZiplineArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  @Override
  protected ArrowImpact onArrowHitBlock(
      final ServerWorld world, final BlockHitResult blockHitResult) {
    final ZiplineOutcome outcome =
        ZiplineService.shoot(
            world, shootingPlayer().orElse(null), blockHitResult.getBlockPos(), getUuid());
    return outcome.spendsTheArrow() ? ArrowImpact.DISCARD : ArrowImpact.DEFAULT;
  }
}
