package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.discovery.TracerPath;
import com.grahambartley.notenougharrows.discovery.TracerService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TracerArrowEntity extends BaseArrowEntity {
  private final TracerPath path = new TracerPath();
  private boolean drawn;

  public TracerArrowEntity(
      final EntityType<? extends TracerArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public TracerArrowEntity(
      final EntityType<? extends TracerArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
    path.record(new Vec3d(x, y, z));
  }

  @Override
  protected void onArrowTick(final ServerWorld world) {
    if (!inGround && !drawn) {
      path.record(getPos());
    }
  }

  @Override
  protected ArrowImpact onArrowHitBlock(
      final ServerWorld world, final BlockHitResult blockHitResult) {
    drawTo(blockHitResult.getPos());
    return ArrowImpact.DISCARD;
  }

  @Override
  protected void afterArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    drawTo(getPos());
  }

  public TracerPath path() {
    return path;
  }

  private void drawTo(final Vec3d impact) {
    if (drawn) {
      return;
    }
    drawn = true;
    path.record(impact);
    TracerService.draw(this, path, getOwner());
  }
}
