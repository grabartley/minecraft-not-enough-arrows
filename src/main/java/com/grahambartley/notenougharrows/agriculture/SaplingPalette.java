package com.grahambartley.notenougharrows.agriculture;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.tint.TintChoice;
import com.grahambartley.notenougharrows.tint.TintPalette;
import java.util.List;
import net.minecraft.util.Identifier;

public final class SaplingPalette {
  public static final String FALLBACK_KEY = "oak";
  public static final String LABEL_PREFIX = "tint." + NotEnoughArrows.MOD_ID + ".sapling.";

  private static final List<TintChoice> CHOICES =
      List.of(
          choice("oak", "oak_sapling", 0x48B518),
          choice("spruce", "spruce_sapling", 0x619961),
          choice("birch", "birch_sapling", 0x80A755),
          choice("jungle", "jungle_sapling", 0x30BB0B),
          choice("acacia", "acacia_sapling", 0xAEA42A),
          choice("dark_oak", "dark_oak_sapling", 0x3A5E1A),
          choice("cherry", "cherry_sapling", 0xF2A7C8),
          choice("mangrove", "mangrove_propagule", 0x92C648),
          choice("azalea", "azalea", 0x6D8B2F),
          choice("flowering_azalea", "flowering_azalea", 0xB05BB3));

  private SaplingPalette() {}

  public static TintPalette create() {
    return new TintPalette(CHOICES, FALLBACK_KEY);
  }

  private static TintChoice choice(final String key, final String sapling, final int color) {
    return new TintChoice(key, Identifier.ofVanilla(sapling), color, LABEL_PREFIX + key);
  }
}
