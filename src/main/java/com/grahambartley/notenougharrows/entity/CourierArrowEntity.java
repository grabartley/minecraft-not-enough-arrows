package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.arrow.FaceClearance;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.config.CourierArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.social.CourierPayloads;
import com.grahambartley.notenougharrows.social.CourierRefusals;
import com.grahambartley.notenougharrows.world.StackHandover;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CourierArrowEntity extends BaseArrowEntity {
  private static final float DELIVER_PITCH = 1.0f;

  public CourierArrowEntity(
      final EntityType<? extends CourierArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public CourierArrowEntity(
      final EntityType<? extends CourierArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  public static boolean reachesEveryPlayer(final PersistentProjectileEntity projectile) {
    return projectile instanceof CourierArrowEntity courier && courier.isLoaded();
  }

  public boolean isLoaded() {
    return CourierPayloads.isLoaded(getItemStack());
  }

  public Optional<ItemStack> payload() {
    return CourierPayloads.payloadOf(getItemStack());
  }

  @Override
  protected ArrowImpact onArrowHitBlock(
      final ServerWorld world, final BlockHitResult blockHitResult) {
    final Optional<ItemStack> payload = takePayload();
    if (payload.isEmpty()) {
      return ArrowImpact.DEFAULT;
    }
    final Vec3d at = FaceClearance.inFrontOf(blockHitResult.getPos(), blockHitResult.getSide());
    deliver(world, payload.get(), null, at);
    return ArrowImpact.DISCARD;
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    final Entity struck = entityHitResult.getEntity();
    final Optional<ItemStack> payload = takePayload();
    if (payload.isEmpty()) {
      return glanceOff(struck);
    }
    deliver(world, payload.get(), struck, struck.getPos());
    return ArrowImpact.DISCARD;
  }

  @Override
  protected boolean hurtsWhatItHits() {
    return false;
  }

  @Override
  public void remove(final RemovalReason reason) {
    if (reason.shouldDestroy() && getWorld() instanceof ServerWorld world) {
      takePayload().ifPresent(payload -> releaseWhereDestroyed(world, payload));
    }
    super.remove(reason);
  }

  private void deliver(
      final ServerWorld world,
      final ItemStack payload,
      @Nullable final Entity struck,
      final Vec3d at) {
    final CourierArrowConfig config = ServerConfigService.get().social().courier();
    if (CourierRefusals.refusesToDeliver(payload, config)) {
      returnToShooter(world, CourierPayloads.loaded(getItemStack(), payload), at);
      return;
    }
    if (struck instanceof PlayerEntity recipient && recipient.isAlive()) {
      StackHandover.handTo(recipient, payload);
    } else {
      StackHandover.drop(world, at, payload);
    }
    ModSoundPlayer.play(
        world,
        at,
        ModSounds.COURIER_ARROW_DELIVER,
        getSoundCategory(),
        ModSoundPlayer.LANDING_VOLUME,
        DELIVER_PITCH);
  }

  private void releaseWhereDestroyed(final ServerWorld world, final ItemStack payload) {
    final Optional<PlayerEntity> shooter = shootingPlayer().filter(PlayerEntity::isAlive);
    if (getY() < world.getBottomY() && shooter.isPresent()) {
      StackHandover.handTo(shooter.get(), payload);
    } else {
      StackHandover.drop(world, getPos(), payload);
    }
  }

  private void returnToShooter(final ServerWorld world, final ItemStack stack, final Vec3d at) {
    final Optional<PlayerEntity> shooter =
        shootingPlayer().filter(player -> player.isAlive() && player.getWorld() == world);
    if (shooter.isPresent()) {
      StackHandover.handTo(shooter.get(), stack);
    } else {
      StackHandover.drop(world, at, stack);
    }
  }

  private Optional<ItemStack> takePayload() {
    final ItemStack carried = getItemStack();
    final Optional<ItemStack> payload = CourierPayloads.payloadOf(carried);
    if (payload.isPresent()) {
      setStack(CourierPayloads.emptied(carried));
    }
    return payload;
  }
}
