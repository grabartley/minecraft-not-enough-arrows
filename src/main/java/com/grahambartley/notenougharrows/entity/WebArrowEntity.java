package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.config.WebArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.terrain.WebService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WebArrowEntity extends BaseArrowEntity {

  public WebArrowEntity(final EntityType<? extends WebArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public WebArrowEntity(
      final EntityType<? extends WebArrowEntity> entityType,
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
    final WebArrowConfig web = ServerConfigService.get().terrain().web();
    if (!web.enabled()) {
      return ArrowImpact.DEFAULT;
    }
    WebService.spin(
        world,
        blockHitResult.getBlockPos().offset(blockHitResult.getSide()),
        shootingPlayer().orElse(null),
        web);
    return ArrowImpact.DISCARD;
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    WebService.spin(
        world,
        entityHitResult.getEntity().getBlockPos(),
        shootingPlayer().orElse(null),
        ServerConfigService.get().terrain().web());
    return ArrowImpact.DEFAULT;
  }
}
