package com.grahambartley.notenougharrows.agriculture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.tint.TintChoice;
import java.util.List;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

class SaplingPaletteTest {

  @Test
  void offersOneChoicePerMemberOfTheVanillaSaplingsTag() {
    assertEquals(
        List.of(
            Identifier.ofVanilla("oak_sapling"),
            Identifier.ofVanilla("spruce_sapling"),
            Identifier.ofVanilla("birch_sapling"),
            Identifier.ofVanilla("jungle_sapling"),
            Identifier.ofVanilla("acacia_sapling"),
            Identifier.ofVanilla("dark_oak_sapling"),
            Identifier.ofVanilla("cherry_sapling"),
            Identifier.ofVanilla("mangrove_propagule"),
            Identifier.ofVanilla("azalea"),
            Identifier.ofVanilla("flowering_azalea")),
        SaplingPalette.create().choices().stream().map(TintChoice::ingredient).toList());
  }

  @Test
  void fallsBackToOak() {
    assertEquals("oak", SaplingPalette.create().fallback().key());
    assertEquals(
        Identifier.ofVanilla("oak_sapling"), SaplingPalette.create().fallback().ingredient());
  }

  @Test
  void labelsEachChoiceWithItsOwnKey() {
    for (final TintChoice choice : SaplingPalette.create().choices()) {
      assertEquals(SaplingPalette.LABEL_PREFIX + choice.key(), choice.labelKey());
    }
  }

  @Test
  void keysTheMangroveChoiceByItsTreeRatherThanItsPropagule() {
    assertTrue(SaplingPalette.create().find("mangrove").isPresent());
    assertTrue(SaplingPalette.create().find("mangrove_propagule").isEmpty());
  }

  @Test
  void givesEveryChoiceItsOwnColour() {
    assertEquals(
        SaplingPalette.create().choices().size(),
        SaplingPalette.create().choices().stream().mapToInt(TintChoice::color).distinct().count());
  }
}
