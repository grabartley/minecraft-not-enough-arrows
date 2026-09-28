package com.grahambartley.notenougharrows.compat.jei;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import mezz.jei.api.ingredients.subtypes.UidContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TintedArrowSubtypesTest {
  private final Object red = new Object();
  private final Object blue = new Object();
  private final TintedArrowSubtypes<Object> subtypes =
      new TintedArrowSubtypes<>(Map.of(red, "red", blue, "blue")::get);

  @ParameterizedTest
  @EnumSource(UidContext.class)
  void namesEachVariantByTheChoiceItCarries(final UidContext context) {
    assertEquals("red", subtypes.getSubtypeData(red, context));
    assertEquals("blue", subtypes.getSubtypeData(blue, context));
  }

  @Test
  void keepsTwoChoicesApartSoEachFindsItsOwnRecipe() {
    assertNotEquals(
        subtypes.getSubtypeData(red, UidContext.Recipe),
        subtypes.getSubtypeData(blue, UidContext.Recipe));
  }

  @Test
  @SuppressWarnings("removal")
  void answersTheLegacyStringWithTheSameKey() {
    assertEquals("red", subtypes.getLegacyStringSubtypeInfo(red, UidContext.Ingredient));
  }

  @Test
  void rejectsAMissingChoiceLookup() {
    assertThrows(NullPointerException.class, () -> new TintedArrowSubtypes<>(null));
    assertThrows(NullPointerException.class, () -> TintedArrowSubtypes.of(null));
  }
}
