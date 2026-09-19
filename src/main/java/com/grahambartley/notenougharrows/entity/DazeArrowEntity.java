package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.control.ControlHoldService;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DazeArrowEntity extends BaseArrowEntity {

  public DazeArrowEntity(
      final EntityType<? extends DazeArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public DazeArrowEntity(
      final EntityType<? extends DazeArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  @Override
  protected void afterArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    if (entityHitResult.getEntity() instanceof MobEntity mob) {
      ControlHoldService.daze(world, mob, ServerConfigService.get().control().targeting());
    }
  }
}
