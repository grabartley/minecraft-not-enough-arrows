package com.grahambartley.notenougharrows.reveal;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public final class WatcherReport {
  static final String NEARBY_KEY = "message.not-enough-arrows.tripwire.report";
  static final String ELSEWHERE_KEY = "message.not-enough-arrows.tripwire.report.elsewhere";

  private WatcherReport() {}

  public static Text of(final WatcherAlarm alarm, final ServerPlayerEntity owner) {
    final Text crosser = alarm.crosser().getType().getName();
    if (owner.getWorld() != alarm.crosser().getWorld()) {
      return Text.translatable(ELSEWHERE_KEY, crosser);
    }
    final Vec3d from = owner.getPos();
    final Vec3d to = Vec3d.ofCenter(alarm.watcherPos());
    return Text.translatable(
        NEARBY_KEY,
        crosser,
        RoughDistance.of(from.distanceTo(to)),
        Text.translatable(CompassPoint.toward(to.x - from.x, to.z - from.z).translationKey()));
  }
}
