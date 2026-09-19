package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.combat.RailgunFlight;
import com.grahambartley.notenougharrows.config.RailgunArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RailgunArrowEntity extends BaseArrowEntity {
  private static final TrackedData<Float> GRAVITY_FACTOR =
      DataTracker.registerData(RailgunArrowEntity.class, TrackedDataHandlerRegistry.FLOAT);
  private static final String LAUNCHED_KEY = "Launched";
  private static final String GRAVITY_FACTOR_KEY = "GravityFactor";

  private boolean launched;

  public RailgunArrowEntity(
      final EntityType<? extends RailgunArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public RailgunArrowEntity(
      final EntityType<? extends RailgunArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  public boolean launched() {
    return launched;
  }

  public float gravityFactor() {
    return getDataTracker().get(GRAVITY_FACTOR);
  }

  @Override
  protected void initDataTracker(final DataTracker.Builder builder) {
    super.initDataTracker(builder);
    builder.add(GRAVITY_FACTOR, RailgunArrowConfig.DEFAULT_GRAVITY_FACTOR);
  }

  @Override
  protected double getGravity() {
    return RailgunFlight.gravity(super.getGravity(), gravityFactor());
  }

  @Override
  protected void onArrowTick(final ServerWorld world) {
    if (launched || inGround) {
      return;
    }

    final RailgunArrowConfig railgun = ServerConfigService.get().combat().railgun();
    getDataTracker().set(GRAVITY_FACTOR, railgun.gravityFactor());
    setVelocity(RailgunFlight.launchVelocity(getVelocity(), railgun.speedMultiplier()));
    velocityModified = true;
    launched = true;
  }

  @Override
  public void writeCustomDataToNbt(final NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    nbt.putBoolean(LAUNCHED_KEY, launched);
    nbt.putFloat(GRAVITY_FACTOR_KEY, gravityFactor());
  }

  @Override
  public void readCustomDataFromNbt(final NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);
    launched = nbt.getBoolean(LAUNCHED_KEY);
    if (nbt.contains(GRAVITY_FACTOR_KEY)) {
      getDataTracker().set(GRAVITY_FACTOR, nbt.getFloat(GRAVITY_FACTOR_KEY));
    }
  }
}
