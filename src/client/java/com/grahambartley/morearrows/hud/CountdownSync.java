package com.grahambartley.morearrows.hud;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CountdownSync {
  private static final Map<Integer, Countdown> BURNING = new LinkedHashMap<>();

  private CountdownSync() {}

  public static void accept(final int carrierId, final int delayTicks, final int remainingTicks) {
    final Countdown countdown = new Countdown(carrierId, delayTicks, remainingTicks);
    if (countdown.isBurning()) {
      BURNING.put(carrierId, countdown);
    } else {
      BURNING.remove(carrierId);
    }
  }

  public static void burnDown() {
    BURNING.values().removeIf(countdown -> !countdown.isBurning());
    BURNING.replaceAll((carrierId, countdown) -> countdown.burned());
  }

  public static List<Countdown> mostUrgentFirst() {
    final List<Countdown> burning = new ArrayList<>(BURNING.values());
    burning.sort(Comparator.comparingInt(Countdown::remainingTicks));
    return List.copyOf(burning);
  }

  public static void forget(final int carrierId) {
    BURNING.remove(carrierId);
  }

  public static void clear() {
    BURNING.clear();
  }
}
