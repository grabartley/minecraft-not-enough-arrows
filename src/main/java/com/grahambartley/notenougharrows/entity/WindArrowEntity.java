package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.wind.WindBurstService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WindArrowEntity extends BaseArrowEntity {
  private static final double FACE_CLEARANCE = 0.25;

  public WindArrowEntity(
      final EntityType<? extends WindArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public WindArrowEntity(
      final EntityType<? extends WindArrowEntity> entityType,
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
    final Vec3d center =
        blockHitResult
            .getPos()
            .add(Vec3d.of(blockHitResult.getSide().getVector()).multiply(FACE_CLEARANCE));
    WindBurstService.burst(world, center, this);
    return ArrowImpact.DISCARD;
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    WindBurstService.burst(world, entityHitResult.getPos(), this);
    return ArrowImpact.DEFAULT;
  }

  @Override
  protected boolean hurtsWhatItHits() {
    return false;
  }
}
