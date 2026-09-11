package com.grahambartley.morearrows.compat.layout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class FletchingRecipeLayoutTest {

  private static final int BOUNDS = 9;

  @ParameterizedTest
  @CsvSource({"1,1", "2,1", "3,1", "4,2", "6,2", "7,3", "9,3"})
  void wrapsInputsIntoRowsOfThree(final int inputCount, final int expectedRows) {
    assertEquals(expectedRows, FletchingRecipeLayout.rows(inputCount));
  }

  @ParameterizedTest
  @CsvSource({"1,1", "2,2", "3,3", "4,3", "9,3"})
  void neverWidensPastThreeColumns(final int inputCount, final int expectedColumns) {
    assertEquals(expectedColumns, FletchingRecipeLayout.columns(inputCount));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
  void everySlotOfASelfSizedRecipeFitsItsOwnBounds(final int inputCount) {
    assertEverythingFitsWithin(
        FletchingRecipeLayout.sizedToFit(inputCount),
        inputCount,
        FletchingRecipeLayout.width(inputCount),
        FletchingRecipeLayout.height(inputCount));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
  void everySlotOfACentredRecipeFitsTheFixedBounds(final int inputCount) {
    assertEverythingFitsWithin(
        FletchingRecipeLayout.centredIn(inputCount, BOUNDS),
        inputCount,
        FletchingRecipeLayout.width(BOUNDS),
        FletchingRecipeLayout.height(BOUNDS));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8})
  void aCentredRecipeLeavesEqualSlackOnBothSides(final int inputCount) {
    final FletchingRecipeLayout layout = FletchingRecipeLayout.centredIn(inputCount, BOUNDS);
    final int leftSlack = layout.inputX(0);
    final int rightSlack =
        FletchingRecipeLayout.width(BOUNDS) - (layout.outputX() + FletchingRecipeLayout.SLOT);
    final int topSlack = layout.inputY(0);
    final int bottomSlack =
        FletchingRecipeLayout.height(BOUNDS)
            - (layout.inputY(inputCount - 1) + FletchingRecipeLayout.SLOT);

    assertTrue(
        Math.abs(leftSlack - rightSlack) <= 1,
        "Horizontal slack should be even, was " + leftSlack + " against " + rightSlack);
    assertTrue(
        Math.abs(topSlack - bottomSlack) <= 1,
        "Vertical slack should be even, was " + topSlack + " against " + bottomSlack);
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
  void centringInItsOwnSizeIsTheSameAsSelfSizing(final int inputCount) {
    final FletchingRecipeLayout selfSized = FletchingRecipeLayout.sizedToFit(inputCount);
    final FletchingRecipeLayout centred = FletchingRecipeLayout.centredIn(inputCount, inputCount);

    assertEquals(selfSized.outputX(), centred.outputX());
    assertEquals(selfSized.outputY(), centred.outputY());
    assertEquals(selfSized.arrowX(), centred.arrowX());
    assertEquals(selfSized.inputX(0), centred.inputX(0));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
  void theOutputSitsBeyondTheArrowWhichSitsBeyondTheInputGrid(final int inputCount) {
    final FletchingRecipeLayout layout = FletchingRecipeLayout.sizedToFit(inputCount);
    final int gridRight =
        layout.inputX(FletchingRecipeLayout.columns(inputCount) - 1) + FletchingRecipeLayout.SLOT;

    assertTrue(layout.arrowX() >= gridRight, "The arrow should start after the input grid ends");
    assertTrue(
        layout.outputX() >= layout.arrowX() + FletchingRecipeLayout.ARROW_WIDTH,
        "The output should start after the arrow ends");
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
  void theArrowAndTheOutputShareOneCentreLine(final int inputCount) {
    final FletchingRecipeLayout layout = FletchingRecipeLayout.sizedToFit(inputCount);
    final int outputCentre = layout.outputY() + FletchingRecipeLayout.SLOT / 2;
    final int arrowCentre = layout.arrowY() + FletchingRecipeLayout.ARROW_HEIGHT / 2;

    assertTrue(
        Math.abs(outputCentre - arrowCentre) <= 1,
        "The arrow and the output should read as one row, centres were "
            + arrowCentre
            + " and "
            + outputCentre);
  }

  @Test
  void everyBoundsWidthLeavesRoomForTheArrowAndTheOutput() {
    IntStream.rangeClosed(1, 9)
        .forEach(
            inputCount ->
                assertEquals(
                    FletchingRecipeLayout.width(inputCount),
                    FletchingRecipeLayout.sizedToFit(inputCount).outputX()
                        + FletchingRecipeLayout.SLOT
                        + FletchingRecipeLayout.PADDING,
                    "Declared width should end one padding past the output"));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 10, -1})
  void refusesAnInputCountNoStationRecipeCouldHave(final int inputCount) {
    assertThrows(
        IllegalArgumentException.class, () -> FletchingRecipeLayout.sizedToFit(inputCount));
    assertThrows(IllegalArgumentException.class, () -> FletchingRecipeLayout.rows(inputCount));
  }

  @Test
  void refusesToCentreARecipeLargerThanItsBounds() {
    assertThrows(IllegalArgumentException.class, () -> FletchingRecipeLayout.centredIn(4, 2));
  }

  @ParameterizedTest
  @CsvSource({"-1", "2", "9"})
  void refusesAnInputIndexTheRecipeDoesNotHave(final int index) {
    final FletchingRecipeLayout layout = FletchingRecipeLayout.sizedToFit(2);

    assertThrows(IllegalArgumentException.class, () -> layout.inputX(index));
    assertThrows(IllegalArgumentException.class, () -> layout.inputY(index));
  }

  private static void assertEverythingFitsWithin(
      final FletchingRecipeLayout layout, final int inputCount, final int width, final int height) {
    IntStream.range(0, inputCount)
        .forEach(
            index -> {
              assertTrue(
                  layout.inputX(index) >= 0
                      && layout.inputX(index) + FletchingRecipeLayout.SLOT <= width,
                  "Input " + index + " should fit the width " + width);
              assertTrue(
                  layout.inputY(index) >= 0
                      && layout.inputY(index) + FletchingRecipeLayout.SLOT <= height,
                  "Input " + index + " should fit the height " + height);
            });
    assertTrue(
        layout.outputX() + FletchingRecipeLayout.SLOT <= width,
        "The output should fit the width " + width);
    assertTrue(
        layout.outputY() + FletchingRecipeLayout.SLOT <= height,
        "The output should fit the height " + height);
    assertTrue(
        layout.arrowX() + FletchingRecipeLayout.ARROW_WIDTH <= width,
        "The arrow should fit the width " + width);
  }
}
