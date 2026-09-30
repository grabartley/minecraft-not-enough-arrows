package com.grahambartley.notenougharrows.control;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

public final class DefenderTargets {

  private DefenderTargets() {}

  public static boolean threatens(final LivingEntity candidate, final LivingEntity defended) {
    if (candidate == null || defended == null || candidate == defended || !candidate.isAlive()) {
      return false;
    }
    return candidate instanceof MobEntity mob && mob.getTarget() == defended;
  }

  public static boolean inReach(
      final LivingEntity candidate, final LivingEntity defended, final double radius) {
    return candidate != null
        && defended != null
        && candidate.squaredDistanceTo(defended) <= radius * radius;
  }

  public static Optional<LivingEntity> threatTo(
      final ServerWorld world,
      final LivingEntity defended,
      final MobEntity defender,
      final double radius) {
    if (defended == null || radius <= 0.0) {
      return Optional.empty();
    }

    final LivingEntity current = defender.getTarget();
    if (isFoe(current, defended, defender, radius)
        && (threatens(current, defended)
            || threatens(current, defender)
            || current.getAttacker() == defender)) {
      return Optional.of(current);
    }

    final Optional<LivingEntity> struckBy =
        Stream.of(defended.getAttacker(), defender.getAttacker())
            .filter(attacker -> isFoe(attacker, defended, defender, radius))
            .findFirst();
    if (struckBy.isPresent()) {
      return struckBy;
    }

    final Box search = defended.getBoundingBox().expand(radius);
    final List<MobEntity> threats =
        world.getEntitiesByClass(
            MobEntity.class,
            search,
            candidate ->
                candidate != defender
                    && (threatens(candidate, defended)
                        || (threatens(candidate, defender) && !isSummonedBy(candidate, defender))));
    return threats.stream()
        .min(
            (left, right) ->
                Double.compare(defender.squaredDistanceTo(left), defender.squaredDistanceTo(right)))
        .map(LivingEntity.class::cast);
  }

  private static boolean isSummonedBy(final LivingEntity candidate, final MobEntity summoner) {
    return candidate instanceof Ownable summoned && summoned.getOwner() == summoner;
  }

  private static boolean isFoe(
      final LivingEntity candidate,
      final LivingEntity defended,
      final MobEntity defender,
      final double radius) {
    return candidate != null
        && candidate != defender
        && candidate != defended
        && (!isSummonedBy(candidate, defender) || threatens(candidate, defended))
        && candidate.isAlive()
        && inReach(candidate, defended, radius);
  }
}
