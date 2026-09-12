package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.redstone.RedstoneChargeService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RedstoneArrowEntity extends BaseArrowEntity {

  public RedstoneArrowEntity(
      final EntityType<? extends RedstoneArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public RedstoneArrowEntity(
      final EntityType<? extends RedstoneArrowEntity> entityType,
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
    final BlockPos chargePos = blockHitResult.getBlockPos().offset(blockHitResult.getSide());
    RedstoneChargeService.charge(world, chargePos, shootingPlayer().orElse(null));
    return ArrowImpact.DEFAULT;
  }
}
