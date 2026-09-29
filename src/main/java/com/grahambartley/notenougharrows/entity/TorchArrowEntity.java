package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.discovery.TorchPlacement;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TorchArrowEntity extends BaseArrowEntity {

  public TorchArrowEntity(
      final EntityType<? extends TorchArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public TorchArrowEntity(
      final EntityType<? extends TorchArrowEntity> entityType,
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
    if (!ServerConfigService.get().discovery().torch().enabled()) {
      return ArrowImpact.DEFAULT;
    }
    return TorchPlacement.place(
                world,
                blockHitResult.getBlockPos(),
                blockHitResult.getSide(),
                shootingPlayer().orElse(null))
            .isPresent()
        ? ArrowImpact.DISCARD
        : ArrowImpact.DEFAULT;
  }
}
