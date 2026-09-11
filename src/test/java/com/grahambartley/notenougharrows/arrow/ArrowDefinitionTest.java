package com.grahambartley.notenougharrows.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.entity.BaseArrowEntity;
import net.minecraft.entity.EntityType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ArrowDefinitionTest {
  private static final EntityType.EntityFactory<BaseArrowEntity> ENTITY_FACTORY =
      (type, world) -> null;
  private static final ArrowEntityFactory SPAWN_FACTORY = (world, x, y, z, stack, weapon) -> null;

  @ParameterizedTest
  @ValueSource(strings = {"tnt_arrow", "arrow", "arrow2", "a", "wind_arrow_mk2"})
  void acceptsLowercaseSnakeCasePaths(final String path) {
    assertEquals(path, ArrowDefinition.of(path, ENTITY_FACTORY, SPAWN_FACTORY).path());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"", "TNT_ARROW", "tntArrow", "tnt-arrow", "tnt arrow", "tnt.arrow", "tnt/arrow"})
  void rejectsPathsThatAreNotLowercaseSnakeCase(final String path) {
    assertThrows(
        IllegalArgumentException.class,
        () -> ArrowDefinition.of(path, ENTITY_FACTORY, SPAWN_FACTORY));
  }

  @ParameterizedTest
  @CsvSource({"0, 0.5", "0.5, 0", "-1, 0.5", "0.5, -1", "0, 0"})
  void rejectsNonPositiveDimensions(final float width, final float height) {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new ArrowDefinition<>(
                "tnt_arrow",
                ENTITY_FACTORY,
                SPAWN_FACTORY,
                width,
                height,
                ArrowDefinition.DEFAULT_MAX_TRACKING_RANGE,
                ArrowDefinition.DEFAULT_TRACKING_TICK_INTERVAL));
  }

  @ParameterizedTest
  @CsvSource({"0, 20", "-4, 20", "4, 0", "4, -20"})
  void rejectsNonPositiveTrackingSettings(
      final int maxTrackingRange, final int trackingTickInterval) {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new ArrowDefinition<>(
                "tnt_arrow",
                ENTITY_FACTORY,
                SPAWN_FACTORY,
                ArrowDefinition.DEFAULT_SIZE,
                ArrowDefinition.DEFAULT_SIZE,
                maxTrackingRange,
                trackingTickInterval));
  }

  @Test
  void rejectsANullPath() {
    assertThrows(
        NullPointerException.class, () -> ArrowDefinition.of(null, ENTITY_FACTORY, SPAWN_FACTORY));
  }

  @Test
  void rejectsANullEntityFactory() {
    assertThrows(
        NullPointerException.class, () -> ArrowDefinition.of("tnt_arrow", null, SPAWN_FACTORY));
  }

  @Test
  void rejectsANullSpawnFactory() {
    assertThrows(
        NullPointerException.class, () -> ArrowDefinition.of("tnt_arrow", ENTITY_FACTORY, null));
  }

  @Test
  void appliesVanillaArrowDefaults() {
    final ArrowDefinition<BaseArrowEntity> definition =
        ArrowDefinition.of("tnt_arrow", ENTITY_FACTORY, SPAWN_FACTORY);

    assertEquals(ArrowDefinition.DEFAULT_SIZE, definition.width());
    assertEquals(ArrowDefinition.DEFAULT_SIZE, definition.height());
    assertEquals(ArrowDefinition.DEFAULT_MAX_TRACKING_RANGE, definition.maxTrackingRange());
    assertEquals(ArrowDefinition.DEFAULT_TRACKING_TICK_INTERVAL, definition.trackingTickInterval());
  }

  @Test
  void keepsTheFactoriesItWasGiven() {
    final ArrowDefinition<BaseArrowEntity> definition =
        ArrowDefinition.of("tnt_arrow", ENTITY_FACTORY, SPAWN_FACTORY);

    assertSame(ENTITY_FACTORY, definition.entityFactory());
    assertSame(SPAWN_FACTORY, definition.spawnFactory());
  }

  @Test
  void namespacesItsIdentifierUnderTheModId() {
    final ArrowDefinition<BaseArrowEntity> definition =
        ArrowDefinition.of("tnt_arrow", ENTITY_FACTORY, SPAWN_FACTORY);

    assertEquals(NotEnoughArrows.MOD_ID, definition.id().getNamespace());
    assertEquals("tnt_arrow", definition.id().getPath());
  }
}
