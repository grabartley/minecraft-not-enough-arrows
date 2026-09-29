package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.chaos.PufferService;
import com.grahambartley.notenougharrows.config.PufferArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PufferArrowEntity extends BaseArrowEntity {

  public PufferArrowEntity(
      final EntityType<? extends PufferArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public PufferArrowEntity(
      final EntityType<? extends PufferArrowEntity> entityType,
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
    if (!isInflating()) {
      return ArrowImpact.DEFAULT;
    }
    if (entityHitResult.getEntity() instanceof LivingEntity struck
        && PufferService.inflate(world, struck, config())) {
      ModSoundPlayer.playFrom(struck, ModSounds.PUFFER_ARROW_INFLATE);
      return ArrowImpact.DISCARD;
    }
    return glanceOff(entityHitResult.getEntity());
  }

  @Override
  protected boolean hurtsWhatItHits() {
    return !isInflating();
  }

  private static boolean isInflating() {
    return config().enabled();
  }

  private static PufferArrowConfig config() {
    return ServerConfigService.get().chaos().puffer();
  }
}
