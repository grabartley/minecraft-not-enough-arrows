package com.grahambartley.notenougharrows.control;

import java.util.List;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.MobEntity;

public final class WitherHeads {
  private static final int FIRST_SIDE_HEAD = 1;
  private static final int LAST_SIDE_HEAD = 2;
  private static final int NO_TARGET = 0;

  private WitherHeads() {}

  public static void spare(final MobEntity mob, final LivingEntity spared) {
    if (mob instanceof WitherEntity wither) {
      aimedAt(wither::getTrackedEntityId, spared.getId())
          .forEach(head -> wither.setTrackedEntityId(head, NO_TARGET));
    }
  }

  static List<Integer> aimedAt(final IntUnaryOperator trackedIdOfHead, final int sparedId) {
    return IntStream.rangeClosed(FIRST_SIDE_HEAD, LAST_SIDE_HEAD)
        .filter(head -> trackedIdOfHead.applyAsInt(head) == sparedId)
        .boxed()
        .toList();
  }
}
