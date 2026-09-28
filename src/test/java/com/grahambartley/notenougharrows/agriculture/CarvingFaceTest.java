package com.grahambartley.notenougharrows.agriculture;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

class CarvingFaceTest {

  @ParameterizedTest
  @CsvSource({
    "1.0, -0.5, 0.2, WEST",
    "-1.0, 0.0, 0.3, EAST",
    "0.2, -2.0, 1.0, NORTH",
    "0.1, 0.0, -1.0, SOUTH"
  })
  void facesBackAlongTheArrowsHorizontalFlight(
      final double x, final double y, final double z, final Direction expected) {
    assertEquals(expected, CarvingFace.towardTheShooter(new Vec3d(x, y, z)));
  }

  @ParameterizedTest
  @EnumSource(
      value = Direction.class,
      names = {"NORTH", "SOUTH", "EAST", "WEST"})
  void aSideHitCarvesTheFaceThatWasStruck(final Direction struck) {
    assertEquals(struck, CarvingFace.carved(struck, Direction.NORTH));
  }

  @ParameterizedTest
  @EnumSource(
      value = Direction.class,
      names = {"UP", "DOWN"})
  void aTopOrBottomHitCarvesTheFaceTowardTheShooter(final Direction struck) {
    assertEquals(Direction.EAST, CarvingFace.carved(struck, Direction.EAST));
  }
}
