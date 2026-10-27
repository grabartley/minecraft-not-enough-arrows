package com.grahambartley.volley.entity;

import com.grahambartley.volley.arrow.ArrowImpact;
import com.grahambartley.volley.arrow.FaceClearance;
import com.grahambartley.volley.audio.ModSoundPlayer;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class BlockAreaArrowEntity extends BaseArrowEntity {

  protected BlockAreaArrowEntity(
      final EntityType<? extends BlockAreaArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  protected BlockAreaArrowEntity(
      final EntityType<? extends BlockAreaArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  protected abstract List<BlockPos> affectAround(
      ServerWorld world, BlockPos center, @Nullable PlayerEntity shooter);

  protected abstract SoundEvent effectSound();

  protected boolean alwaysDiscardsOnBlockHit() {
    return false;
  }

  @Override
  protected final ArrowImpact onArrowHitBlock(
      final ServerWorld world, final BlockHitResult blockHitResult) {
    final boolean changedAny = affect(world, FaceClearance.blockInFrontOf(blockHitResult));
    return changedAny || alwaysDiscardsOnBlockHit() ? ArrowImpact.DISCARD : ArrowImpact.DEFAULT;
  }

  @Override
  protected final ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    affect(world, entityHitResult.getEntity().getBlockPos());
    return ArrowImpact.DEFAULT;
  }

  private boolean affect(final ServerWorld world, final BlockPos center) {
    if (affectAround(world, center, shootingPlayer().orElse(null)).isEmpty()) {
      return false;
    }
    ModSoundPlayer.playFrom(this, effectSound());
    return true;
  }
}
