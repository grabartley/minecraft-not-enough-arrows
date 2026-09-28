package com.grahambartley.notenougharrows.render;

import java.util.HashSet;
import java.util.Set;

public final class RidingPlayers {
  private static final Set<Integer> RIDING = new HashSet<>();

  private RidingPlayers() {}

  public static void accept(final int entityId, final boolean riding) {
    if (riding) {
      RIDING.add(entityId);
    } else {
      RIDING.remove(entityId);
    }
  }

  public static boolean isRiding(final int entityId) {
    return RIDING.contains(entityId);
  }

  public static void forget(final int entityId) {
    RIDING.remove(entityId);
  }

  public static void clear() {
    RIDING.clear();
  }
}
