package com.grahambartley.notenougharrows.countdown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CountdownKindTest {

  @Test
  void aFuseRingShowsOnlyWhenLookedAtAndSitsBesideTheArrow() {
    assertTrue(CountdownKind.FUSE.showsOnlyWhenLookedAt());
    assertFalse(CountdownKind.FUSE.sitsAboveTheHead());
  }

  @Test
  void anAllegianceRingAlwaysShowsAboveTheMobsHead() {
    assertFalse(CountdownKind.ALLEGIANCE.showsOnlyWhenLookedAt());
    assertTrue(CountdownKind.ALLEGIANCE.sitsAboveTheHead());
  }

  @Test
  void readsEveryKindBackFromItsOrdinal() {
    for (final CountdownKind kind : CountdownKind.values()) {
      assertEquals(kind, CountdownKind.fromOrdinal(kind.ordinal()));
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, 2, 99})
  void treatsAnUnknownOrdinalAsAFuse(final int ordinal) {
    assertEquals(CountdownKind.FUSE, CountdownKind.fromOrdinal(ordinal));
  }
}
