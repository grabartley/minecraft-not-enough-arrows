package com.grahambartley.notenougharrows.structure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TimedStructureTest {
  private static final UUID ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID OWNER = UUID.fromString("00000000-0000-0000-0000-000000000002");
  private static final BlockPos FIRST = new BlockPos(0, 64, 0);
  private static final BlockPos SECOND = new BlockPos(1, 64, 0);
  private static final long EXPIRY = 100L;

  @Test
  void aStructureNeedsAnIdentity() {
    assertThrows(
        NullPointerException.class, () -> new TimedStructure(null, OWNER, List.of(FIRST), EXPIRY));
  }

  @Test
  void aStructureWithNoShooterHasNoOwner() {
    assertNull(new TimedStructure(ID, null, List.of(FIRST), EXPIRY).owner());
  }

  @Test
  void missingPositionsMeanAnEmptyStructure() {
    assertTrue(new TimedStructure(ID, OWNER, null, EXPIRY).isEmpty());
  }

  @Test
  void positionsAreCopiedSoTheCallerCannotChangeThemLater() {
    final List<BlockPos> positions = new ArrayList<>(List.of(FIRST));
    final TimedStructure structure = new TimedStructure(ID, OWNER, positions, EXPIRY);

    positions.add(SECOND);

    assertEquals(List.of(FIRST), structure.positions());
  }

  @ParameterizedTest
  @ValueSource(longs = {0L, 1L, EXPIRY - 1})
  void aStructureStandsBeforeItsExpiryTick(final long tick) {
    assertFalse(structure().hasExpired(tick));
  }

  @ParameterizedTest
  @ValueSource(longs = {EXPIRY, EXPIRY + 1, Long.MAX_VALUE})
  void aStructureHasExpiredFromItsExpiryTickOn(final long tick) {
    assertTrue(structure().hasExpired(tick));
  }

  @Test
  void aStructureHoldsOnlyThePositionsItWasGiven() {
    final TimedStructure structure = new TimedStructure(ID, OWNER, List.of(FIRST), EXPIRY);

    assertTrue(structure.holds(FIRST));
    assertFalse(structure.holds(SECOND));
  }

  @Test
  void takingAPositionOutKeepsEverythingElse() {
    final TimedStructure without = structure().without(FIRST);

    assertEquals(List.of(SECOND), without.positions());
    assertEquals(ID, without.id());
    assertEquals(OWNER, without.owner());
    assertEquals(EXPIRY, without.expiryTick());
  }

  @Test
  void takingOutAPositionItDoesNotHoldChangesNothing() {
    final TimedStructure structure = structure();

    assertSame(structure, structure.without(new BlockPos(9, 9, 9)));
  }

  @Test
  void takingOutEveryPositionLeavesItEmpty() {
    assertTrue(structure().without(FIRST).without(SECOND).isEmpty());
  }

  private static TimedStructure structure() {
    return new TimedStructure(ID, OWNER, List.of(FIRST, SECOND), EXPIRY);
  }
}
