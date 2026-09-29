package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.config.SonarArrowConfig;
import com.grahambartley.notenougharrows.reveal.EntityRevealPulse;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SonarArrowEntity extends BaseArrowEntity {
  private static final float IMPACT_VOLUME = 1.0f;
  private static final float IMPACT_PITCH = 1.0f;
  private boolean fired;

  public SonarArrowEntity(
      final EntityType<? extends SonarArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public SonarArrowEntity(
      final EntityType<? extends SonarArrowEntity> entityType,
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
    pulseAt(world, blockHitResult.getPos());
    return ArrowImpact.DISCARD;
  }

  @Override
  protected void afterArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    pulseAt(world, getPos());
  }

  private void pulseAt(final ServerWorld world, final Vec3d center) {
    if (fired) {
      return;
    }
    fired = true;
    final SonarArrowConfig sonar = ServerConfigService.get().discovery().sonar();
    EntityRevealPulse.fire(world, center, sonar.radius(), sonar.durationTicks(), this, getOwner());
    ModSoundPlayer.playFrom(this, ModSounds.SONAR_ARROW_PULSE, IMPACT_VOLUME, IMPACT_PITCH);
  }
}
