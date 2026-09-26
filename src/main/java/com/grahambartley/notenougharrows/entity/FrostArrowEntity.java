package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.config.FrostArrowConfig;
import com.grahambartley.notenougharrows.control.FrostGripService;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FrostArrowEntity extends BaseArrowEntity {
  public FrostArrowEntity(
      final EntityType<? extends FrostArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public FrostArrowEntity(
      final EntityType<? extends FrostArrowEntity> entityType,
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
    if (!(entityHitResult.getEntity() instanceof LivingEntity living)) {
      return;
    }
    final FrostArrowConfig frost = ServerConfigService.get().control().frost();
    FrostGripService.grip(world, living, frost);
  }
}
