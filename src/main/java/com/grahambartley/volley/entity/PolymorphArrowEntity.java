package com.grahambartley.volley.entity;

import com.grahambartley.volley.ModSounds;
import com.grahambartley.volley.arrow.ArrowImpact;
import com.grahambartley.volley.audio.ModSoundPlayer;
import com.grahambartley.volley.config.PolymorphArrowConfig;
import com.grahambartley.volley.disguise.DisguiseService;
import com.grahambartley.volley.server.ServerConfigService;
import com.grahambartley.volley.world.ParticleBursts;
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
    ParticleBursts.aroundBody(world, struck, ParticleTypes.SCULK_SOUL, CHANGE_PUFFS, PUFF_SPEED);
    ModSoundPlayer.playFrom(struck, ModSounds.POLYMORPH_ARROW_CHANGE);
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
