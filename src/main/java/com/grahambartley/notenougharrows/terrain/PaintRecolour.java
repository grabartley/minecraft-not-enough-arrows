package com.grahambartley.notenougharrows.terrain;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public final class PaintRecolour {
  private static final String VANILLA = Identifier.DEFAULT_NAMESPACE;
  private static final List<String> REDYED_FAMILIES = List.of("_wool", "_carpet");
  private static final List<String> UNDYED_FAMILIES =
      List.of("glass", "glass_pane", "terracotta", "candle");
  private static final Set<String> COLOURS =
      Arrays.stream(DyeColor.values())
          .map(DyeColor::getName)
          .collect(Collectors.toUnmodifiableSet());

  private PaintRecolour() {}

  public static Optional<Identifier> recoloured(final Identifier block, final String colour) {
    if (block == null || colour == null || !VANILLA.equals(block.getNamespace())) {
      return Optional.empty();
    }
    if (!COLOURS.contains(colour)) {
      return Optional.empty();
    }
    return recolouredPath(block.getPath(), colour)
        .filter(path -> !path.equals(block.getPath()))
        .map(Identifier::ofVanilla);
  }

  private static Optional<String> recolouredPath(final String path, final String colour) {
    for (final String family : REDYED_FAMILIES) {
      if (path.endsWith(family)
          && COLOURS.contains(path.substring(0, path.length() - family.length()))) {
        return Optional.of(colour + family);
      }
    }
    if (!UNDYED_FAMILIES.contains(path)) {
      return Optional.empty();
    }
    return Optional.of(colour + "_" + stainedPathOf(path));
  }

  private static String stainedPathOf(final String undyed) {
    return undyed.startsWith("glass") ? "stained_" + undyed : undyed;
  }
}
