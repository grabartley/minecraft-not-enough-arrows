package com.grahambartley.notenougharrows.screen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OptionRowLayoutTest {
  private static final int X = 40;
  private static final int Y = 100;
  private static final int ENTRY_WIDTH = 340;
  private static final int CONTROL_WIDTH = 100;

  private static OptionRowLayout withControl() {
    return OptionRowLayout.of(X, Y, ENTRY_WIDTH, CONTROL_WIDTH);
  }

  @Test
  void keepsTheLabelClearOfTheControlBesideIt() {
    final OptionRowLayout layout = withControl();

    assertEquals(X, layout.textX());
    assertEquals(ENTRY_WIDTH - CONTROL_WIDTH - OptionRowLayout.TEXT_GAP, layout.labelWidth());
    assertTrue(layout.textX() + layout.labelWidth() < layout.controlX());
  }

  @Test
  void givesTheDescriptionTheWholeRowBecauseItSitsBelowTheControl() {
    final OptionRowLayout layout = withControl();

    assertEquals(ENTRY_WIDTH, layout.descriptionWidth());
    assertTrue(layout.descriptionY() > layout.labelY());
  }

  @Test
  void rightAlignsTheControlAgainstTheEndOfTheRow() {
    final OptionRowLayout layout = withControl();

    assertEquals(X + ENTRY_WIDTH - CONTROL_WIDTH, layout.controlX());
    assertEquals(Y + OptionRowLayout.CONTROL_OFFSET, layout.controlY());
  }

  @Test
  void leavesTheLabelTheWholeRowWhenNoControlSitsBesideIt() {
    final OptionRowLayout layout = OptionRowLayout.of(X, Y, ENTRY_WIDTH, 0);

    assertEquals(ENTRY_WIDTH, layout.labelWidth());
    assertEquals(ENTRY_WIDTH, layout.descriptionWidth());
    assertEquals(X + ENTRY_WIDTH, layout.controlX());
  }

  @ParameterizedTest
  @CsvSource({"0, 1", "100, 1", "106, 1", "107, 1", "120, 14", "340, 234"})
  void neverAsksForATextWidthBelowOneEvenInASqueezedRow(
      final int entryWidth, final int expectedLabelWidth) {
    assertEquals(
        expectedLabelWidth, OptionRowLayout.of(X, Y, entryWidth, CONTROL_WIDTH).labelWidth());
  }

  @Test
  void stacksTheTwoTextLinesAtItsDeclaredOffsets() {
    final OptionRowLayout layout = withControl();

    assertEquals(Y + OptionRowLayout.LABEL_OFFSET, layout.labelY());
    assertEquals(Y + OptionRowLayout.DESCRIPTION_OFFSET, layout.descriptionY());
  }
}
