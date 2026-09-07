package com.grahambartley.morearrows.grapple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

class GrappleTrackerTest {
  private static final UUID FIRST_PLAYER = UUID.fromString("00000000-0000-0000-0000-0000000000a1");
  private static final UUID SECOND_PLAYER = UUID.fromString("00000000-0000-0000-0000-0000000000a2");
  private static final BlockPos ANCHOR = new BlockPos(4, 64, -2);
  private static final BlockPos OTHER_ANCHOR = new BlockPos(-8, 70, 12);
  private static final int LIFETIME_TICKS = 40;

  private GrappleTracker tracker;

  @BeforeEach
  void setUp() {
    tracker = new GrappleTracker();
  }

  @Test
  void startsWithNobodyBeingPulled() {
    assertTrue(tracker.isEmpty());
    assertEquals(0, tracker.size());
  }

  @ParameterizedTest
  @NullSource
  void aMissingSessionIsNotTracked(final GrappleSession session) {
    tracker.add(session);

    assertTrue(tracker.isEmpty());
  }

  @Test
  void aSessionThatHasAlreadyEndedIsNotTracked() {
    tracker.add(new GrappleSession(FIRST_PLAYER, ANCHOR, 0, 0));

    assertTrue(tracker.isEmpty());
  }

  @Test
  void aTrackedSessionIsFoundByThePlayerItPulls() {
    final GrappleSession session = session(FIRST_PLAYER, ANCHOR);
    tracker.add(session);

    assertEquals(session, tracker.sessionOf(FIRST_PLAYER));
  }

  @Test
  void aPlayerBeingPulledHasOnlyOneSessionAtATime() {
    tracker.add(session(FIRST_PLAYER, ANCHOR));
    tracker.add(session(FIRST_PLAYER, OTHER_ANCHOR));

    assertEquals(1, tracker.size());
    assertEquals(OTHER_ANCHOR, tracker.sessionOf(FIRST_PLAYER).anchor());
  }

  @Test
  void playersPulledTowardTheSameBlockAreTrackedSeparately() {
    tracker.add(session(FIRST_PLAYER, ANCHOR));
    tracker.add(session(SECOND_PLAYER, ANCHOR));

    assertEquals(2, tracker.size());
    assertEquals(ANCHOR, tracker.sessionOf(FIRST_PLAYER).anchor());
    assertEquals(ANCHOR, tracker.sessionOf(SECOND_PLAYER).anchor());
  }

  @Test
  void releasingAPlayerHandsBackTheSessionItEnded() {
    final GrappleSession session = session(FIRST_PLAYER, ANCHOR);
    tracker.add(session);

    assertEquals(session, tracker.remove(FIRST_PLAYER));
    assertTrue(tracker.isEmpty());
  }

  @Test
  void releasingAPlayerWhoWasNeverPulledChangesNothing() {
    tracker.add(session(FIRST_PLAYER, ANCHOR));

    assertNull(tracker.remove(SECOND_PLAYER));
    assertEquals(1, tracker.size());
  }

  @ParameterizedTest
  @NullSource
  void aMissingPlayerMatchesNoSession(final UUID playerId) {
    tracker.add(session(FIRST_PLAYER, ANCHOR));

    assertNull(tracker.sessionOf(playerId));
    assertNull(tracker.remove(playerId));
  }

  @Test
  void theTrackedSessionsAreListedInTheOrderTheyStarted() {
    tracker.add(session(FIRST_PLAYER, ANCHOR));
    tracker.add(session(SECOND_PLAYER, OTHER_ANCHOR));

    assertEquals(
        List.of(FIRST_PLAYER, SECOND_PLAYER),
        tracker.sessions().stream().map(GrappleSession::playerId).toList());
  }

  @Test
  void theTrackedSessionsCannotBeChangedFromOutside() {
    tracker.add(session(FIRST_PLAYER, ANCHOR));
    final List<GrappleSession> sessions = tracker.sessions();

    tracker.remove(FIRST_PLAYER);

    assertEquals(1, sessions.size());
    assertFalse(sessions.isEmpty());
  }

  private static GrappleSession session(final UUID playerId, final BlockPos anchor) {
    return GrappleSession.beginning(playerId, anchor, LIFETIME_TICKS);
  }
}
