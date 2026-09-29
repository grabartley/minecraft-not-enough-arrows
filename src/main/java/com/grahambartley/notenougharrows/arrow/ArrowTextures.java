package com.grahambartley.notenougharrows.arrow;

import java.util.Objects;
import net.minecraft.util.Identifier;

public final class ArrowTextures {
  private static final String FLIGHT_DIRECTORY = "textures/entity/arrow/";
  private static final String TINT_SUFFIX = "_tint";
  private static final String PNG = ".png";

  private ArrowTextures() {}

  public static Identifier flight(final Identifier arrowId) {
    Objects.requireNonNull(arrowId, "arrowId");
    return Identifier.of(arrowId.getNamespace(), FLIGHT_DIRECTORY + arrowId.getPath() + PNG);
  }

  public static Identifier flightTint(final Identifier arrowId) {
    Objects.requireNonNull(arrowId, "arrowId");
    return Identifier.of(
        arrowId.getNamespace(), FLIGHT_DIRECTORY + arrowId.getPath() + TINT_SUFFIX + PNG);
  }
}
