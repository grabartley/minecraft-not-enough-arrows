package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.traversal.TrampolineService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TrampolineArrowEntity extends BaseArrowEntity {

  public TrampolineArrowEntity(
      final EntityType<? extends TrampolineArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public TrampolineArrowEntity(
      final EntityType<? extends TrampolineArrowEntity> entityType,
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
    return TrampolineService.place(
                world,
                blockHitResult.getBlockPos().offset(blockHitResult.getSide()),
                shootingPlayer().orElse(null))
            .isEmpty()
        ? ArrowImpact.DEFAULT
        : ArrowImpact.DISCARD;
  }
}
