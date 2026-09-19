package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.combat.RailgunFlight;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RailgunArrowEntity extends BaseArrowEntity {
  private static final String LAUNCHED_KEY = "Launched";

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

  @Override
  protected double getGravity() {
    return RailgunFlight.gravity(
        super.getGravity(), ServerConfigService.get().combat().railgun().gravityFactor());
  }

  @Override
  protected void onArrowTick(final ServerWorld world) {
    if (launched || inGround) {
      return;
    }

    setVelocity(
        RailgunFlight.launchVelocity(
            getVelocity(), ServerConfigService.get().combat().railgun().speedMultiplier()));
    velocityModified = true;
    launched = true;
  }

  @Override
  public void writeCustomDataToNbt(final NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    nbt.putBoolean(LAUNCHED_KEY, launched);
  }

  @Override
  public void readCustomDataFromNbt(final NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);
    launched = nbt.getBoolean(LAUNCHED_KEY);
  }
}
