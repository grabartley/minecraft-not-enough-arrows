package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.control.SmokeCloudService;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SmokeArrowEntity extends AreaControlArrowEntity {
  private static final float IMPACT_VOLUME = 1.0f;
  private static final float IMPACT_PITCH = 0.8f;

  public SmokeArrowEntity(
      final EntityType<? extends SmokeArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public SmokeArrowEntity(
      final EntityType<? extends SmokeArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  @Override
  protected boolean resolveAt(
      final ServerWorld world, final Vec3d center, @Nullable final LivingEntity struck) {
    if (!SmokeCloudService.open(world, center, ServerConfigService.get().control().smoke())) {
      return false;
    }
    ModSoundPlayer.playFrom(this, ModSounds.SMOKE_ARROW_IMPACT, IMPACT_VOLUME, IMPACT_PITCH);
    return true;
  }
}
