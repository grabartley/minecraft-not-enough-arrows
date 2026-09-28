package com.grahambartley.notenougharrows.tint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

class TintPaletteTest {
  private static final TintChoice WHITE = choice("white", 0xF9FFFE);
  private static final TintChoice RED = choice("red", 0xB02E26);
  private static final TintChoice BLUE = choice("blue", 0x3C44AA);

  private static TintChoice choice(final String key, final int color) {
    return new TintChoice(key, Identifier.ofVanilla(key + "_dye"), color, "color.minecraft." + key);
  }

  private static TintPalette palette() {
    return new TintPalette(List.of(WHITE, RED, BLUE), "white");
  }

  @Test
  void resolvesAKnownChoiceToItself() {
    assertSame(RED, palette().resolve(new ArrowChoice("red")));
  }

  @Test
  void resolvesAMissingChoiceToTheFallback() {
    assertSame(WHITE, palette().resolve(null));
  }

  @Test
  void resolvesAnUnrecognisedChoiceToTheFallback() {
    assertSame(WHITE, palette().resolve(new ArrowChoice("mauve")));
    assertSame(WHITE, palette().resolve(new ArrowChoice("")));
  }

  @Test
  void findsOnlyTheChoicesItOffers() {
    assertSame(BLUE, palette().find("blue").orElseThrow());
    assertTrue(palette().find("mauve").isEmpty());
  }

  @Test
  void offersItsChoicesInTheOrderGiven() {
    assertEquals(List.of(WHITE, RED, BLUE), palette().choices());
  }

  @Test
  void doesNotLetItsChoicesBeChangedFromOutside() {
    final List<TintChoice> choices = new ArrayList<>(List.of(WHITE, RED));
    final TintPalette palette = new TintPalette(choices, "white");

    choices.add(BLUE);

    assertEquals(List.of(WHITE, RED), palette.choices());
    assertThrows(UnsupportedOperationException.class, () -> palette.choices().add(BLUE));
  }

  @Test
  void rejectsAnEmptyPalette() {
    assertThrows(IllegalArgumentException.class, () -> new TintPalette(List.of(), "white"));
  }

  @Test
  void rejectsAFallbackItDoesNotOffer() {
    assertThrows(
        IllegalArgumentException.class, () -> new TintPalette(List.of(RED, BLUE), "white"));
  }

  @Test
  void rejectsTheSameKeyListedTwice() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new TintPalette(List.of(WHITE, RED, choice("red", 0xFF0000)), "white"));
  }

  @Test
  void rejectsMissingParts() {
    assertThrows(NullPointerException.class, () -> new TintPalette(null, "white"));
    assertThrows(NullPointerException.class, () -> new TintPalette(List.of(WHITE), null));
  }
}
