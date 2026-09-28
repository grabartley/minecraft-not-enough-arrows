package com.grahambartley.notenougharrows.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.entity.BaseArrowEntity;
import com.grahambartley.notenougharrows.tint.DyePalette;
import com.grahambartley.notenougharrows.tint.TintPalette;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
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
                ArrowDefinition.DEFAULT_TRACKING_TICK_INTERVAL,
                List.of()));
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
                trackingTickInterval,
                List.of()));
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

  @Test
  void declaresNoSoundUnlessAsked() {
    assertTrue(ArrowDefinition.of("tnt_arrow", ENTITY_FACTORY, SPAWN_FACTORY).sounds().isEmpty());
  }

  @Test
  void carriesTheSoundItDeclares() {
    final ArrowSound sound =
        ArrowSound.own(Identifier.of(NotEnoughArrows.MOD_ID, "smoke_arrow_impact"));

    final ArrowDefinition<BaseArrowEntity> definition =
        ArrowDefinition.of("smoke_arrow", ENTITY_FACTORY, SPAWN_FACTORY, sound);

    assertEquals(List.of(sound), definition.sounds());
  }

  @Test
  void declaringASoundKeepsTheDefaultsOfAPlainArrow() {
    final ArrowDefinition<BaseArrowEntity> plain =
        ArrowDefinition.of("smoke_arrow", ENTITY_FACTORY, SPAWN_FACTORY);

    final ArrowDefinition<BaseArrowEntity> voiced =
        ArrowDefinition.of(
            "smoke_arrow",
            ENTITY_FACTORY,
            SPAWN_FACTORY,
            ArrowSound.own(Identifier.of(NotEnoughArrows.MOD_ID, "smoke_arrow_impact")));

    assertEquals(plain.path(), voiced.path());
    assertSame(plain.entityFactory(), voiced.entityFactory());
    assertSame(plain.spawnFactory(), voiced.spawnFactory());
    assertEquals(plain.width(), voiced.width());
    assertEquals(plain.height(), voiced.height());
    assertEquals(plain.maxTrackingRange(), voiced.maxTrackingRange());
    assertEquals(plain.trackingTickInterval(), voiced.trackingTickInterval());
  }

  @Test
  void carriesEverySoundItDeclaresInOrder() {
    final ArrowSound crack = ArrowSound.own(Identifier.of(NotEnoughArrows.MOD_ID, "crack"));
    final ArrowSound thaw = ArrowSound.own(Identifier.of(NotEnoughArrows.MOD_ID, "thaw"));

    assertEquals(
        List.of(crack, thaw),
        ArrowDefinition.of("frost_arrow", ENTITY_FACTORY, SPAWN_FACTORY, crack, thaw).sounds());
  }

  @Test
  void rejectsANullSound() {
    assertThrows(
        NullPointerException.class,
        () -> ArrowDefinition.of("tnt_arrow", ENTITY_FACTORY, SPAWN_FACTORY, (ArrowSound) null));
  }

  @Test
  void rejectsANullSoundList() {
    assertThrows(
        NullPointerException.class,
        () ->
            new ArrowDefinition<>(
                "tnt_arrow",
                ENTITY_FACTORY,
                SPAWN_FACTORY,
                ArrowDefinition.DEFAULT_SIZE,
                ArrowDefinition.DEFAULT_SIZE,
                ArrowDefinition.DEFAULT_MAX_TRACKING_RANGE,
                ArrowDefinition.DEFAULT_TRACKING_TICK_INTERVAL,
                null));
  }

  @Test
  void isNotTintedUnlessAsked() {
    final ArrowDefinition<BaseArrowEntity> definition =
        ArrowDefinition.of("tnt_arrow", ENTITY_FACTORY, SPAWN_FACTORY);

    assertFalse(definition.isTinted());
    assertTrue(definition.palette().isEmpty());
  }

  @Test
  void carriesThePaletteItIsTintedBy() {
    final TintPalette palette = DyePalette.create();

    final ArrowDefinition<BaseArrowEntity> definition =
        ArrowDefinition.of("paint_arrow", ENTITY_FACTORY, SPAWN_FACTORY).tintedBy(palette);

    assertTrue(definition.isTinted());
    assertSame(palette, definition.palette().orElseThrow());
  }

  @Test
  void tintingKeepsEverythingElseAboutTheArrow() {
    final ArrowSound sound = ArrowSound.own(Identifier.of(NotEnoughArrows.MOD_ID, "paint_splat"));
    final ArrowDefinition<BaseArrowEntity> plain =
        ArrowDefinition.of("paint_arrow", ENTITY_FACTORY, SPAWN_FACTORY, sound);

    final ArrowDefinition<BaseArrowEntity> tinted = plain.tintedBy(DyePalette.create());

    assertEquals(plain.path(), tinted.path());
    assertSame(plain.entityFactory(), tinted.entityFactory());
    assertSame(plain.spawnFactory(), tinted.spawnFactory());
    assertEquals(plain.width(), tinted.width());
    assertEquals(plain.height(), tinted.height());
    assertEquals(plain.maxTrackingRange(), tinted.maxTrackingRange());
    assertEquals(plain.trackingTickInterval(), tinted.trackingTickInterval());
    assertEquals(plain.sounds(), tinted.sounds());
  }

  @Test
  void rejectsANullPalette() {
    final ArrowDefinition<BaseArrowEntity> plain =
        ArrowDefinition.of("paint_arrow", ENTITY_FACTORY, SPAWN_FACTORY);

    assertThrows(NullPointerException.class, () -> plain.tintedBy(null));
  }
}
