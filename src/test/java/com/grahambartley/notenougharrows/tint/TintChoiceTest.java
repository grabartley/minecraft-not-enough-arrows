package com.grahambartley.notenougharrows.tint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TintChoiceTest {
  private static final Identifier RED_DYE = Identifier.ofVanilla("red_dye");

  @ParameterizedTest
  @ValueSource(strings = {"red", "light_blue", "music_disc_13", "a"})
  void acceptsLowercaseSnakeCaseKeys(final String key) {
    assertEquals(key, new TintChoice(key, RED_DYE, 0xB02E26, "color.minecraft.red").key());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "Red", "light-blue", "light blue", "red/dark", "red.dark"})
  void rejectsKeysThatCannotNameARecipe(final String key) {
    assertThrows(
        IllegalArgumentException.class,
        () -> new TintChoice(key, RED_DYE, 0xB02E26, "color.minecraft.red"));
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, 0x1000000, 0xFFB02E26})
  void rejectsAColourThatIsNotPlainRgb(final int color) {
    assertThrows(
        IllegalArgumentException.class,
        () -> new TintChoice("red", RED_DYE, color, "color.minecraft.red"));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 0xB02E26, TintChoice.MAX_COLOR})
  void acceptsAnyRgbColour(final int color) {
    assertEquals(color, new TintChoice("red", RED_DYE, color, "color.minecraft.red").color());
  }

  @Test
  void rejectsABlankLabelKey() {
    assertThrows(
        IllegalArgumentException.class, () -> new TintChoice("red", RED_DYE, 0xB02E26, " "));
  }

  @Test
  void rejectsMissingParts() {
    assertThrows(
        NullPointerException.class,
        () -> new TintChoice(null, RED_DYE, 0xB02E26, "color.minecraft.red"));
    assertThrows(
        NullPointerException.class,
        () -> new TintChoice("red", null, 0xB02E26, "color.minecraft.red"));
    assertThrows(NullPointerException.class, () -> new TintChoice("red", RED_DYE, 0xB02E26, null));
  }

  @Test
  void becomesTheComponentThatNamesIt() {
    assertEquals(
        new ArrowChoice("red"),
        new TintChoice("red", RED_DYE, 0xB02E26, "color.minecraft.red").asComponent());
  }
}
