package com.grahambartley.notenougharrows.reveal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RevealSyncTest {

  @BeforeEach
  void setUp() {
    RevealSync.clear();
  }

  @Test
  void anOutlineLastsItsDuration() {
    RevealSync.acceptOutline(List.of(BlockPos.ORIGIN), 2);

    RevealSync.tick();
    assertEquals(List.of(List.of(BlockPos.ORIGIN)), RevealSync.outlines());

    RevealSync.tick();
    assertTrue(RevealSync.outlines().isEmpty());
  }

  @Test
  void aPathLastsItsLifetime() {
    final List<Vec3d> points = List.of(Vec3d.ZERO, new Vec3d(4, 1, 0));
    RevealSync.acceptPath(points, 1);

    assertEquals(List.of(points), RevealSync.paths());
    RevealSync.tick();
    assertTrue(RevealSync.paths().isEmpty());
  }

  @Test
  void holdsABoundedNumberOfPulses() {
    for (int pulse = 0; pulse < RevealSync.MAX_LIVE_PULSES + 3; pulse++) {
      RevealSync.acceptOutline(List.of(new BlockPos(pulse, 0, 0)), 100);
    }

    assertEquals(RevealSync.MAX_LIVE_PULSES, RevealSync.outlines().size());
  }

  @Test
  void disconnectingForgetsEverything() {
    RevealSync.acceptOutline(List.of(BlockPos.ORIGIN), 100);
    RevealSync.acceptPath(List.of(Vec3d.ZERO, new Vec3d(1, 0, 0)), 100);

    RevealSync.clear();

    assertTrue(RevealSync.outlines().isEmpty());
    assertTrue(RevealSync.paths().isEmpty());
  }
}
