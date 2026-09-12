package com.grahambartley.notenougharrows.hud;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class CountdownSync {
  private static final Map<Integer, Countdown> BURNING = new LinkedHashMap<>();

  private CountdownSync() {}

  public static void register() {
    ClientTickEvents.END_WORLD_TICK.register(world -> burnDown());
  }

  public static void accept(final int carrierId, final int delayTicks, final int remainingTicks) {
    final Countdown countdown = new Countdown(carrierId, delayTicks, remainingTicks);
    if (countdown.isBurning()) {
      BURNING.put(carrierId, countdown);
    } else {
      BURNING.remove(carrierId);
    }
  }

  public static void burnDown() {
    BURNING.replaceAll((carrierId, countdown) -> countdown.burned());
    BURNING.values().removeIf(countdown -> !countdown.isBurning());
  }

  public static List<Countdown> burning() {
    return List.copyOf(BURNING.values());
  }

  public static void forget(final int carrierId) {
    BURNING.remove(carrierId);
  }

  public static void clear() {
    BURNING.clear();
  }
}
