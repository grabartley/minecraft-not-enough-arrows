package com.grahambartley.volley.entity;

import com.grahambartley.volley.ModSounds;
import com.grahambartley.volley.audio.ModSoundPlayer;
import com.grahambartley.volley.config.DisarmArrowConfig;
import com.grahambartley.volley.control.DisarmDrop;
import com.grahambartley.volley.server.ServerConfigService;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DisarmArrowEntity extends BaseArrowEntity {

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
      ModSoundPlayer.playFrom(this, ModSounds.DISARM_ARROW_IMPACT);
    }
  }

  @Override
  protected boolean hurtsWhatItHits() {
    return false;
  }
}
