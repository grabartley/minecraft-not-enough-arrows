package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.combat.VolleyBurst;
import com.grahambartley.notenougharrows.config.VolleyArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class VolleyArrowEntity extends BaseArrowEntity {
  private static final String FLIGHT_TICKS_KEY = "FlightTicks";

  private int flightTicks;

  public VolleyArrowEntity(
      final EntityType<? extends VolleyArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public VolleyArrowEntity(
      final EntityType<? extends VolleyArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  public int flightTicks() {
    return flightTicks;
  }

  @Override
  protected void onArrowTick(final ServerWorld world) {
    if (inGround) {
      return;
    }

    flightTicks++;
    final VolleyArrowConfig volley = ServerConfigService.get().combat().volley();
    if (flightTicks < volley.splitDelayTicks()) {
      return;
    }

    VolleyBurst.split(world, this, volley);
    discard();
  }

  @Override
  public void writeCustomDataToNbt(final NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    nbt.putInt(FLIGHT_TICKS_KEY, flightTicks);
  }

  @Override
  public void readCustomDataFromNbt(final NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);
    flightTicks = nbt.getInt(FLIGHT_TICKS_KEY);
  }
}
