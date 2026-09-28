package com.grahambartley.notenougharrows.zipline;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;

class PendingAnchorTest {
  private static final Identifier OVERWORLD = Identifier.of("minecraft", "overworld");
  private static final Identifier NETHER = Identifier.of("minecraft", "the_nether");
  private static final Identifier STONE = Identifier.of("minecraft", "stone");
  private static final Identifier DIRT = Identifier.of("minecraft", "dirt");

  @Test
  void itExpiresOnItsExpiryTick() {
    final PendingAnchor anchor = anchor(50L);

    assertFalse(anchor.hasExpired(49L));
    assertTrue(anchor.hasExpired(50L));
  }

  @Test
  void itBelongsToTheWorldItWasSetIn() {
    final PendingAnchor anchor = anchor(50L);

    assertTrue(anchor.isIn(OVERWORLD));
    assertFalse(anchor.isIn(NETHER));
  }

  @Test
  void itHoldsOntoTheBlockItWasSetInOnly() {
    final PendingAnchor anchor = anchor(50L);

    assertTrue(anchor.holdsOnto(STONE));
    assertFalse(anchor.holdsOnto(DIRT));
    assertFalse(anchor.holdsOnto(null));
  }

  @Test
  void itNeedsEveryPart() {
    final BlockPos pos = BlockPos.ORIGIN;
    final UUID arrow = UUID.randomUUID();
    assertThrows(NullPointerException.class, () -> new PendingAnchor(null, pos, STONE, arrow, 1L));
    assertThrows(
        NullPointerException.class, () -> new PendingAnchor(OVERWORLD, null, STONE, arrow, 1L));
    assertThrows(
        NullPointerException.class, () -> new PendingAnchor(OVERWORLD, pos, null, arrow, 1L));
    assertThrows(
        NullPointerException.class, () -> new PendingAnchor(OVERWORLD, pos, STONE, null, 1L));
  }

  private static PendingAnchor anchor(final long expiryTick) {
    return new PendingAnchor(
        OVERWORLD, new BlockPos(0, 64, 0), STONE, UUID.randomUUID(), expiryTick);
  }
}
