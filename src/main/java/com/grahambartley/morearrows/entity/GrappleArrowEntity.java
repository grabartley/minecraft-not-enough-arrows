package com.grahambartley.morearrows.entity;

import com.grahambartley.morearrows.arrow.ArrowImpact;
import com.grahambartley.morearrows.grapple.GrappleService;
import com.grahambartley.morearrows.grapple.GrappleSession;
import java.util.OptionalInt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GrappleArrowEntity extends BaseArrowEntity {
  private static final TrackedData<OptionalInt> HAULED_PLAYER =
      DataTracker.registerData(GrappleArrowEntity.class, TrackedDataHandlerRegistry.OPTIONAL_INT);

  private static final String ANCHOR_KEY = "GrappleAnchor";

  @Nullable private BlockPos anchor;

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

  public OptionalInt hauledPlayerId() {
    return getDataTracker().get(HAULED_PLAYER);
  }

  @Override
  protected void initDataTracker(final DataTracker.Builder builder) {
    super.initDataTracker(builder);
    builder.add(HAULED_PLAYER, OptionalInt.empty());
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
                haul(player);
              }
            });
    return ArrowImpact.DEFAULT;
  }

  @Override
  protected void onArrowTick(final ServerWorld world) {
    if (anchor == null) {
      return;
    }
    if (!(getOwner() instanceof PlayerEntity player) || !stillPulling(world, player)) {
      anchor = null;
      release();
      return;
    }
    haul(player);
  }

  @Override
  public void writeCustomDataToNbt(final NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    if (anchor != null) {
      nbt.putIntArray(ANCHOR_KEY, new int[] {anchor.getX(), anchor.getY(), anchor.getZ()});
    }
  }

  @Override
  public void readCustomDataFromNbt(final NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);
    final int[] stored = nbt.getIntArray(ANCHOR_KEY);
    anchor = stored.length == 3 ? new BlockPos(stored[0], stored[1], stored[2]) : null;
  }

  private boolean stillPulling(final ServerWorld world, final PlayerEntity player) {
    final GrappleSession session = GrappleService.sessionOf(world, player.getUuid());
    return session != null && session.anchor().equals(anchor);
  }

  private void haul(final Entity player) {
    final OptionalInt hauled = OptionalInt.of(player.getId());
    if (!hauled.equals(hauledPlayerId())) {
      getDataTracker().set(HAULED_PLAYER, hauled);
    }
  }

  private void release() {
    if (hauledPlayerId().isPresent()) {
      getDataTracker().set(HAULED_PLAYER, OptionalInt.empty());
    }
  }
}
