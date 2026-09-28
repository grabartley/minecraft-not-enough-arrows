package com.grahambartley.notenougharrows.arrow;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record ArrowSound(Identifier sound, @Nullable String system) {

  public ArrowSound {
    Objects.requireNonNull(sound, "sound");
    if (system != null && system.isBlank()) {
      throw new IllegalArgumentException("A shared sound system needs a name");
    }
  }

  public static ArrowSound own(final Identifier sound) {
    return new ArrowSound(sound, null);
  }

  public static ArrowSound sharedBy(final String system, final Identifier sound) {
    return new ArrowSound(sound, Objects.requireNonNull(system, "system"));
  }

  public Optional<String> sharedSystem() {
    return Optional.ofNullable(system);
  }
}
