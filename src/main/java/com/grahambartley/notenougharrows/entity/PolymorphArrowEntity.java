package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.config.PolymorphArrowConfig;
import com.grahambartley.notenougharrows.disguise.DisguiseService;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PolymorphArrowEntity extends BaseArrowEntity {
  private static final int CHANGE_PUFFS = 20;
  private static final double PUFF_SPEED = 0.02;

  public PolymorphArrowEntity(
      final EntityType<? extends PolymorphArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public PolymorphArrowEntity(
      final EntityType<? extends PolymorphArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    final PolymorphArrowConfig polymorph = config();
    if (!polymorph.enabled()) {
      return ArrowImpact.DEFAULT;
    }
    final Entity struck = entityHitResult.getEntity();
    if (!DisguiseService.disguise(world, struck, polymorph.durationTicks())) {
      return glanceOff(struck);
    }
    world.spawnParticles(
        ParticleTypes.SCULK_SOUL,
        struck.getX(),
        struck.getBodyY(0.5),
        struck.getZ(),
        CHANGE_PUFFS,
        struck.getWidth() / 2.0,
        struck.getHeight() / 2.0,
        struck.getWidth() / 2.0,
        PUFF_SPEED);
    ModSoundPlayer.playLanding(struck, ModSounds.POLYMORPH_ARROW_CHANGE);
    return ArrowImpact.DISCARD;
  }

  @Override
  protected boolean hurtsWhatItHits() {
    return !config().enabled();
  }

  private static PolymorphArrowConfig config() {
    return ServerConfigService.get().chaos().polymorph();
  }
}
