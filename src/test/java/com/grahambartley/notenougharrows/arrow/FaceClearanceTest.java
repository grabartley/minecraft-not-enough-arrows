package com.grahambartley.notenougharrows.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class FaceClearanceTest {
  private static final Vec3d HIT = new Vec3d(1.0, 2.0, 3.0);

  @Test
  void stepsOutOfTheStruckFaceTowardTheShooter() {
    assertEquals(new Vec3d(0.75, 2.0, 3.0), FaceClearance.inFrontOf(HIT, Direction.WEST));
    assertEquals(new Vec3d(1.0, 2.25, 3.0), FaceClearance.inFrontOf(HIT, Direction.UP));
    assertEquals(new Vec3d(1.0, 2.0, 3.25), FaceClearance.inFrontOf(HIT, Direction.SOUTH));
  }
}
