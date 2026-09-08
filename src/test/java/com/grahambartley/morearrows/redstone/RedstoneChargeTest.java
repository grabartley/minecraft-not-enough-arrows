package com.grahambartley.morearrows.redstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RedstoneChargeTest {
  private static final BlockPos POSITION = new BlockPos(4, 64, -2);

  @ParameterizedTest
  @CsvSource({"99, false", "100, true", "101, true"})
  void aChargeExpiresOnceTheWorldClockReachesItsExpiryTick(final long tick, final boolean expired) {
    assertEquals(expired, new RedstoneCharge(POSITION, 100L).hasExpired(tick));
  }

  @Test
  void aChargeKeepsThePositionItWasGiven() {
    assertEquals(POSITION, new RedstoneCharge(POSITION, 100L).pos());
  }

  @Test
  void aMutablePositionIsStoredAsAnImmutableCopy() {
    final BlockPos.Mutable mutable = new BlockPos.Mutable(4, 64, -2);

    final RedstoneCharge charge = new RedstoneCharge(mutable, 100L);
    mutable.set(99, 99, 99);

    assertEquals(POSITION, charge.pos());
  }

  @Test
  void aChargeWithoutAPositionIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> new RedstoneCharge(null, 100L));
  }

  @Test
  void aChargeThatNeverExpiresOutlastsTheWorldClock() {
    assertFalse(new RedstoneCharge(POSITION, Long.MAX_VALUE).hasExpired(Long.MAX_VALUE - 1));
    assertTrue(new RedstoneCharge(POSITION, Long.MAX_VALUE).hasExpired(Long.MAX_VALUE));
  }
}
