package com.grahambartley.morearrows.screen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FletchingListGeometryTest {

  @ParameterizedTest(name = "{0} recipes hide {1} rows")
  @CsvSource({"0, 0", "1, 0", "3, 0", "4, 1", "9, 6"})
  void hiddenRowsCountsOnlyWhatTheVisibleWindowCannotShow(
      final int recipeCount, final int expected) {
    assertEquals(expected, FletchingListGeometry.hiddenRows(recipeCount));
  }

  @ParameterizedTest(name = "{0} recipes scrollable: {1}")
  @CsvSource({"0, false", "3, false", "4, true", "20, true"})
  void aListIsScrollableOnlyWhenItOverflowsTheWindow(
      final int recipeCount, final boolean expected) {
    assertEquals(expected, FletchingListGeometry.scrollable(recipeCount));
  }

  @ParameterizedTest(name = "{0} recipes fill {1} rows")
  @CsvSource({"0, 0", "2, 2", "3, 3", "7, 3"})
  void visibleRowsNeverExceedsTheWindowOrTheRecipesOnOffer(
      final int recipeCount, final int expected) {
    assertEquals(expected, FletchingListGeometry.visibleRows(recipeCount));
  }

  @ParameterizedTest(name = "amount {0} clamps to {1}")
  @CsvSource({"-1.0, 0.0", "0.0, 0.0", "0.5, 0.5", "1.0, 1.0", "2.5, 1.0"})
  void scrollAmountIsClampedIntoZeroToOne(final float amount, final float expected) {
    assertEquals(expected, FletchingListGeometry.clampAmount(amount));
  }

  @Test
  void aNotANumberScrollAmountClampsToTheTopRatherThanPropagating() {
    assertEquals(0f, FletchingListGeometry.clampAmount(Float.NaN));
  }

  @ParameterizedTest(name = "{0} recipes at amount {1} start at row {2}")
  @CsvSource({
    "3, 0.0, 0",
    "3, 1.0, 0",
    "4, 0.0, 0",
    "4, 1.0, 1",
    "9, 0.0, 0",
    "9, 0.5, 3",
    "9, 1.0, 6"
  })
  void theTopRowFollowsTheScrollAmountAcrossTheHiddenRows(
      final int recipeCount, final float amount, final int expected) {
    assertEquals(expected, FletchingListGeometry.topRow(recipeCount, amount));
  }

  @Test
  void aListThatCannotScrollStaysPinnedToItsFirstRow() {
    assertEquals(0, FletchingListGeometry.topRow(2, 1.0f));
  }

  @ParameterizedTest(name = "wheel {2} on {0} recipes at {1} gives {3}")
  @CsvSource({
    "3, 0.0, 1.0, 0.0",
    "4, 0.0, -1.0, 1.0",
    "4, 1.0, 1.0, 0.0",
    "9, 0.5, -1.0, 0.6666667",
    "9, 0.0, 1.0, 0.0"
  })
  void wheelingMovesOneRowPerNotchAndStopsAtBothEnds(
      final int recipeCount, final float amount, final double wheel, final float expected) {
    assertEquals(
        expected, FletchingListGeometry.amountAfterScroll(recipeCount, amount, wheel), 1.0e-6f);
  }

  @ParameterizedTest(name = "dragging to {0} over a track at {1} gives {2}")
  @CsvSource({"0, 0, 0.0", "7, 0, 0.0", "27, 0, 0.5", "54, 0, 1.0", "100, 0, 1.0", "27, 20, 0.0"})
  void draggingMapsThePointerOntoTheScrollerTravelAndClampsAtBothEnds(
      final double mouseY, final int trackTop, final float expected) {
    assertEquals(expected, FletchingListGeometry.amountFromDrag(mouseY, trackTop), 1.0e-6f);
  }

  @ParameterizedTest(name = "amount {0} puts the scroller {1} down the track")
  @CsvSource({"0.0, 0", "0.5, 19", "1.0, 39"})
  void theScrollerSitsProportionallyAlongItsTravel(final float amount, final int expected) {
    assertEquals(expected, FletchingListGeometry.scrollerOffsetY(amount));
  }

  @Test
  void theScrollerNeverLeavesTheTrackItTravels() {
    assertEquals(
        FletchingListGeometry.TRACK_HEIGHT,
        FletchingListGeometry.scrollerOffsetY(1.0f) + FletchingListGeometry.SCROLLER_HEIGHT);
  }

  @Test
  void aTrackOnlyTakesThePointerWhileTheListCanActuallyScroll() {
    assertFalse(FletchingListGeometry.withinTrack(0, 0, 3));
    assertTrue(FletchingListGeometry.withinTrack(0, 0, 4));
  }

  @ParameterizedTest(name = "pointer ({0}, {1}) over a scrollable track: {2}")
  @CsvSource({
    "0, 0, true",
    "11, 53, true",
    "-1, 10, false",
    "12, 10, false",
    "5, -1, false",
    "5, 54, false"
  })
  void theTrackOnlyClaimsPointersInsideItsOwnRectangle(
      final double offsetX, final double offsetY, final boolean expected) {
    assertEquals(expected, FletchingListGeometry.withinTrack(offsetX, offsetY, 9));
  }

  @ParameterizedTest(name = "pointer ({2}, {3}) on {0} recipes at {1} hits row {4}")
  @CsvSource({
    "3, 0.0, 0, 0, 0",
    "3, 0.0, 15, 17, 0",
    "3, 0.0, 0, 18, 1",
    "3, 0.0, 0, 36, 2",
    "3, 0.0, 0, 54, -1",
    "2, 0.0, 0, 36, -1",
    "0, 0.0, 0, 0, -1",
    "3, 0.0, 16, 0, -1",
    "3, 0.0, -1, 0, -1",
    "3, 0.0, 0, -1, -1",
    "9, 1.0, 0, 0, 6",
    "9, 1.0, 0, 36, 8"
  })
  void aPointerResolvesToTheRecipeUnderItOrToNoRowAtAll(
      final int recipeCount,
      final float amount,
      final double offsetX,
      final double offsetY,
      final int expected) {
    assertEquals(
        expected, FletchingListGeometry.rowAtOffset(recipeCount, amount, offsetX, offsetY));
  }
}
