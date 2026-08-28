package com.grahambartley.morearrows.fire;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class FirePatchShapeTest {
  private static final BlockPos CENTER = new BlockPos(10, 64, -30);

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -8})
  void aRadiusOfZeroOrLessCoversNothing(final int radius) {
    assertEquals(List.of(), FirePatchShape.columns(CENTER, radius));
  }

  @Test
  void aMissingCentreCoversNothing() {
    assertEquals(List.of(), FirePatchShape.columns(null, 4));
  }

  @ParameterizedTest
  @CsvSource({"1, 5", "2, 13", "3, 29", "4, 49", "8, 197"})
  void theColumnCountMatchesTheDiscOfThatRadius(final int radius, final int expectedColumns) {
    assertEquals(expectedColumns, FirePatchShape.columns(CENTER, radius).size());
  }

  @Test
  void aRadiusOfOneCoversTheCentreAndItsFourNeighbours() {
    assertEquals(
        List.of(
            CENTER,
            CENTER.add(-1, 0, 0),
            CENTER.add(0, 0, -1),
            CENTER.add(0, 0, 1),
            CENTER.add(1, 0, 0)),
        FirePatchShape.columns(CENTER, 1));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 8})
  void everyColumnStaysWithinTheRadius(final int radius) {
    for (final BlockPos column : FirePatchShape.columns(CENTER, radius)) {
      final int offsetX = column.getX() - CENTER.getX();
      final int offsetZ = column.getZ() - CENTER.getZ();
      assertTrue(
          offsetX * offsetX + offsetZ * offsetZ <= radius * radius,
          "Column " + column + " falls outside radius " + radius);
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 8})
  void everyColumnSitsAtTheCentreHeight(final int radius) {
    for (final BlockPos column : FirePatchShape.columns(CENTER, radius)) {
      assertEquals(CENTER.getY(), column.getY(), "Column " + column + " left the centre height");
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 8})
  void theCentreIsAlwaysCoveredFirst(final int radius) {
    assertEquals(CENTER, FirePatchShape.columns(CENTER, radius).get(0));
  }

  @Test
  void columnsAreOrderedOutwardsFromTheCentre() {
    final List<BlockPos> columns = FirePatchShape.columns(CENTER, 3);

    long previousDistance = -1;
    for (final BlockPos column : columns) {
      final long offsetX = (long) column.getX() - CENTER.getX();
      final long offsetZ = (long) column.getZ() - CENTER.getZ();
      final long distance = offsetX * offsetX + offsetZ * offsetZ;
      assertTrue(distance >= previousDistance, "Column " + column + " broke outward ordering");
      previousDistance = distance;
    }
  }

  @Test
  void everyColumnIsCoveredOnlyOnce() {
    final List<BlockPos> columns = FirePatchShape.columns(CENTER, 4);

    assertEquals(columns.size(), columns.stream().distinct().count());
  }

  @Test
  void theSameRadiusAlwaysProducesTheSameColumnsInTheSameOrder() {
    assertEquals(FirePatchShape.columns(CENTER, 3), FirePatchShape.columns(CENTER, 3));
  }

  @Test
  void theReturnedColumnsCannotBeEdited() {
    final List<BlockPos> columns = FirePatchShape.columns(CENTER, 2);

    assertThrows(UnsupportedOperationException.class, () -> columns.add(BlockPos.ORIGIN));
  }
}
