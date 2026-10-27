package com.grahambartley.volley.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FaceClearanceTest {
  private static final Vec3d HIT = new Vec3d(1.0, 2.0, 3.0);

  @Test
  void stepsOutOfTheStruckFaceTowardTheShooter() {
    assertEquals(new Vec3d(0.75, 2.0, 3.0), FaceClearance.inFrontOf(HIT, Direction.WEST));
    assertEquals(new Vec3d(1.0, 2.25, 3.0), FaceClearance.inFrontOf(HIT, Direction.UP));
    assertEquals(new Vec3d(1.0, 2.0, 3.25), FaceClearance.inFrontOf(HIT, Direction.SOUTH));
  }

  @ParameterizedTest
  @CsvSource({"UP, 4, 65, 4", "DOWN, 4, 63, 4", "NORTH, 4, 64, 3", "EAST, 5, 64, 4"})
  void theBlockInFrontOfAHitIsTheOneOnTheStruckFace(
      final Direction face, final int x, final int y, final int z) {
    final BlockHitResult hit = new BlockHitResult(Vec3d.ZERO, face, new BlockPos(4, 64, 4), false);

    assertEquals(new BlockPos(x, y, z), FaceClearance.blockInFrontOf(hit));
    assertEquals(Vec3d.ofBottomCenter(new BlockPos(x, y, z)), FaceClearance.groundInFrontOf(hit));
  }
}
