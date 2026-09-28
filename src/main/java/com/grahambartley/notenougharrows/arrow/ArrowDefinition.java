package com.grahambartley.notenougharrows.arrow;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.entity.BaseArrowEntity;
import com.grahambartley.notenougharrows.entity.TintedArrowEntity;
import com.grahambartley.notenougharrows.tint.TintPalette;
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
    ArrowEntityFactory spawnFactory,
    float width,
    float height,
    int maxTrackingRange,
    int trackingTickInterval,
    List<ArrowSound> sounds,
    Optional<TintPalette> palette) {

  public static final float DEFAULT_SIZE = 0.5f;
  public static final int DEFAULT_MAX_TRACKING_RANGE = 4;
  public static final int DEFAULT_TRACKING_TICK_INTERVAL = 20;

  private static final Pattern VALID_PATH = Pattern.compile("[a-z0-9_]+");

  public ArrowDefinition {
    Objects.requireNonNull(path, "path");
    Objects.requireNonNull(entityFactory, "entityFactory");
    Objects.requireNonNull(spawnFactory, "spawnFactory");
    sounds = List.copyOf(Objects.requireNonNull(sounds, "sounds"));
    Objects.requireNonNull(palette, "palette");

    if (!VALID_PATH.matcher(path).matches()) {
      throw new IllegalArgumentException(
          "Arrow path must match " + VALID_PATH.pattern() + " but was '" + path + "'");
    }
    if (width <= 0f || height <= 0f) {
      throw new IllegalArgumentException(
          "Arrow dimensions must be positive but were " + width + "x" + height);
    }
    if (maxTrackingRange <= 0) {
      throw new IllegalArgumentException(
          "Arrow max tracking range must be positive but was " + maxTrackingRange);
    }
    if (trackingTickInterval <= 0) {
      throw new IllegalArgumentException(
          "Arrow tracking tick interval must be positive but was " + trackingTickInterval);
    }
  }

  public ArrowDefinition(
      final String path,
      final EntityType.EntityFactory<E> entityFactory,
      final ArrowEntityFactory spawnFactory,
      final float width,
      final float height,
      final int maxTrackingRange,
      final int trackingTickInterval,
      final List<ArrowSound> sounds) {
    this(
        path,
        entityFactory,
        spawnFactory,
        width,
        height,
        maxTrackingRange,
        trackingTickInterval,
        sounds,
        Optional.empty());
  }

  public static <E extends BaseArrowEntity> ArrowDefinition<E> of(
      final String path,
      final EntityType.EntityFactory<E> entityFactory,
      final ArrowEntityFactory spawnFactory,
      final ArrowSound... sounds) {
    return new ArrowDefinition<>(
        path,
        entityFactory,
        spawnFactory,
        DEFAULT_SIZE,
        DEFAULT_SIZE,
        DEFAULT_MAX_TRACKING_RANGE,
        DEFAULT_TRACKING_TICK_INTERVAL,
        Arrays.asList(Objects.requireNonNull(sounds, "sounds")));
  }

  public static <E extends TintedArrowEntity> ArrowDefinition<E> tinted(
      final String path,
      final EntityType.EntityFactory<E> entityFactory,
      final ArrowEntityFactory spawnFactory,
      final TintPalette palette,
      final ArrowSound... sounds) {
    return new ArrowDefinition<>(
        path,
        entityFactory,
        spawnFactory,
        DEFAULT_SIZE,
        DEFAULT_SIZE,
        DEFAULT_MAX_TRACKING_RANGE,
        DEFAULT_TRACKING_TICK_INTERVAL,
        Arrays.asList(Objects.requireNonNull(sounds, "sounds")),
        Optional.of(Objects.requireNonNull(palette, "palette")));
  }

  public Identifier id() {
    return Identifier.of(NotEnoughArrows.MOD_ID, path);
  }
}
