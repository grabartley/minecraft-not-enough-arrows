package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AreaControlArrowEntity extends BaseArrowEntity {
  protected static final double FACE_CLEARANCE = 0.25;

  protected AreaControlArrowEntity(
      final EntityType<? extends AreaControlArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  protected AreaControlArrowEntity(
      final EntityType<? extends AreaControlArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  protected abstract boolean resolveAt(ServerWorld world, Vec3d center);

  @Override
  protected final ArrowImpact onArrowHitBlock(
      final ServerWorld world, final BlockHitResult blockHitResult) {
    final Vec3d center =
        blockHitResult
            .getPos()
            .add(Vec3d.of(blockHitResult.getSide().getVector()).multiply(FACE_CLEARANCE));
    return resolveAt(world, center) ? ArrowImpact.DISCARD : ArrowImpact.DEFAULT;
  }

  @Override
  protected final ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    resolveAt(world, entityHitResult.getPos());
    return ArrowImpact.DEFAULT;
  }
}
