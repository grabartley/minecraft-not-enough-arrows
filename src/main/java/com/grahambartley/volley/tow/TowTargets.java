package com.grahambartley.volley.tow;

import com.grahambartley.volley.ender.RecallTargets;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

public final class TowTargets {

  private TowTargets() {}

  public static boolean isTowable(@Nullable final Entity entity, final boolean playersMove) {
    if (!RecallTargets.isRecallable(entity) || entity.isSpectator()) {
      return false;
    }
    return playersMove || !RecallTargets.carriesAPlayer(entity);
  }
}
