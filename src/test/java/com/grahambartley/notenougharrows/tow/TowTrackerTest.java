package com.grahambartley.notenougharrows.tow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TowTrackerTest {
  private static final UUID SHOOTER = UUID.randomUUID();

  private TowTracker tracker;

  @BeforeEach
  void setUp() {
    tracker = new TowTracker();
  }

  @Test
  void aTargetIsDraggedByOneTowAtATime() {
    final UUID target = UUID.randomUUID();
    tracker.add(TowSession.beginning(target, SHOOTER, 50, 10.0));
    final TowSession later = TowSession.beginning(target, UUID.randomUUID(), 50, 10.0);
    tracker.add(later);

    assertEquals(1, tracker.size());
    assertEquals(later, tracker.towOf(target));
  }

  @Test
  void oneShooterMayDragSeveralTargets() {
    tracker.add(TowSession.beginning(UUID.randomUUID(), SHOOTER, 50, 10.0));
    tracker.add(TowSession.beginning(UUID.randomUUID(), SHOOTER, 50, 10.0));

    assertEquals(2, tracker.size());
  }

  @Test
  void aShooterLeavingEndsEveryTowTheyStarted() {
    final TowSession other = TowSession.beginning(UUID.randomUUID(), UUID.randomUUID(), 50, 1.0);
    tracker.add(TowSession.beginning(UUID.randomUUID(), SHOOTER, 50, 10.0));
    tracker.add(TowSession.beginning(UUID.randomUUID(), SHOOTER, 50, 10.0));
    tracker.add(other);

    assertEquals(2, tracker.removeInvolving(SHOOTER).size());
    assertEquals(List.of(other), tracker.tows());
  }

  @Test
  void aTargetLeavingEndsItsTow() {
    final UUID target = UUID.randomUUID();
    tracker.add(TowSession.beginning(target, SHOOTER, 50, 10.0));

    assertEquals(1, tracker.removeInvolving(target).size());
    assertTrue(tracker.isEmpty());
  }

  @Test
  void aTowWithNoTimeLeftIsNotTracked() {
    tracker.add(TowSession.beginning(UUID.randomUUID(), SHOOTER, 0, 10.0));
    tracker.add(null);

    assertTrue(tracker.isEmpty());
  }

  @Test
  void missingIdsAreIgnored() {
    assertNull(tracker.towOf(null));
    assertNull(tracker.remove(null));
    assertTrue(tracker.removeInvolving(null).isEmpty());
  }
}
