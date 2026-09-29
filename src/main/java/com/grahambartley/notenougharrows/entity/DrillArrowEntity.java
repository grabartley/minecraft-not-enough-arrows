package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.terrain.DrillService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DrillArrowEntity extends BaseArrowEntity {

  public DrillArrowEntity(
      final EntityType<? extends DrillArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public DrillArrowEntity(
      final EntityType<? extends DrillArrowEntity> entityType,
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
    if (!DrillService.bore(world, blockHitResult.getBlockPos(), shootingPlayer().orElse(null))) {
      return ArrowImpact.DEFAULT;
    }
    ModSoundPlayer.playFrom(this, ModSounds.DRILL_ARROW_BORE);
    return ArrowImpact.DISCARD;
  }
}
