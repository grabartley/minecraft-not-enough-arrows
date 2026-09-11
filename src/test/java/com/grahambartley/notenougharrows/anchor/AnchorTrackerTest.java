package com.grahambartley.notenougharrows.anchor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

class AnchorTrackerTest {
  private static final UUID FIRST_OWNER = UUID.fromString("00000000-0000-0000-0000-0000000000a1");
  private static final UUID SECOND_OWNER = UUID.fromString("00000000-0000-0000-0000-0000000000a2");
  private static final UUID STRANGER = UUID.fromString("00000000-0000-0000-0000-0000000000a3");
  private static final BlockPos SHARED_POSITION = new BlockPos(0, 64, 0);
  private static final BlockPos OTHER_POSITION = new BlockPos(8, 64, 0);
  private static final Identifier STONE = Identifier.ofVanilla("stone");

  private AnchorTracker tracker;

  @BeforeEach
  void setUp() {
    tracker = new AnchorTracker();
  }

  @Test
  void startsWithNothingAnchored() {
    assertTrue(tracker.isEmpty());
    assertEquals(0, tracker.size());
    assertEquals(List.of(), tracker.anchors());
  }

  @ParameterizedTest
  @NullSource
  void aMissingAnchorIsNotTracked(final BlockAnchor anchor) {
    tracker.add(anchor);

    assertTrue(tracker.isEmpty());
  }

  @Test
  void aTrackedAnchorIsFoundByItsOwner() {
    final BlockAnchor anchor = anchorFor(FIRST_OWNER, SHARED_POSITION, 100L);
    tracker.add(anchor);

    assertSame(anchor, tracker.anchorOf(FIRST_OWNER));
    assertEquals(1, tracker.size());
  }

  @Test
  void anOwnerWithNoAnchorFindsNothing() {
    tracker.add(anchorFor(FIRST_OWNER, SHARED_POSITION, 100L));

    assertNull(tracker.anchorOf(STRANGER));
  }

  @ParameterizedTest
  @NullSource
  void anAnchorIsNeverFoundWithoutAnOwnerToLookUp(final UUID ownerId) {
    tracker.add(anchorFor(FIRST_OWNER, SHARED_POSITION, 100L));

    assertNull(tracker.anchorOf(ownerId));
    assertNull(tracker.remove(ownerId));
    assertEquals(1, tracker.size());
  }

  @Test
  void twoOwnersAnchoredToTheSameBlockHoldSeparateAnchors() {
    final BlockAnchor first = anchorFor(FIRST_OWNER, SHARED_POSITION, 100L);
    final BlockAnchor second = anchorFor(SECOND_OWNER, SHARED_POSITION, 200L);
    tracker.add(first);
    tracker.add(second);

    assertEquals(2, tracker.size());
    assertSame(first, tracker.anchorOf(FIRST_OWNER));
    assertSame(second, tracker.anchorOf(SECOND_OWNER));
  }

  @Test
  void releasingOneOwnersAnchorLeavesTheOtherAnchoredToTheSameBlock() {
    tracker.add(anchorFor(FIRST_OWNER, SHARED_POSITION, 100L));
    final BlockAnchor second = anchorFor(SECOND_OWNER, SHARED_POSITION, 100L);
    tracker.add(second);

    tracker.remove(FIRST_OWNER);

    assertEquals(List.of(second), tracker.anchors());
  }

  @Test
  void anOwnerAnchoringAgainReplacesTheAnchorTheyAlreadyHeld() {
    tracker.add(anchorFor(FIRST_OWNER, SHARED_POSITION, 100L));
    final BlockAnchor replacement = anchorFor(FIRST_OWNER, OTHER_POSITION, 200L);

    tracker.add(replacement);

    assertEquals(1, tracker.size());
    assertSame(replacement, tracker.anchorOf(FIRST_OWNER));
  }

  @Test
  void aReleasedAnchorIsHandedBackOnlyOnce() {
    final BlockAnchor anchor = anchorFor(FIRST_OWNER, SHARED_POSITION, 100L);
    tracker.add(anchor);

    assertSame(anchor, tracker.remove(FIRST_OWNER));
    assertNull(tracker.remove(FIRST_OWNER));
    assertTrue(tracker.isEmpty());
  }

  @Test
  void onlyTheAnchorsThatMatchAreTakenAway() {
    final BlockAnchor lost = anchorFor(FIRST_OWNER, SHARED_POSITION, 100L);
    final BlockAnchor kept = anchorFor(SECOND_OWNER, OTHER_POSITION, 100L);
    tracker.add(lost);
    tracker.add(kept);

    assertEquals(List.of(lost), tracker.takeIf(anchor -> anchor.pos().equals(SHARED_POSITION)));
    assertEquals(List.of(kept), tracker.anchors());
  }

  @Test
  void anAnchorTakenAwayIsHandedBackOnlyOnce() {
    tracker.add(anchorFor(FIRST_OWNER, SHARED_POSITION, 100L));

    assertEquals(1, tracker.takeIf(anchor -> anchor.hasExpired(150L)).size());
    assertEquals(List.of(), tracker.takeIf(anchor -> anchor.hasExpired(150L)));
    assertTrue(tracker.isEmpty());
  }

  @Test
  void nothingIsTakenAwayWhileEveryAnchorStillHolds() {
    tracker.add(anchorFor(FIRST_OWNER, SHARED_POSITION, 100L));

    assertEquals(List.of(), tracker.takeIf(anchor -> anchor.hasExpired(99L)));
    assertFalse(tracker.isEmpty());
  }

  @Test
  void anchorsAreHandedBackInTheOrderTheyWereAnchored() {
    final BlockAnchor first = anchorFor(FIRST_OWNER, SHARED_POSITION, 100L);
    final BlockAnchor second = anchorFor(SECOND_OWNER, OTHER_POSITION, 100L);
    tracker.add(first);
    tracker.add(second);

    assertEquals(List.of(first, second), tracker.takeIf(anchor -> true));
  }

  private static BlockAnchor anchorFor(
      final UUID ownerId, final BlockPos pos, final long expiryTick) {
    return new BlockAnchor(ownerId, pos, STONE, expiryTick);
  }
}
