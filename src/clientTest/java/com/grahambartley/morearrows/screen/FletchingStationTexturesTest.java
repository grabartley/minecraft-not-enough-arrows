package com.grahambartley.morearrows.screen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FletchingStationTexturesTest {

  @ParameterizedTest(name = "row {0} with selected={1} hovered={2} draws from u={3}")
  @CsvSource({
    "0, -1, -1, 0",
    "0, -1, 0, 16",
    "0, 0, -1, 32",
    "0, 0, 0, 32",
    "2, 2, 1, 32",
    "1, 2, 1, 16",
    "1, 2, 0, 0"
  })
  void aSelectedRowOutranksAHoveredOneAndNeitherClaimsARowThatIsNotItsOwn(
      final int index, final int selected, final int hovered, final int expected) {
    assertEquals(expected, FletchingStationTextures.rowU(index, selected, hovered));
  }

  @ParameterizedTest(name = "scrollable={0} draws the scroller from u={1}")
  @CsvSource({"true, 48", "false, 60"})
  void aListWithNowhereToScrollDrawsTheDisabledScroller(
      final boolean scrollable, final int expected) {
    assertEquals(expected, FletchingStationTextures.scrollerU(scrollable));
  }
}
