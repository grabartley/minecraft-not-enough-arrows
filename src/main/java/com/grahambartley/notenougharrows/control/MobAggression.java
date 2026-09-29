package com.grahambartley.notenougharrows.control;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.mob.Angriness;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WardenEntity;
import org.jetbrains.annotations.Nullable;

public final class MobAggression {

  private MobAggression() {}

  public static void aim(final MobEntity mob, @Nullable final LivingEntity target) {
    final LivingEntity previous = mob.getTarget();
    if (target != null && mob instanceof WardenEntity warden) {
      warden.increaseAngerAt(target, Angriness.ANGRY.getThreshold(), false);
    }
    mob.setTarget(target);
    final Brain<?> brain = mob.getBrain();
    if (keeps(brain, MemoryModuleType.ATTACK_TARGET)) {
      if (target == null) {
        brain.forget(MemoryModuleType.ATTACK_TARGET);
      } else {
        brain.remember(MemoryModuleType.ATTACK_TARGET, target);
      }
    }
    if (keeps(brain, MemoryModuleType.ANGRY_AT)) {
      if (target == null) {
        brain.forget(MemoryModuleType.ANGRY_AT);
      } else {
        brain.remember(MemoryModuleType.ANGRY_AT, target.getUuid());
      }
    }
    if (previous != target) {
      dropAttacksOn(mob);
    }
  }

  public static void forgive(final MobEntity mob, final Entity subject) {
    if (mob instanceof WardenEntity warden) {
      warden.removeSuspect(subject);
    }
  }

  private static void dropAttacksOn(final MobEntity mob) {
    mob.goalSelector.getGoals().stream()
        .filter(PrioritizedGoal::isRunning)
        .filter(running -> chasesItsOwnTarget(running.getGoal()))
        .forEach(PrioritizedGoal::stop);
  }

  private static boolean chasesItsOwnTarget(final Goal goal) {
    return goal instanceof ProjectileAttackGoal || goal instanceof AttackGoal;
  }

  private static boolean keeps(final Brain<?> brain, final MemoryModuleType<?> memory) {
    return brain.isMemoryInState(memory, MemoryModuleState.REGISTERED);
  }
}
