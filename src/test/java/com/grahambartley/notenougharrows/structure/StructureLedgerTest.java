package com.grahambartley.notenougharrows.structure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StructureLedgerTest {
  private static final BlockPos EARLY_POSITION = new BlockPos(0, 64, 0);
  private static final BlockPos LATE_POSITION = new BlockPos(1, 64, 0);
  private static final BlockPos SPARE_POSITION = new BlockPos(2, 64, 0);

  private StructureLedger ledger;

  @BeforeEach
  void setUp() {
    ledger = new StructureLedger();
  }

  @Test
  void startsWithNothingStanding() {
    assertTrue(ledger.isEmpty());
    assertEquals(0, ledger.size());
  }

  @Test
  void aMissingStructureIsNotRecorded() {
    ledger.add(null);

    assertTrue(ledger.isEmpty());
  }

  @Test
  void aStructureThatPlacedNothingIsNotRecorded() {
    ledger.add(structure(10L));

    assertTrue(ledger.isEmpty());
  }

  @Test
  void theSameStructureIsRecordedOnce() {
    final TimedStructure structure = structure(10L, EARLY_POSITION);
    ledger.add(structure);
    ledger.add(structure);

    assertEquals(1, ledger.size());
    assertEquals(List.of(structure), ledger.takeExpired(10L));
  }

  @Test
  void nothingIsTakenBeforeItsExpiry() {
    ledger.add(structure(10L, EARLY_POSITION));

    assertTrue(ledger.takeExpired(9L).isEmpty());
    assertEquals(1, ledger.size());
  }

  @Test
  void onlyStructuresThatHaveExpiredAreTaken() {
    final TimedStructure early = structure(10L, EARLY_POSITION);
    final TimedStructure late = structure(20L, LATE_POSITION);
    ledger.add(late);
    ledger.add(early);

    assertEquals(List.of(early), ledger.takeExpired(15L));
    assertEquals(1, ledger.size());
    assertTrue(ledger.holds(LATE_POSITION));
    assertFalse(ledger.holds(EARLY_POSITION));
  }

  @Test
  void anExpiredStructureIsTakenOnlyOnce() {
    ledger.add(structure(10L, EARLY_POSITION));

    assertEquals(1, ledger.takeExpired(10L).size());
    assertTrue(ledger.takeExpired(11L).isEmpty());
  }

  @Test
  void takingEverythingEmptiesTheLedger() {
    ledger.add(structure(10L, EARLY_POSITION));
    ledger.add(structure(20L, LATE_POSITION));

    assertEquals(2, ledger.takeAll().size());
    assertTrue(ledger.isEmpty());
    assertFalse(ledger.holds(EARLY_POSITION));
    assertTrue(ledger.takeExpired(Long.MAX_VALUE).isEmpty());
  }

  @Test
  void releasingAPositionTakesItOutOfItsStructure() {
    ledger.add(structure(10L, EARLY_POSITION, LATE_POSITION));

    assertTrue(ledger.release(EARLY_POSITION));

    assertFalse(ledger.holds(EARLY_POSITION));
    assertEquals(List.of(LATE_POSITION), ledger.takeExpired(10L).get(0).positions());
  }

  @Test
  void releasingAPositionNothingHoldsReportsNothing() {
    ledger.add(structure(10L, EARLY_POSITION));

    assertFalse(ledger.release(SPARE_POSITION));
  }

  @Test
  void aStructureIsLiveUntilItsExpiryTick() {
    final TimedStructure structure = structure(10L, EARLY_POSITION);
    ledger.add(structure);

    assertTrue(ledger.isLive(structure.id(), 9L));
    assertFalse(ledger.isLive(structure.id(), 10L));
  }

  @Test
  void aStructureTheLedgerNeverSawIsNotLive() {
    assertFalse(ledger.isLive(UUID.randomUUID(), 0L));
  }

  @Test
  void aTakenStructureIsNoLongerLive() {
    final TimedStructure structure = structure(10L, EARLY_POSITION);
    ledger.add(structure);
    ledger.takeAll();

    assertFalse(ledger.isLive(structure.id(), 0L));
  }

  @Test
  void anExpiredStructureDoesNotForgetAPositionALaterStructureNowHolds() {
    final TimedStructure early = structure(10L, EARLY_POSITION);
    ledger.add(early);
    ledger.release(EARLY_POSITION);
    ledger.add(structure(20L, EARLY_POSITION));

    ledger.takeExpired(10L);

    assertTrue(ledger.holds(EARLY_POSITION));
  }

  @Test
  void findsALiveStructureByItsId() {
    final TimedStructure structure = structure(10L, EARLY_POSITION);
    ledger.add(structure);

    assertEquals(structure, ledger.find(structure.id()).orElseThrow());
  }

  @Test
  void findsNothingForAnUnknownOrMissingId() {
    assertTrue(ledger.find(UUID.randomUUID()).isEmpty());
    assertTrue(ledger.find(null).isEmpty());
  }

  @Test
  void findingAStructureSeesAPositionThatWasReleased() {
    final TimedStructure structure = structure(10L, EARLY_POSITION, LATE_POSITION);
    ledger.add(structure);

    ledger.release(EARLY_POSITION);

    assertEquals(List.of(LATE_POSITION), ledger.find(structure.id()).orElseThrow().positions());
  }

  @Test
  void takingAStructureEarlyForgetsItAndItsPositions() {
    final TimedStructure structure = structure(10L, EARLY_POSITION);
    ledger.add(structure);

    assertEquals(structure, ledger.take(structure.id()).orElseThrow());
    assertTrue(ledger.isEmpty());
    assertFalse(ledger.holds(EARLY_POSITION));
    assertTrue(ledger.takeExpired(10L).isEmpty());
  }

  @Test
  void takingAStructureLeavesTheOthersToExpire() {
    final TimedStructure taken = structure(10L, EARLY_POSITION);
    final TimedStructure kept = structure(20L, LATE_POSITION);
    ledger.add(taken);
    ledger.add(kept);

    ledger.take(taken.id());

    assertEquals(List.of(kept), ledger.takeExpired(20L));
  }

  @Test
  void takingAnUnknownStructureTakesNothing() {
    ledger.add(structure(10L, EARLY_POSITION));

    assertTrue(ledger.take(UUID.randomUUID()).isEmpty());
    assertTrue(ledger.take(null).isEmpty());
    assertEquals(1, ledger.size());
  }

  private static TimedStructure structure(final long expiryTick, final BlockPos... positions) {
    return new TimedStructure(UUID.randomUUID(), null, List.of(positions), expiryTick);
  }
}
