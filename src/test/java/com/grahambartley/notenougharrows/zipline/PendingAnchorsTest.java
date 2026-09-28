package com.grahambartley.notenougharrows.zipline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PendingAnchorsTest {
  private static final Identifier OVERWORLD = Identifier.of("minecraft", "overworld");
  private static final Identifier NETHER = Identifier.of("minecraft", "the_nether");
  private static final Identifier STONE = Identifier.of("minecraft", "stone");
  private static final UUID OWNER = UUID.randomUUID();
  private static final UUID OTHER_OWNER = UUID.randomUUID();

  private PendingAnchors pending;

  @BeforeEach
  void setUp() {
    pending = new PendingAnchors();
  }

  @Test
  void holdsOneAnchorPerPlayer() {
    final PendingAnchor anchor = anchor(OVERWORLD, 100L);

    assertTrue(pending.hold(OWNER, anchor).isEmpty());

    assertEquals(anchor, pending.of(OWNER).orElseThrow());
    assertEquals(1, pending.size());
  }

  @Test
  void aThirdShotReplacesThePendingAnchorRatherThanQueuingIt() {
    final PendingAnchor first = anchor(OVERWORLD, 100L);
    final PendingAnchor replacement = anchor(OVERWORLD, 200L);
    pending.hold(OWNER, first);

    assertEquals(first, pending.hold(OWNER, replacement).orElseThrow());

    assertEquals(replacement, pending.of(OWNER).orElseThrow());
    assertEquals(1, pending.size());
  }

  @Test
  void takingAnAnchorLeavesNothingPending() {
    final PendingAnchor anchor = anchor(OVERWORLD, 100L);
    pending.hold(OWNER, anchor);

    assertEquals(anchor, pending.take(OWNER).orElseThrow());
    assertTrue(pending.take(OWNER).isEmpty());
    assertTrue(pending.isEmpty());
  }

  @Test
  void playersHoldTheirAnchorsIndependently() {
    pending.hold(OWNER, anchor(OVERWORLD, 100L));
    pending.hold(OTHER_OWNER, anchor(OVERWORLD, 100L));

    pending.take(OWNER);

    assertTrue(pending.of(OTHER_OWNER).isPresent());
  }

  @Test
  void expiryOnlyDropsAnchorsInTheWorldBeingTicked() {
    pending.hold(OWNER, anchor(OVERWORLD, 100L));
    pending.hold(OTHER_OWNER, anchor(NETHER, 100L));

    assertEquals(1, pending.dropExpiredIn(OVERWORLD, 100L));

    assertTrue(pending.of(OWNER).isEmpty());
    assertTrue(pending.of(OTHER_OWNER).isPresent());
  }

  @Test
  void anAnchorOutlivesTheTicksBeforeItsExpiry() {
    pending.hold(OWNER, anchor(OVERWORLD, 100L));

    assertEquals(0, pending.dropExpiredIn(OVERWORLD, 99L));
    assertTrue(pending.of(OWNER).isPresent());
  }

  @Test
  void missingOwnersAndAnchorsAreIgnored() {
    assertTrue(pending.hold(null, anchor(OVERWORLD, 1L)).isEmpty());
    assertTrue(pending.hold(OWNER, null).isEmpty());
    assertTrue(pending.take(null).isEmpty());
    assertTrue(pending.of(null).isEmpty());
    assertTrue(pending.isEmpty());
  }

  @Test
  void clearingDropsEveryAnchor() {
    pending.hold(OWNER, anchor(OVERWORLD, 100L));
    pending.hold(OTHER_OWNER, anchor(NETHER, 100L));

    pending.clear();

    assertTrue(pending.isEmpty());
  }

  private static PendingAnchor anchor(final Identifier world, final long expiryTick) {
    return new PendingAnchor(world, new BlockPos(0, 64, 0), STONE, UUID.randomUUID(), expiryTick);
  }
}
