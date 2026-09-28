package com.grahambartley.notenougharrows.terrain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class PaintRecolourTest {

  @ParameterizedTest
  @CsvSource({
    "white_wool, red, red_wool",
    "light_blue_wool, black, black_wool",
    "black_carpet, light_gray, light_gray_carpet",
    "glass, blue, blue_stained_glass",
    "glass_pane, lime, lime_stained_glass_pane",
    "terracotta, orange, orange_terracotta",
    "candle, magenta, magenta_candle"
  })
  void aBlockVanillaAlreadyDyesTakesTheArrowsColour(
      final String block, final String colour, final String painted) {
    assertEquals(
        Optional.of(Identifier.ofVanilla(painted)),
        PaintRecolour.recoloured(Identifier.ofVanilla(block), colour));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "white_concrete",
        "white_concrete_powder",
        "red_stained_glass",
        "red_stained_glass_pane",
        "red_terracotta",
        "red_glazed_terracotta",
        "red_candle",
        "red_bed",
        "red_shulker_box",
        "red_banner",
        "moss_carpet",
        "pale_moss_carpet",
        "stone",
        "air"
      })
  void aBlockVanillaHasNoDyeingRecipeForIsLeftAlone(final String block) {
    assertTrue(PaintRecolour.recoloured(Identifier.ofVanilla(block), "blue").isEmpty());
  }

  @Test
  void paintingABlockItsOwnColourChangesNothing() {
    assertTrue(PaintRecolour.recoloured(Identifier.ofVanilla("red_wool"), "red").isEmpty());
  }

  @Test
  void woolNeverBecomesAnotherFamily() {
    assertEquals(
        Optional.of(Identifier.ofVanilla("green_wool")),
        PaintRecolour.recoloured(Identifier.ofVanilla("yellow_wool"), "green"));
  }

  @Test
  void aModdedBlockIsLeftAlone() {
    assertTrue(PaintRecolour.recoloured(Identifier.of("othermod", "white_wool"), "red").isEmpty());
  }

  @Test
  void aColourVanillaDoesNotHaveIsRefused() {
    assertTrue(PaintRecolour.recoloured(Identifier.ofVanilla("white_wool"), "teal").isEmpty());
  }

  @Test
  void missingInputPaintsNothing() {
    assertTrue(PaintRecolour.recoloured(null, "red").isEmpty());
    assertTrue(PaintRecolour.recoloured(Identifier.ofVanilla("white_wool"), null).isEmpty());
  }
}
