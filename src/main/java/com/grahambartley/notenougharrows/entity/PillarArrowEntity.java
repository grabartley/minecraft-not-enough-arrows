package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.terrain.PillarService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PillarArrowEntity extends BaseArrowEntity {

  public PillarArrowEntity(
      final EntityType<? extends PillarArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public PillarArrowEntity(
      final EntityType<? extends PillarArrowEntity> entityType,
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
    if (PillarService.raise(world, blockHitResult.getBlockPos(), shootingPlayer().orElse(null))
        .isEmpty()) {
      return ArrowImpact.DEFAULT;
    }
    ModSoundPlayer.playFrom(this, ModSounds.PILLAR_ARROW_RISE);
    return ArrowImpact.DISCARD;
  }
}
