package com.grahambartley.notenougharrows.arrow;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.Identifier;

public record ArrowSound(Identifier sound, Optional<String> sharedSystem) {

  public ArrowSound {
    Objects.requireNonNull(sound, "sound");
    Objects.requireNonNull(sharedSystem, "sharedSystem");
    if (sharedSystem.filter(String::isBlank).isPresent()) {
      throw new IllegalArgumentException("A shared sound system needs a name");
    }
  }

  public static ArrowSound own(final Identifier sound) {
    return new ArrowSound(sound, Optional.empty());
  }

  public static ArrowSound sharedBy(final String system, final Identifier sound) {
    return new ArrowSound(sound, Optional.of(Objects.requireNonNull(system, "system")));
  }
}
