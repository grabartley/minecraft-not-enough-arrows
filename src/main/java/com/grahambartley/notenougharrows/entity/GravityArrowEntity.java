package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.gravity.GravityService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GravityArrowEntity extends BaseArrowEntity {

  public GravityArrowEntity(
      final EntityType<? extends GravityArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public GravityArrowEntity(
      final EntityType<? extends GravityArrowEntity> entityType,
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
    final BlockPos struck = blockHitResult.getBlockPos();
    final boolean struckBlockFell =
        GravityService.collapse(world, struck, shootingPlayer().orElse(null)).contains(struck);

    return struckBlockFell ? ArrowImpact.DISCARD : ArrowImpact.DEFAULT;
  }
}
