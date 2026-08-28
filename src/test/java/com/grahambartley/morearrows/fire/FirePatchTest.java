package com.grahambartley.morearrows.fire;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FirePatchTest {
  private static final BlockPos FIRST = new BlockPos(0, 64, 0);
  private static final BlockPos SECOND = new BlockPos(1, 64, 0);

  @Test
  void missingPositionsBecomeAnEmptyPatch() {
    final FirePatch patch = new FirePatch(null, 100L);

    assertTrue(patch.isEmpty());
    assertEquals(List.of(), patch.positions());
  }

  @Test
  void aPatchWithPositionsIsNotEmpty() {
    assertFalse(new FirePatch(List.of(FIRST, SECOND), 100L).isEmpty());
  }

  @Test
  void aPatchKeepsItsOwnCopyOfThePositionsItWasGiven() {
    final List<BlockPos> positions = new ArrayList<>(List.of(FIRST));
    final FirePatch patch = new FirePatch(positions, 100L);

    positions.add(SECOND);

    assertEquals(List.of(FIRST), patch.positions());
  }

  @Test
  void thePositionsOfAPatchCannotBeEdited() {
    final FirePatch patch = new FirePatch(List.of(FIRST), 100L);

    assertThrows(UnsupportedOperationException.class, () -> patch.positions().add(SECOND));
  }

  @ParameterizedTest
  @CsvSource({"99, false", "100, true", "101, true", "-1, false"})
  void aPatchExpiresOnItsExpiryTickRatherThanAfterIt(final long tick, final boolean expired) {
    assertEquals(expired, new FirePatch(List.of(FIRST), 100L).hasExpired(tick));
  }
}
