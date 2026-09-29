package com.grahambartley.notenougharrows.discovery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class TracerPathTest {

  @Test
  void keepsThePointsInTheOrderTheArrowFlewThem() {
    final TracerPath path = new TracerPath();
    path.record(new Vec3d(0, 64, 0));
    path.record(new Vec3d(1, 64.5, 0));
    path.record(new Vec3d(2, 64.8, 0));

    assertEquals(
        List.of(new Vec3d(0, 64, 0), new Vec3d(1, 64.5, 0), new Vec3d(2, 64.8, 0)), path.points());
  }

  @Test
  void skipsAPointTheArrowBarelyMovedTo() {
    final TracerPath path = new TracerPath();
    path.record(new Vec3d(0, 64, 0));
    path.record(new Vec3d(0.05, 64, 0));

    assertEquals(1, path.points().size());
  }

  @Test
  void stopsRecordingAtItsLimit() {
    final TracerPath path = new TracerPath();
    for (int step = 0; step < TracerPath.MAX_POINTS + 50; step++) {
      path.record(new Vec3d(step, 64, 0));
    }

    assertEquals(TracerPath.MAX_POINTS, path.points().size());
    assertEquals(new Vec3d(0, 64, 0), path.points().getFirst());
  }

  @Test
  void needsTwoPointsToDrawALine() {
    final TracerPath path = new TracerPath();
    assertFalse(path.isDrawable());
    path.record(new Vec3d(0, 64, 0));
    assertFalse(path.isDrawable());
    path.record(new Vec3d(3, 64, 0));
    assertTrue(path.isDrawable());
  }

  @Test
  void ignoresANullPoint() {
    final TracerPath path = new TracerPath();
    path.record(null);

    assertTrue(path.points().isEmpty());
  }
}
