package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.agriculture.CarvingFace;
import com.grahambartley.notenougharrows.agriculture.ShearService;
import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ShearArrowEntity extends BaseArrowEntity {
  public ShearArrowEntity(
      final EntityType<? extends ShearArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public ShearArrowEntity(
      final EntityType<? extends ShearArrowEntity> entityType,
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
    return ShearService.shearBlock(
            world,
            blockHitResult.getBlockPos(),
            blockHitResult.getSide(),
            CarvingFace.towardTheShooter(getVelocity()),
            shootingPlayer().orElse(null))
        ? ArrowImpact.DISCARD
        : ArrowImpact.DEFAULT;
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    if (ShearService.shearEntity(entityHitResult.getEntity(), shootingPlayer().orElse(null))) {
      return ArrowImpact.DISCARD;
    }
    return glanceOff(entityHitResult.getEntity());
  }

  @Override
  protected boolean hurtsWhatItHits() {
    return false;
  }
}
