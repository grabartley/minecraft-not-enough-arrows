package com.grahambartley.notenougharrows.chaos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.tint.TintChoice;
import java.util.List;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

class DiscPaletteTest {
  private static final List<String> VANILLA_SONGS =
      List.of(
          "13",
          "cat",
          "blocks",
          "chirp",
          "far",
          "mall",
          "mellohi",
          "stal",
          "strad",
          "ward",
          "11",
          "wait",
          "otherside",
          "5",
          "pigstep",
          "relic",
          "creator",
          "creator_music_box",
          "precipice");

  @Test
  void offersOneChoicePerVanillaMusicDisc() {
    assertEquals(
        VANILLA_SONGS, DiscPalette.create().choices().stream().map(TintChoice::key).toList());
  }

  @Test
  void eachChoiceIsCraftedFromItsOwnDisc() {
    for (final TintChoice choice : DiscPalette.create().choices()) {
      assertEquals(Identifier.ofVanilla("music_disc_" + choice.key()), choice.ingredient());
    }
  }

  @Test
  void eachChoiceIsLabelledUnderThePartyPrefix() {
    for (final TintChoice choice : DiscPalette.create().choices()) {
      assertEquals(DiscPalette.LABEL_PREFIX + choice.key(), choice.labelKey());
    }
  }

  @Test
  void anUnknownDiscFallsBackToCat() {
    assertEquals("cat", DiscPalette.create().fallback().key());
    assertTrue(DiscPalette.create().find("music_disc_cat").isEmpty());
  }

  @Test
  void everyDiscIsTintedItsOwnColour() {
    assertEquals(
        DiscPalette.create().choices().size(),
        DiscPalette.create().choices().stream().mapToInt(TintChoice::color).distinct().count());
  }
}
