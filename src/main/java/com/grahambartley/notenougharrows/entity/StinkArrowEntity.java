package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.chaos.StinkCloudService;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class StinkArrowEntity extends AreaControlArrowEntity {
  private static final float RELEASE_VOLUME = 1.0f;
  private static final float RELEASE_PITCH = 0.7f;

  public StinkArrowEntity(
      final EntityType<? extends StinkArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public StinkArrowEntity(
      final EntityType<? extends StinkArrowEntity> entityType,
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
    if (!StinkCloudService.open(world, center, ServerConfigService.get().chaos().stink())) {
      return false;
    }
    ModSoundPlayer.playFrom(this, ModSounds.STINK_ARROW_RELEASE, RELEASE_VOLUME, RELEASE_PITCH);
    return true;
  }
}
