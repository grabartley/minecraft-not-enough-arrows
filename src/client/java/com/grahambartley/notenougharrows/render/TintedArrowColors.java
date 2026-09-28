package com.grahambartley.notenougharrows.render;

import com.grahambartley.notenougharrows.item.TintedArrowItems;
import com.grahambartley.notenougharrows.tint.TintChoice;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;

public final class TintedArrowColors {
  public static final int TINTED_LAYER = 0;

  private TintedArrowColors() {}

  public static void registerAll() {
    TintedArrowItems.registered()
        .forEach(
            item ->
                ColorProviderRegistry.ITEM.register(
                    (stack, tintIndex) -> layerColor(tintIndex, item.choiceOf(stack)), item));
  }

  public static int layerColor(final int tintIndex, final TintChoice choice) {
    return tintIndex == TINTED_LAYER ? opaque(choice) : Colors.WHITE;
  }

  public static int opaque(final TintChoice choice) {
    return ColorHelper.Argb.fullAlpha(choice.color());
  }
}
