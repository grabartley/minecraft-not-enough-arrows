package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.config.BridgeArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.traversal.BridgeLine;
import com.grahambartley.notenougharrows.traversal.BridgeService;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BridgeArrowEntity extends BaseArrowEntity {

  public BridgeArrowEntity(
      final EntityType<? extends BridgeArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public BridgeArrowEntity(
      final EntityType<? extends BridgeArrowEntity> entityType,
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
    final BridgeArrowConfig bridge = ServerConfigService.get().traversal().bridge();
    final Vec3d toward =
        shootingPlayer()
            .map(PlayerEntity::getPos)
            .orElseGet(
                () ->
                    BridgeLine.backAlong(
                        blockHitResult.getPos(), getVelocity(), bridge.lengthBlocks()));
    return BridgeService.lay(
                world, blockHitResult.getBlockPos(), toward, shootingPlayer().orElse(null), bridge)
            .isEmpty()
        ? ArrowImpact.DEFAULT
        : ArrowImpact.DISCARD;
  }
}
