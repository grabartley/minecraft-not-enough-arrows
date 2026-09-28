package com.grahambartley.notenougharrows.zipline;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public final class ZiplineFeedback {
  private static final boolean ACTION_BAR = true;

  private ZiplineFeedback() {}

  public static void tell(@Nullable final PlayerEntity shooter, final ZiplineOutcome outcome) {
    if (shooter == null || outcome == null) {
      return;
    }
    outcome.messageKey().ifPresent(key -> shooter.sendMessage(Text.translatable(key), ACTION_BAR));
  }
}
