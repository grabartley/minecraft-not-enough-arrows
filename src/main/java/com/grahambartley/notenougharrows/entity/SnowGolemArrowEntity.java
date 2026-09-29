package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.social.SnowGolemBuild;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SnowGolemArrowEntity extends BaseArrowEntity {

  public SnowGolemArrowEntity(
      final EntityType<? extends SnowGolemArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public SnowGolemArrowEntity(
      final EntityType<? extends SnowGolemArrowEntity> entityType,
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
    final Vec3d inFront =
        Vec3d.ofBottomCenter(blockHitResult.getBlockPos().offset(blockHitResult.getSide()));
    return buildAt(world, inFront) ? ArrowImpact.DISCARD : ArrowImpact.DEFAULT;
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    if (buildAt(world, entityHitResult.getEntity().getPos())) {
      return ArrowImpact.DISCARD;
    }
    return glanceOff(entityHitResult.getEntity());
  }

  @Override
  protected boolean hurtsWhatItHits() {
    return false;
  }

  private boolean buildAt(final ServerWorld world, final Vec3d at) {
    final int lifetime = ServerConfigService.get().social().snowGolem().lifetimeTicks();
    return SnowGolemBuild.build(world, at, shootingPlayer().orElse(null), lifetime).isPresent();
  }
}
