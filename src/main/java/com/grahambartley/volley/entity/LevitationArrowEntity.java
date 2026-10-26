package com.grahambartley.volley.entity;

import com.grahambartley.volley.combat.StatusArrowImpact;
import com.grahambartley.volley.config.LevitationArrowConfig;
import com.grahambartley.volley.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LevitationArrowEntity extends BaseArrowEntity {

  public LevitationArrowEntity(
      final EntityType<? extends LevitationArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public LevitationArrowEntity(
      final EntityType<? extends LevitationArrowEntity> entityType,
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
    final LevitationArrowConfig levitation = ServerConfigService.get().control().levitation();
    StatusArrowImpact.apply(
        entityHitResult.getEntity(), StatusEffects.LEVITATION, levitation.durationTicks(), this);
  }

  @Override
  protected boolean hurtsWhatItHits() {
    return false;
  }
}
