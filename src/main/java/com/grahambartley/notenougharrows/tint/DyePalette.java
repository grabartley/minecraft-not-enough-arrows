package com.grahambartley.notenougharrows.tint;

import java.util.Arrays;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public final class DyePalette {
  public static final String FALLBACK_KEY = DyeColor.WHITE.getName();

  private DyePalette() {}

  public static TintPalette create() {
    return new TintPalette(
        Arrays.stream(DyeColor.values()).map(DyePalette::choiceFor).toList(), FALLBACK_KEY);
  }

  static TintChoice choiceFor(final DyeColor dye) {
    final String name = dye.getName();
    return new TintChoice(
        name,
        Identifier.ofVanilla(name + "_dye"),
        dye.getEntityColor() & TintChoice.MAX_COLOR,
        "color.minecraft." + name);
  }
}
