package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.control.ControlHoldService;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AllegianceArrowEntity extends BaseArrowEntity {
  private static final float IMPACT_VOLUME = 1.0f;
  private static final float IMPACT_PITCH = 1.4f;

  public AllegianceArrowEntity(
      final EntityType<? extends AllegianceArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public AllegianceArrowEntity(
      final EntityType<? extends AllegianceArrowEntity> entityType,
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
    if (!(entityHitResult.getEntity() instanceof MobEntity mob)) {
      return;
    }
    final boolean enlisted =
        ControlHoldService.enlist(
            world, mob, shooter().orElse(null), ServerConfigService.get().control().allegiance());
    if (enlisted) {
      playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, IMPACT_VOLUME, IMPACT_PITCH);
    }
  }
}
