package com.grahambartley.notenougharrows.control;

import com.grahambartley.notenougharrows.countdown.CountdownTimer;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public final class AllegianceCountdowns {

  private AllegianceCountdowns() {}

  public static List<CountdownTimer> timersIn(final ServerWorld world) {
    return ControlHoldService.holdsIn(world).stream()
        .filter(hold -> hold.steering() == ControlSteering.DEFENDING)
        .map(hold -> timerFor(world, hold))
        .toList();
  }

  @Nullable
  public static CountdownTimer timerOn(final ServerWorld world, final UUID mobId) {
    final Entity entity = world.getEntity(mobId);
    if (!(entity instanceof MobEntity mob)) {
      return null;
    }
    return ControlHoldService.heldIn(world, mob)
        .filter(hold -> hold.steering() == ControlSteering.DEFENDING)
        .map(hold -> timerFor(world, hold))
        .filter(CountdownTimer::isRunning)
        .orElse(null);
  }

  public static CountdownTimer timerFor(final ServerWorld world, final ControlHold hold) {
    final int remaining = (int) Math.max(0L, hold.expiryTick() - world.getTime());
    final int configured = ServerConfigService.get().control().allegiance().durationTicks();
    return new CountdownTimer(
        hold.mobId(), Math.max(configured, remaining), remaining, hold.expiryTick());
  }
}
