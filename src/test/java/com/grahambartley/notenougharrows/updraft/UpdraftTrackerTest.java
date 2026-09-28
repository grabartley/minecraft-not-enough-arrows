package com.grahambartley.notenougharrows.updraft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdraftTrackerTest {
  private UpdraftTracker tracker;

  @BeforeEach
  void setUp() {
    tracker = new UpdraftTracker();
  }

  @Test
  void expiredColumnsStopTicking() {
    final LiveUpdraft early = updraft(10L);
    final LiveUpdraft late = updraft(20L);
    tracker.add(early);
    tracker.add(late);

    tracker.removeExpired(10L);

    assertEquals(List.of(late), tracker.live());
  }

  @Test
  void twoColumnsAtTheSamePlaceAreIndependent() {
    tracker.add(updraft(10L));
    tracker.add(updraft(10L));
    tracker.add(null);

    assertEquals(2, tracker.size());
  }

  @Test
  void startsEmpty() {
    assertTrue(tracker.isEmpty());
  }

  private static LiveUpdraft updraft(final long expiryTick) {
    return LiveUpdraft.opening(new UpdraftColumn(Vec3d.ZERO, 1.5, 12, 0.4f, expiryTick));
  }
}
