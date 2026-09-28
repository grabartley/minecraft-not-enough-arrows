package com.grahambartley.notenougharrows.tint;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class DyePaletteTest {

  @Test
  void offersOneChoicePerVanillaDye() {
    final Set<String> expected =
        Arrays.stream(DyeColor.values()).map(DyeColor::getName).collect(Collectors.toSet());

    final Set<String> offered =
        DyePalette.create().choices().stream().map(TintChoice::key).collect(Collectors.toSet());

    assertEquals(16, offered.size());
    assertEquals(expected, offered);
  }

  @Test
  void fallsBackToWhite() {
    assertEquals("white", DyePalette.create().fallback().key());
  }

  @ParameterizedTest
  @EnumSource(DyeColor.class)
  void namesTheDyeItemAsTheIngredient(final DyeColor dye) {
    assertEquals(
        Identifier.ofVanilla(dye.getName() + "_dye"), DyePalette.choiceFor(dye).ingredient());
  }

  @ParameterizedTest
  @EnumSource(DyeColor.class)
  void takesTheColourVanillaDyesThingsWith(final DyeColor dye) {
    assertEquals(dye.getEntityColor() & 0xFFFFFF, DyePalette.choiceFor(dye).color());
  }

  @ParameterizedTest
  @EnumSource(DyeColor.class)
  void labelsTheChoiceWithVanillasOwnColourName(final DyeColor dye) {
    assertEquals("color.minecraft." + dye.getName(), DyePalette.choiceFor(dye).labelKey());
  }
}
