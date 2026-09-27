package com.grahambartley.notenougharrows.control;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public final class TargetGate {

  private TargetGate() {}

  public static boolean permits(final MobEntity mob, @Nullable final LivingEntity target) {
    if (target == null || !(mob.getWorld() instanceof ServerWorld world)) {
      return true;
    }
    if (DisarmFetchService.isFetching(world, mob)) {
      return false;
    }
    final Optional<ControlHold> held = ControlHoldService.heldIn(world, mob);
    if (held.isEmpty()) {
      return true;
    }
    final ControlHold hold = held.get();
    final Optional<UUID> subject = hold.subjectId();
    return switch (hold.steering()) {
      case FLEEING -> ControlHoldService.isCornered(mob);
      case DRAWN -> subject.map(id -> id.equals(target.getUuid())).orElse(true);
      case DEFENDING -> subject.map(id -> !id.equals(target.getUuid())).orElse(true);
    };
  }
}
