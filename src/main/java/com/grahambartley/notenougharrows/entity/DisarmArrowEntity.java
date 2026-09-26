package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.config.DisarmArrowConfig;
import com.grahambartley.notenougharrows.control.DisarmDrop;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DisarmArrowEntity extends BaseArrowEntity {
  private static final float IMPACT_VOLUME = 1.0f;
  private static final float IMPACT_PITCH = 1.2f;

  public DisarmArrowEntity(
      final EntityType<? extends DisarmArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public DisarmArrowEntity(
      final EntityType<? extends DisarmArrowEntity> entityType,
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
    final DisarmArrowConfig disarm = ServerConfigService.get().control().disarm();
    if (DisarmDrop.disarm(
        world,
        living,
        shooter().map(Entity::getPos).orElse(null),
        disarm.affectsPlayers(),
        disarm.throwDistance())) {
      playSound(SoundEvents.BLOCK_TRIPWIRE_DETACH, IMPACT_VOLUME, IMPACT_PITCH);
    }
  }
}
