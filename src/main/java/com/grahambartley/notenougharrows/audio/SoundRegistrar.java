package com.grahambartley.notenougharrows.audio;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class SoundRegistrar {
  public static final float REACH_BLOCKS = 48.0f;
  private static final Pattern VALID_PATH = Pattern.compile("[a-z0-9_]+");

  private final Map<Identifier, SoundEvent> declared = new LinkedHashMap<>();
  private boolean registered;

  public SoundEvent declare(final String path) {
    Objects.requireNonNull(path, "path");
    if (registered) {
      throw new IllegalStateException(
          "Sound '" + path + "' was declared after the registrar wrote to the registry");
    }
    if (!VALID_PATH.matcher(path).matches()) {
      throw new IllegalArgumentException(
          "Sound path must match " + VALID_PATH.pattern() + " but was '" + path + "'");
    }

    final Identifier id = Identifier.of(NotEnoughArrows.MOD_ID, path);
    if (declared.containsKey(id)) {
      throw new IllegalArgumentException("Sound '" + id + "' is already declared");
    }
    final SoundEvent event = SoundEvent.of(id, REACH_BLOCKS);
    declared.put(id, event);
    return event;
  }

  public void registerInto(final BiConsumer<Identifier, SoundEvent> registry) {
    Objects.requireNonNull(registry, "registry");
    if (registered) {
      throw new IllegalStateException("Sounds were already written to the registry");
    }
    registered = true;
    declared.forEach(registry);
  }

  public List<Identifier> ids() {
    return List.copyOf(declared.keySet());
  }

  public List<SoundEvent> events() {
    return List.copyOf(declared.values());
  }
}
