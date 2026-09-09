package com.grahambartley.morearrows.hud;

import com.grahambartley.morearrows.MoreArrows;
import com.grahambartley.morearrows.client.state.ClientStateService;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public final class CountdownHudRenderer {
  public static final String LABEL_KEY = "hud." + MoreArrows.MOD_ID + ".countdown";
  public static final int MAX_ROWS = 3;

  private static final int BAR_WIDTH = 60;
  private static final int BAR_HEIGHT = 3;
  private static final int ROW_HEIGHT = 14;
  private static final int BOTTOM_MARGIN = 64;
  private static final int TEXT_GAP = 4;

  private static final int TEXT_COLOR = 0xFFFFFFFF;
  private static final int URGENT_TEXT_COLOR = 0xFFFF6B6B;
  private static final int BAR_TRACK_COLOR = 0x80000000;
  private static final int BAR_FILL_COLOR = 0xFFE0C060;
  private static final int URGENT_BAR_FILL_COLOR = 0xFFFF6B6B;

  private CountdownHudRenderer() {}

  public static void register() {
    HudRenderCallback.EVENT.register((context, tickCounter) -> render(context));
  }

  private static void render(final DrawContext context) {
    final MinecraftClient client = MinecraftClient.getInstance();
    if (!shouldDraw(client)) {
      return;
    }

    final List<Countdown> burning = CountdownSync.mostUrgentFirst();
    if (burning.isEmpty()) {
      return;
    }

    final float scale = ClientStateService.get().countdownHudScale();
    context.getMatrices().push();
    context.getMatrices().scale(scale, scale, 1.0f);
    drawRows(
        context,
        client.textRenderer,
        burning,
        scaledWidth(context, scale) / 2,
        baseY(context, scale));
    context.getMatrices().pop();
  }

  private static void drawRows(
      final DrawContext context,
      final TextRenderer textRenderer,
      final List<Countdown> burning,
      final int centerX,
      final int baseY) {
    final int rows = Math.min(MAX_ROWS, burning.size());
    for (int row = 0; row < rows; row++) {
      drawRow(
          context, textRenderer, burning.get(row), centerX, baseY - (rows - 1 - row) * ROW_HEIGHT);
    }
  }

  private static void drawRow(
      final DrawContext context,
      final TextRenderer textRenderer,
      final Countdown countdown,
      final int centerX,
      final int y) {
    final boolean urgent = CountdownReadout.isUrgent(countdown.remainingTicks());
    final Text label =
        Text.translatable(LABEL_KEY, CountdownReadout.secondsText(countdown.remainingTicks()));
    final int labelWidth = textRenderer.getWidth(label);
    final int rowWidth = labelWidth + TEXT_GAP + BAR_WIDTH;
    final int left = centerX - rowWidth / 2;

    context.drawTextWithShadow(
        textRenderer, label, left, y, urgent ? URGENT_TEXT_COLOR : TEXT_COLOR);

    final int barLeft = left + labelWidth + TEXT_GAP;
    final int barTop = y + (textRenderer.fontHeight - BAR_HEIGHT) / 2;
    context.fill(barLeft, barTop, barLeft + BAR_WIDTH, barTop + BAR_HEIGHT, BAR_TRACK_COLOR);

    final int filled =
        CountdownReadout.barWidth(countdown.remainingTicks(), countdown.delayTicks(), BAR_WIDTH);
    if (filled > 0) {
      context.fill(
          barLeft,
          barTop,
          barLeft + filled,
          barTop + BAR_HEIGHT,
          urgent ? URGENT_BAR_FILL_COLOR : BAR_FILL_COLOR);
    }
  }

  private static boolean shouldDraw(final MinecraftClient client) {
    return client != null
        && client.player != null
        && client.world != null
        && !client.options.hudHidden
        && ClientStateService.get().showCountdownHud();
  }

  private static int scaledWidth(final DrawContext context, final float scale) {
    return Math.round(context.getScaledWindowWidth() / scale);
  }

  private static int baseY(final DrawContext context, final float scale) {
    return Math.round((context.getScaledWindowHeight() - BOTTOM_MARGIN) / scale);
  }
}
