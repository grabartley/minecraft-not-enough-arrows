package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.arrow.OneShot;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.config.ProspectorArrowConfig;
import com.grahambartley.notenougharrows.reveal.BlockRevealPulse;
import com.grahambartley.notenougharrows.reveal.RevealedBlocks;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ProspectorArrowEntity extends BaseArrowEntity {
  private static final float IMPACT_VOLUME = 1.0f;
  private static final float IMPACT_PITCH = 1.0f;
  private final OneShot pulse = new OneShot();

  public ProspectorArrowEntity(
      final EntityType<? extends ProspectorArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public ProspectorArrowEntity(
      final EntityType<? extends ProspectorArrowEntity> entityType,
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
    pulseAt(world, blockHitResult.getBlockPos());
    return ArrowImpact.DISCARD;
  }

  @Override
  protected void afterArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    pulseAt(world, getBlockPos());
  }

  private void pulseAt(final ServerWorld world, final BlockPos center) {
    if (!pulse.claim()) {
      return;
    }
    final ProspectorArrowConfig prospector = ServerConfigService.get().discovery().prospector();
    BlockRevealPulse.fire(
        world,
        center,
        prospector.radius(),
        prospector.durationTicks(),
        RevealedBlocks.of(prospector.blocks()),
        getOwner());
    ModSoundPlayer.playFrom(this, ModSounds.PROSPECTOR_ARROW_PULSE, IMPACT_VOLUME, IMPACT_PITCH);
  }
}
