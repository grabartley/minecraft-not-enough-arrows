package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.combat.StatusArrowImpact;
import com.grahambartley.notenougharrows.config.StatusArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class StatusArrowEntity extends BaseArrowEntity {
  protected StatusArrowEntity(
      final EntityType<? extends StatusArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  protected StatusArrowEntity(
      final EntityType<? extends StatusArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  protected abstract RegistryEntry<StatusEffect> effect();

  protected abstract int durationTicks(StatusArrowConfig status);

  @Override
  protected void afterArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    final StatusArrowConfig status = ServerConfigService.get().combat().status();
    StatusArrowImpact.apply(entityHitResult.getEntity(), effect(), durationTicks(status), this);
  }
}
