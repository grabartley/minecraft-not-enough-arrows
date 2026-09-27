package com.grahambartley.notenougharrows.control;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;

public final class MobAggression {

  private MobAggression() {}

  public static void aim(final MobEntity mob, @Nullable final LivingEntity target) {
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
  }

  private static boolean keeps(final Brain<?> brain, final MemoryModuleType<?> memory) {
    return brain.isMemoryInState(memory, MemoryModuleState.REGISTERED);
  }
}
