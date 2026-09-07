package com.grahambartley.morearrows.entity;

import com.grahambartley.morearrows.arrow.ArrowImpact;
import com.grahambartley.morearrows.grapple.GrappleService;
import com.grahambartley.morearrows.grapple.GrappleSession;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GrappleArrowEntity extends BaseArrowEntity implements Leashable {
  private static final String ANCHOR_KEY = "GrappleAnchor";

  @Nullable private BlockPos anchor;
  @Nullable private LeashData leashData;

  public GrappleArrowEntity(
      final EntityType<? extends GrappleArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public GrappleArrowEntity(
      final EntityType<? extends GrappleArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  @Override
  @Nullable
  public LeashData getLeashData() {
    return leashData;
  }

  @Override
  public void setLeashData(@Nullable final LeashData leashData) {
    this.leashData = leashData;
  }

  @Override
  public boolean canBeLeashed() {
    return false;
  }

  @Override
  public boolean beforeLeashTick(final Entity holder, final float distance) {
    return false;
  }

  @Override
  public void detachLeash(final boolean sendPacket, final boolean dropItem) {
    Leashable.super.detachLeash(sendPacket, false);
  }

  @Override
  protected ArrowImpact onArrowHitBlock(
      final ServerWorld world, final BlockHitResult blockHitResult) {
    shootingPlayer()
        .ifPresent(
            player -> {
              final GrappleSession started =
                  GrappleService.start(world, player, blockHitResult.getBlockPos());
              if (started != null) {
                anchor = started.anchor();
                attachLeash(player, true);
              }
            });
    return ArrowImpact.DEFAULT;
  }

  @Override
  protected void onArrowTick(final ServerWorld world) {
    if (anchor == null) {
      return;
    }
    if (getOwner() instanceof PlayerEntity player && stillPulling(world, player)) {
      if (getLeashHolder() != player) {
        attachLeash(player, true);
      }
      return;
    }
    anchor = null;
    if (isLeashed()) {
      detachLeash(true, false);
    }
  }

  @Override
  public void writeCustomDataToNbt(final NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    if (anchor != null) {
      nbt.put(ANCHOR_KEY, NbtHelper.fromBlockPos(anchor));
    }
  }

  @Override
  public void readCustomDataFromNbt(final NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);
    anchor = NbtHelper.toBlockPos(nbt, ANCHOR_KEY).orElse(null);
  }

  private boolean stillPulling(final ServerWorld world, final PlayerEntity player) {
    final GrappleSession session = GrappleService.sessionOf(world, player.getUuid());
    return session != null && session.anchor().equals(anchor);
  }
}
