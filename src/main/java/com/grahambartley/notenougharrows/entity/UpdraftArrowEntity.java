package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.updraft.UpdraftService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class UpdraftArrowEntity extends BaseArrowEntity {

  public UpdraftArrowEntity(
      final EntityType<? extends UpdraftArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public UpdraftArrowEntity(
      final EntityType<? extends UpdraftArrowEntity> entityType,
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
    final Vec3d base =
        Vec3d.ofBottomCenter(blockHitResult.getBlockPos().offset(blockHitResult.getSide()));
    return UpdraftService.open(world, base, ServerConfigService.get().traversal().updraft())
        ? ArrowImpact.DISCARD
        : ArrowImpact.DEFAULT;
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    UpdraftService.open(
        world,
        entityHitResult.getEntity().getPos(),
        ServerConfigService.get().traversal().updraft());
    return ArrowImpact.DEFAULT;
  }
}
