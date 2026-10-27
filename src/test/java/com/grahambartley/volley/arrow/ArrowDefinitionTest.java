package com.grahambartley.volley.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.volley.Volley;
import com.grahambartley.volley.entity.BaseArrowEntity;
import com.grahambartley.volley.entity.TintedArrowEntity;
import com.grahambartley.volley.tint.DyePalette;
import com.grahambartley.volley.tint.TintPalette;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ArrowDefinitionTest {
  private static final EntityType.EntityFactory<BaseArrowEntity> ENTITY_FACTORY =
      (type, world) -> null;
  private static final EntityType.EntityFactory<TintedArrowEntity> TINTED_FACTORY =
      (type, world) -> null;
  private static final PositionedArrowConstructor<BaseArrowEntity> CONSTRUCTOR =
      (type, world, x, y, z, stack, weapon) -> null;
  private static final PositionedArrowConstructor<TintedArrowEntity> TINTED_CONSTRUCTOR =
      (type, world, x, y, z, stack, weapon) -> null;

  @ParameterizedTest
  @ValueSource(strings = {"tnt_arrow", "arrow", "arrow2", "a", "wind_arrow_mk2"})
  void acceptsLowercaseSnakeCasePaths(final String path) {
    assertEquals(path, ArrowDefinition.of(path, ENTITY_FACTORY, CONSTRUCTOR).path());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"", "TNT_ARROW", "tntArrow", "tnt-arrow", "tnt arrow", "tnt.arrow", "tnt/arrow"})
  void rejectsPathsThatAreNotLowercaseSnakeCase(final String path) {
    assertThrows(
        IllegalArgumentException.class,
        () -> ArrowDefinition.of(path, ENTITY_FACTORY, CONSTRUCTOR));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -20})
  void rejectsANonPositiveTrackingInterval(final int ticks) {
    final ArrowDefinition<BaseArrowEntity> definition =
        ArrowDefinition.of("tnt_arrow", ENTITY_FACTORY, CONSTRUCTOR);

    assertThrows(IllegalArgumentException.class, () -> definition.trackedEvery(ticks));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 5, 20})
  void tracksAtTheIntervalItIsGivenAndKeepsEverythingElse(final int ticks) {
    final ArrowDefinition<BaseArrowEntity> plain =
        ArrowDefinition.of("boomerang_arrow", ENTITY_FACTORY, CONSTRUCTOR);

    final ArrowDefinition<BaseArrowEntity> tracked = plain.trackedEvery(ticks);

    assertEquals(ticks, tracked.trackingTickInterval());
    assertEquals(plain.path(), tracked.path());
    assertEquals(plain.sounds(), tracked.sounds());
  }

  @Test
  void rejectsANullPath() {
    assertThrows(
        NullPointerException.class, () -> ArrowDefinition.of(null, ENTITY_FACTORY, CONSTRUCTOR));
  }

  @Test
  void rejectsANullEntityFactory() {
    assertThrows(
        NullPointerException.class, () -> ArrowDefinition.of("tnt_arrow", null, CONSTRUCTOR));
  }

  @Test
  void rejectsANullPositionedConstructor() {
    assertThrows(
        NullPointerException.class, () -> ArrowDefinition.of("tnt_arrow", ENTITY_FACTORY, null));
  }

  @Test
  void appliesVanillaArrowDefaults() {
    final ArrowDefinition<BaseArrowEntity> definition =
        ArrowDefinition.of("tnt_arrow", ENTITY_FACTORY, CONSTRUCTOR);

    assertEquals(ArrowDefinition.DEFAULT_TRACKING_TICK_INTERVAL, definition.trackingTickInterval());
  }

  @Test
  void keepsTheFactoriesItWasGiven() {
    final ArrowDefinition<BaseArrowEntity> definition =
        ArrowDefinition.of("tnt_arrow", ENTITY_FACTORY, CONSTRUCTOR);

    assertSame(ENTITY_FACTORY, definition.entityFactory());
    assertSame(CONSTRUCTOR, definition.positionedConstructor());
  }

  @Test
  void namespacesItsIdentifierUnderTheModId() {
    final ArrowDefinition<BaseArrowEntity> definition =
        ArrowDefinition.of("tnt_arrow", ENTITY_FACTORY, CONSTRUCTOR);

    assertEquals(Volley.MOD_ID, definition.id().getNamespace());
    assertEquals("tnt_arrow", definition.id().getPath());
  }

  @Test
  void declaresNoSoundUnlessAsked() {
    assertTrue(ArrowDefinition.of("tnt_arrow", ENTITY_FACTORY, CONSTRUCTOR).sounds().isEmpty());
  }

  @Test
  void carriesTheSoundItDeclares() {
    final ArrowSound sound = ArrowSound.own(Identifier.of(Volley.MOD_ID, "smoke_arrow_impact"));

    final ArrowDefinition<BaseArrowEntity> definition =
        ArrowDefinition.of("smoke_arrow", ENTITY_FACTORY, CONSTRUCTOR, sound);

    assertEquals(List.of(sound), definition.sounds());
  }

  @Test
  void declaringASoundKeepsTheDefaultsOfAPlainArrow() {
    final ArrowDefinition<BaseArrowEntity> plain =
        ArrowDefinition.of("smoke_arrow", ENTITY_FACTORY, CONSTRUCTOR);

    final ArrowDefinition<BaseArrowEntity> voiced =
        ArrowDefinition.of(
            "smoke_arrow",
            ENTITY_FACTORY,
            CONSTRUCTOR,
            ArrowSound.own(Identifier.of(Volley.MOD_ID, "smoke_arrow_impact")));

    assertEquals(plain.path(), voiced.path());
    assertSame(plain.entityFactory(), voiced.entityFactory());
    assertSame(plain.positionedConstructor(), voiced.positionedConstructor());
    assertEquals(plain.trackingTickInterval(), voiced.trackingTickInterval());
  }

  @Test
  void carriesEverySoundItDeclaresInOrder() {
    final ArrowSound crack = ArrowSound.own(Identifier.of(Volley.MOD_ID, "crack"));
    final ArrowSound thaw = ArrowSound.own(Identifier.of(Volley.MOD_ID, "thaw"));

    assertEquals(
        List.of(crack, thaw),
        ArrowDefinition.of("frost_arrow", ENTITY_FACTORY, CONSTRUCTOR, crack, thaw).sounds());
  }

  @Test
  void rejectsANullSound() {
    assertThrows(
        NullPointerException.class,
        () -> ArrowDefinition.of("tnt_arrow", ENTITY_FACTORY, CONSTRUCTOR, (ArrowSound) null));
  }

  @Test
  void rejectsANullSoundList() {
    assertThrows(
        NullPointerException.class,
        () ->
            new ArrowDefinition<>(
                "tnt_arrow",
                ENTITY_FACTORY,
                CONSTRUCTOR,
                ArrowDefinition.DEFAULT_TRACKING_TICK_INTERVAL,
                null,
                java.util.Optional.empty(),
                List.of()));
  }

  @Test
  void isNotTintedUnlessAsked() {
    assertTrue(ArrowDefinition.of("tnt_arrow", ENTITY_FACTORY, CONSTRUCTOR).palette().isEmpty());
  }

  @Test
  void carriesThePaletteItIsTintedBy() {
    final TintPalette palette = DyePalette.create();

    final ArrowDefinition<TintedArrowEntity> definition =
        ArrowDefinition.tinted("paint_arrow", TINTED_FACTORY, TINTED_CONSTRUCTOR, palette);

    assertSame(palette, definition.palette().orElseThrow());
  }

  @Test
  void givesATintedArrowThePlainArrowDefaults() {
    final ArrowSound sound = ArrowSound.own(Identifier.of(Volley.MOD_ID, "paint_splat"));

    final ArrowDefinition<TintedArrowEntity> definition =
        ArrowDefinition.tinted(
            "paint_arrow", TINTED_FACTORY, TINTED_CONSTRUCTOR, DyePalette.create(), sound);

    assertEquals("paint_arrow", definition.path());
    assertSame(TINTED_FACTORY, definition.entityFactory());
    assertSame(TINTED_CONSTRUCTOR, definition.positionedConstructor());
    assertEquals(ArrowDefinition.DEFAULT_TRACKING_TICK_INTERVAL, definition.trackingTickInterval());
    assertEquals(List.of(sound), definition.sounds());
  }

  @Test
  void rejectsATintedArrowWithoutAPalette() {
    assertThrows(
        NullPointerException.class,
        () -> ArrowDefinition.tinted("paint_arrow", TINTED_FACTORY, TINTED_CONSTRUCTOR, null));
  }

  @Test
  void appliesNoEffectUnlessDeclared() {
    assertTrue(ArrowDefinition.of("tnt_arrow", ENTITY_FACTORY, CONSTRUCTOR).effects().isEmpty());
    assertTrue(
        ArrowDefinition.tinted(
                "paint_arrow", TINTED_FACTORY, TINTED_CONSTRUCTOR, DyePalette.create())
            .effects()
            .isEmpty());
  }

  @Test
  void carriesTheEffectsItIsDeclaredToApply() {
    final ArrowEffect glowing =
        new ArrowEffect(Identifier.ofVanilla("glowing"), ArrowEffect.Delivery.STRUCK_TARGET);

    final ArrowDefinition<BaseArrowEntity> definition =
        ArrowDefinition.of("glow_ink_arrow", ENTITY_FACTORY, CONSTRUCTOR).applying(glowing);

    assertEquals(List.of(glowing), definition.effects());
  }

  @Test
  void keepsEverythingElseWhenDeclaringEffects() {
    final ArrowSound sound = ArrowSound.own(Identifier.of(Volley.MOD_ID, "puff"));
    final ArrowDefinition<TintedArrowEntity> plain =
        ArrowDefinition.tinted(
            "paint_arrow", TINTED_FACTORY, TINTED_CONSTRUCTOR, DyePalette.create(), sound);

    final ArrowDefinition<TintedArrowEntity> declared =
        plain.applying(new ArrowEffect(Identifier.ofVanilla("nausea"), ArrowEffect.Delivery.AREA));

    assertEquals(plain.path(), declared.path());
    assertSame(plain.entityFactory(), declared.entityFactory());
    assertSame(plain.positionedConstructor(), declared.positionedConstructor());
    assertEquals(plain.sounds(), declared.sounds());
    assertEquals(plain.palette(), declared.palette());
    assertEquals(plain.trackingTickInterval(), declared.trackingTickInterval());
  }

  @Test
  void rejectsANullEffectList() {
    assertThrows(
        NullPointerException.class,
        () ->
            ArrowDefinition.of("tnt_arrow", ENTITY_FACTORY, CONSTRUCTOR)
                .applying((ArrowEffect[]) null));
  }
}
