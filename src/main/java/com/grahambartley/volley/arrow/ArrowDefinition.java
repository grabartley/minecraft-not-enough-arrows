package com.grahambartley.volley.arrow;

import com.grahambartley.volley.Volley;
import com.grahambartley.volley.entity.BaseArrowEntity;
import com.grahambartley.volley.entity.TintedArrowEntity;
import com.grahambartley.volley.tint.TintPalette;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

public record ArrowDefinition<E extends BaseArrowEntity>(
    String path,
    EntityType.EntityFactory<E> entityFactory,
    PositionedArrowConstructor<E> positionedConstructor,
    int trackingTickInterval,
    List<ArrowSound> sounds,
    Optional<TintPalette> palette,
    List<ArrowEffect> effects) {

  public static final float SIZE = 0.5f;
  public static final int MAX_TRACKING_RANGE = 4;
  public static final int DEFAULT_TRACKING_TICK_INTERVAL = 20;

  private static final Pattern VALID_PATH = Pattern.compile("[a-z0-9_]+");

  public ArrowDefinition {
    Objects.requireNonNull(path, "path");
    Objects.requireNonNull(entityFactory, "entityFactory");
    Objects.requireNonNull(positionedConstructor, "positionedConstructor");
    sounds = List.copyOf(Objects.requireNonNull(sounds, "sounds"));
    Objects.requireNonNull(palette, "palette");
    effects = List.copyOf(Objects.requireNonNull(effects, "effects"));

    if (!VALID_PATH.matcher(path).matches()) {
      throw new IllegalArgumentException(
          "Arrow path must match " + VALID_PATH.pattern() + " but was '" + path + "'");
    }
    if (trackingTickInterval <= 0) {
      throw new IllegalArgumentException(
          "Arrow tracking tick interval must be positive but was " + trackingTickInterval);
    }
  }

  public static <E extends BaseArrowEntity> ArrowDefinition<E> of(
      final String path,
      final EntityType.EntityFactory<E> entityFactory,
      final PositionedArrowConstructor<E> positionedConstructor,
      final ArrowSound... sounds) {
    return new ArrowDefinition<>(
        path,
        entityFactory,
        positionedConstructor,
        DEFAULT_TRACKING_TICK_INTERVAL,
        Arrays.asList(Objects.requireNonNull(sounds, "sounds")),
        Optional.empty(),
        List.of());
  }

  public static <E extends TintedArrowEntity> ArrowDefinition<E> tinted(
      final String path,
      final EntityType.EntityFactory<E> entityFactory,
      final PositionedArrowConstructor<E> positionedConstructor,
      final TintPalette palette,
      final ArrowSound... sounds) {
    return new ArrowDefinition<>(
        path,
        entityFactory,
        positionedConstructor,
        DEFAULT_TRACKING_TICK_INTERVAL,
        Arrays.asList(Objects.requireNonNull(sounds, "sounds")),
        Optional.of(Objects.requireNonNull(palette, "palette")),
        List.of());
  }

  public ArrowDefinition<E> trackedEvery(final int ticks) {
    return new ArrowDefinition<>(
        path, entityFactory, positionedConstructor, ticks, sounds, palette, effects);
  }

  public ArrowDefinition<E> applying(final ArrowEffect... applied) {
    return new ArrowDefinition<>(
        path,
        entityFactory,
        positionedConstructor,
        trackingTickInterval,
        sounds,
        palette,
        Arrays.asList(Objects.requireNonNull(applied, "applied")));
  }

  public Identifier id() {
    return Identifier.of(Volley.MOD_ID, path);
  }
}
