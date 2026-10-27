package com.grahambartley.volley.control;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

final class FleeBehaviour {
  private static final Set<UUID> CORNERED = new HashSet<>();

  private FleeBehaviour() {}

  static boolean isCornered(final MobEntity mob) {
    return CORNERED.contains(mob.getUuid());
  }

  static void release(final UUID mobId) {
    CORNERED.remove(mobId);
  }

  static void forget() {
    CORNERED.clear();
  }

  static void keepFleeing(final MobEntity mob, final ControlHold hold) {
    if (hold.hasFledFarEnough(mob.getPos())) {
      MobSteering.halt(mob);
      CORNERED.remove(mob.getUuid());
      MobAggression.aim(mob, null);
      return;
    }
    MobSteering.rouse(mob);
    final boolean pulledBackToAFight = mob.getTarget() != null;
    final boolean hasSomewhereToRun =
        pulledBackToAFight || MobSteering.needsNewCourse(mob, fleeDestination(mob, hold))
            ? MobSteering.steer(mob, hold.anchor(), hold.steering())
            : MobSteering.isUnderway(mob);
    if (!hasSomewhereToRun && MobSteering.canPlanFromHere(mob)) {
      CORNERED.add(mob.getUuid());
    } else {
      CORNERED.remove(mob.getUuid());
      MobAggression.aim(mob, null);
    }
  }

  @Nullable
  private static Vec3d fleeDestination(final MobEntity mob, final ControlHold hold) {
    return hold.steering().destination(mob.getPos(), hold.anchor()).orElse(null);
  }
}
