package com.grahambartley.morearrows.entity;

import com.grahambartley.morearrows.arrow.ArrowImpact;
import com.grahambartley.morearrows.config.PhysicsArrowConfig;
import com.grahambartley.morearrows.ricochet.Ricochet;
import com.grahambartley.morearrows.server.ServerConfigService;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RicochetArrowEntity extends BaseArrowEntity {
  private static final String BOUNCES_KEY = "Bounces";
  private static final float BOUNCE_VOLUME = 0.6f;
  private static final float BOUNCE_PITCH = 1.4f;

  private int bounces;

  public RicochetArrowEntity(
      final EntityType<? extends RicochetArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public RicochetArrowEntity(
      final EntityType<? extends RicochetArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  public int bounces() {
    return bounces;
  }

  @Override
  protected ArrowImpact onArrowHitBlock(
      final ServerWorld world, final BlockHitResult blockHitResult) {
    final PhysicsArrowConfig physics = ServerConfigService.get().physics();
    if (!Ricochet.canBounce(bounces, physics.ricochetBounceCount())) {
      return ArrowImpact.DEFAULT;
    }

    reactTo(world, blockHitResult);
    bounceOff(blockHitResult.getSide(), blockHitResult.getPos(), physics.ricochetRetainsDamage());
    return ArrowImpact.RETAIN;
  }

  @Override
  public void writeCustomDataToNbt(final NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    nbt.putInt(BOUNCES_KEY, bounces);
  }

  @Override
  public void readCustomDataFromNbt(final NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);
    bounces = nbt.getInt(BOUNCES_KEY);
  }

  private void reactTo(final ServerWorld world, final BlockHitResult blockHitResult) {
    final BlockState struck = world.getBlockState(blockHitResult.getBlockPos());
    struck.onProjectileHit(world, struck, blockHitResult, this);
  }

  private void bounceOff(final Direction surface, final Vec3d impact, final boolean retainsDamage) {
    setPosition(Ricochet.clearOf(impact, surface));
    setVelocity(Ricochet.deflect(getVelocity(), surface));
    velocityModified = true;
    setDamage(Ricochet.damageAfterBounce(getDamage(), retainsDamage));
    playSound(SoundEvents.ENTITY_ARROW_HIT, BOUNCE_VOLUME, BOUNCE_PITCH);
    bounces++;
  }
}
