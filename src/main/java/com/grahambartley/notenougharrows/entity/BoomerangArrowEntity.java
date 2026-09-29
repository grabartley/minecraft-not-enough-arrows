package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.chaos.BoomerangFlight;
import com.grahambartley.notenougharrows.chaos.BoomerangReturn;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BoomerangArrowEntity extends BaseArrowEntity {
  private static final String RETURNING_KEY = "Returning";
  private static final String RETURN_TICKS_KEY = "ReturnTicks";
  private static final byte NOT_HOLDING_PIERCE = -1;
  private static final float RETURN_VOLUME = 1.0f;
  private static final float RETURN_PITCH = 1.2f;

  private boolean returning;
  private int returnTicks;
  private byte pierceBeforeHit = NOT_HOLDING_PIERCE;

  public BoomerangArrowEntity(
      final EntityType<? extends BoomerangArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public BoomerangArrowEntity(
      final EntityType<? extends BoomerangArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  public boolean isReturning() {
    return returning;
  }

  @Override
  protected ArrowImpact onArrowHitBlock(
      final ServerWorld world, final BlockHitResult blockHitResult) {
    if (!canReturn(world)) {
      return ArrowImpact.DEFAULT;
    }
    final BlockState struck = world.getBlockState(blockHitResult.getBlockPos());
    struck.onProjectileHit(world, struck, blockHitResult, this);
    startReturn();
    return ArrowImpact.RETAIN;
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    if (canReturn(world)) {
      pierceBeforeHit = getPierceLevel();
      setPierceLevel((byte) Math.max(1, pierceBeforeHit));
    }
    return ArrowImpact.DEFAULT;
  }

  @Override
  protected void afterArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    if (pierceBeforeHit == NOT_HOLDING_PIERCE) {
      return;
    }
    setPierceLevel(pierceBeforeHit);
    pierceBeforeHit = NOT_HOLDING_PIERCE;
    if (!isRemoved()) {
      startReturn();
    }
  }

  @Override
  protected void onArrowTick(final ServerWorld world) {
    if (!returning) {
      return;
    }
    returnTicks++;
    final LivingEntity shooter = shooter().orElse(null);
    if (!BoomerangReturn.isHome(world, shooter)) {
      land(world, null);
      return;
    }
    if (BoomerangFlight.hasArrived(getPos(), shooter.getEyePos())
        || BoomerangFlight.hasRunOutOfTime(returnTicks)) {
      land(world, shooter);
      return;
    }
    setVelocity(BoomerangFlight.steer(getVelocity(), getPos(), shooter.getEyePos()));
    velocityModified = true;
  }

  @Override
  protected boolean tryPickup(final PlayerEntity player) {
    return !returning && super.tryPickup(player);
  }

  @Override
  public void writeCustomDataToNbt(final NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    nbt.putBoolean(RETURNING_KEY, returning);
    nbt.putInt(RETURN_TICKS_KEY, returnTicks);
  }

  @Override
  public void readCustomDataFromNbt(final NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);
    returnTicks = nbt.getInt(RETURN_TICKS_KEY);
    if (nbt.getBoolean(RETURNING_KEY)) {
      fly();
    }
  }

  private boolean canReturn(final ServerWorld world) {
    return !returning
        && ServerConfigService.get().chaos().boomerang().enabled()
        && BoomerangReturn.isHome(world, shooter().orElse(null));
  }

  private void startReturn() {
    fly();
    setVelocity(BoomerangFlight.turnBack(getVelocity()));
    velocityModified = true;
  }

  private void fly() {
    returning = true;
    inGround = false;
    setNoClip(true);
    setNoGravity(true);
  }

  private void land(final ServerWorld world, @Nullable final LivingEntity shooter) {
    final BoomerangReturn.Outcome outcome =
        BoomerangReturn.resolve(
            world, asItemStack(), shooter, getPos(), pickupType == PickupPermission.ALLOWED);
    if (outcome != BoomerangReturn.Outcome.NOTHING_TO_RETURN) {
      ModSoundPlayer.playFrom(this, ModSounds.BOOMERANG_ARROW_RETURN, RETURN_VOLUME, RETURN_PITCH);
    }
    discard();
  }
}
