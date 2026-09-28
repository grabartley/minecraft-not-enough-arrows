package com.grahambartley.notenougharrows.updraft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class LiveUpdraftTest {
  private static final UpdraftColumn COLUMN = new UpdraftColumn(Vec3d.ZERO, 1.5, 12, 0.4f, 10L);

  @Test
  void aColumnOpensWithNobodyInItYet() {
    final LiveUpdraft first = LiveUpdraft.opening(COLUMN);
    final LiveUpdraft second = LiveUpdraft.opening(COLUMN);

    assertEquals(COLUMN, first.column());
    assertNotSame(first.riders(), second.riders());
  }

  @Test
  void itNeedsItsColumnAndRiders() {
    assertThrows(NullPointerException.class, () -> new LiveUpdraft(null, new UpdraftRiders()));
    assertThrows(NullPointerException.class, () -> new LiveUpdraft(COLUMN, null));
  }
}
