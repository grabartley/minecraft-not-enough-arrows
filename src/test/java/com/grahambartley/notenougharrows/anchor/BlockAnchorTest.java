package com.grahambartley.notenougharrows.anchor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;

class BlockAnchorTest {
  private static final UUID OWNER = UUID.fromString("00000000-0000-0000-0000-0000000000a1");
  private static final BlockPos POSITION = new BlockPos(4, 64, -2);
  private static final Identifier STONE = Identifier.ofVanilla("stone");
  private static final Identifier DIRT = Identifier.ofVanilla("dirt");
  private static final long EXPIRY_TICK = 100L;

  @ParameterizedTest
  @CsvSource({"99, false", "100, true", "101, true"})
  void anAnchorExpiresOnceItsExpiryTickArrives(final long tick, final boolean expired) {
    assertEquals(expired, anchor().hasExpired(tick));
  }

  @Test
  void anAnchorHoldsOntoTheBlockItWasMadeOn() {
    assertTrue(anchor().holdsOnto(STONE));
  }

  @Test
  void anAnchorLosesItsGripWhenTheBlockBecomesAnother() {
    assertFalse(anchor().holdsOnto(DIRT));
  }

  @ParameterizedTest
  @NullSource
  void anAnchorLosesItsGripWhenThereIsNoBlockLeftToRead(final Identifier currentBlockId) {
    assertFalse(anchor().holdsOnto(currentBlockId));
  }

  @Test
  void anAnchorWithoutAnOwnerIsRejected() {
    assertThrows(
        NullPointerException.class, () -> new BlockAnchor(null, POSITION, STONE, EXPIRY_TICK));
  }

  @Test
  void anAnchorWithoutAPositionIsRejected() {
    assertThrows(
        NullPointerException.class, () -> new BlockAnchor(OWNER, null, STONE, EXPIRY_TICK));
  }

  @Test
  void anAnchorWithoutABlockIsRejected() {
    assertThrows(
        NullPointerException.class, () -> new BlockAnchor(OWNER, POSITION, null, EXPIRY_TICK));
  }

  @Test
  void anAnchorKeepsItsPositionWhenTheCallersCursorMovesOn() {
    final BlockPos.Mutable cursor = new BlockPos.Mutable(4, 64, -2);
    final BlockAnchor anchor = new BlockAnchor(OWNER, cursor, STONE, EXPIRY_TICK);

    cursor.set(9, 9, 9);

    assertEquals(POSITION, anchor.pos());
  }

  private static BlockAnchor anchor() {
    return new BlockAnchor(OWNER, POSITION, STONE, EXPIRY_TICK);
  }
}
