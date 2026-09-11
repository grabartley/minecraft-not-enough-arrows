package com.grahambartley.morearrows.compat.info;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class FletchingRecipeLayoutTest {

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
  @CsvSource({"0,4", "1,22", "2,40", "3,4", "4,22", "5,40", "6,4"})
  void columnsRepeatEveryThreeInputs(final int index, final int expectedX) {
    assertEquals(expectedX, FletchingRecipeLayout.inputX(index));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
  void theOutputSitsBeyondTheArrowWhichSitsBeyondTheInputGrid(final int inputCount) {
    final int gridRight =
        FletchingRecipeLayout.inputX(FletchingRecipeLayout.columns(inputCount) - 1)
            + FletchingRecipeLayout.SLOT;

    assertTrue(
        FletchingRecipeLayout.arrowX(inputCount) >= gridRight,
        "The arrow should start at or after the input grid ends");
    assertTrue(
        FletchingRecipeLayout.outputX(inputCount)
            >= FletchingRecipeLayout.arrowX(inputCount) + FletchingRecipeLayout.ARROW_WIDTH,
        "The output should start after the arrow ends");
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
  void everySlotFitsInsideTheDeclaredBounds(final int inputCount) {
    final int width = FletchingRecipeLayout.width(inputCount);
    final int height = FletchingRecipeLayout.height(inputCount);

    IntStream.range(0, inputCount)
        .forEach(
            index -> {
              assertTrue(
                  FletchingRecipeLayout.inputX(index) + FletchingRecipeLayout.SLOT <= width,
                  "Input " + index + " should fit the declared width");
              assertTrue(
                  FletchingRecipeLayout.inputY(index, inputCount) + FletchingRecipeLayout.SLOT
                      <= height,
                  "Input " + index + " should fit the declared height");
            });
    assertTrue(
        FletchingRecipeLayout.outputX(inputCount) + FletchingRecipeLayout.SLOT <= width,
        "The output should fit the declared width");
    assertTrue(
        FletchingRecipeLayout.outputY(inputCount) + FletchingRecipeLayout.SLOT <= height,
        "The output should fit the declared height");
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
  void theOutputAndTheArrowShareOneCentreLine(final int inputCount) {
    final int outputCentre =
        FletchingRecipeLayout.outputY(inputCount) + FletchingRecipeLayout.SLOT / 2;
    final int arrowCentre =
        FletchingRecipeLayout.arrowY(inputCount) + FletchingRecipeLayout.ARROW_HEIGHT / 2;

    assertTrue(
        Math.abs(outputCentre - arrowCentre) <= 1,
        "The arrow and the output should read as one row, centres were "
            + arrowCentre
            + " and "
            + outputCentre);
  }

  @Test
  void aSingleInputStillGetsAFullHeightRow() {
    assertEquals(FletchingRecipeLayout.height(1), FletchingRecipeLayout.height(3));
    assertEquals(FletchingRecipeLayout.outputY(1), FletchingRecipeLayout.outputY(3));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1})
  void refusesARecipeThatDeclaresNoInputs(final int inputCount) {
    assertThrows(IllegalArgumentException.class, () -> FletchingRecipeLayout.rows(inputCount));
    assertThrows(IllegalArgumentException.class, () -> FletchingRecipeLayout.columns(inputCount));
  }

  @Test
  void refusesANegativeInputIndex() {
    assertThrows(IllegalArgumentException.class, () -> FletchingRecipeLayout.inputX(-1));
    assertThrows(IllegalArgumentException.class, () -> FletchingRecipeLayout.inputY(-1, 1));
  }
}
