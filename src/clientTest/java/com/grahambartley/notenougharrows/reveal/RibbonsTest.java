package com.grahambartley.notenougharrows.reveal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class RibbonsTest {
  private static final double EPSILON = 1.0E-9;

  @Test
  void widensASegmentAcrossTheLineOfSight() {
    final Vec3d side = Ribbons.side(Vec3d.ZERO, new Vec3d(10, 0, 0), new Vec3d(5, 0, 10), 0.5);

    assertEquals(0.5, side.length(), EPSILON);
    assertEquals(0.0, side.dotProduct(new Vec3d(1, 0, 0)), EPSILON);
    assertEquals(0.0, side.dotProduct(new Vec3d(5, 0, 10)), EPSILON);
  }

  @Test
  void aSegmentSeenEndOnHasNoWidth() {
    assertEquals(
        Vec3d.ZERO, Ribbons.side(Vec3d.ZERO, new Vec3d(10, 0, 0), new Vec3d(20, 0, 0), 0.5));
  }

  @Test
  void aSegmentOfNoLengthHasNoWidth() {
    assertEquals(Vec3d.ZERO, Ribbons.side(Vec3d.ZERO, Vec3d.ZERO, new Vec3d(0, 5, 0), 0.5));
  }
}
