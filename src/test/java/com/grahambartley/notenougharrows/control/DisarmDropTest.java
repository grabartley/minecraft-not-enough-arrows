package com.grahambartley.notenougharrows.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DisarmDropTest {

  private static final Vec3d EAST = new Vec3d(1.0, 0.0, 0.0);

  @Test
  void throwsTheItemAlongTheDirectionAwayFromTheShooter() {
    final Vec3d velocity = DisarmDrop.throwVelocity(EAST, 5.0);

    assertTrue(velocity.getX() > 0.0, "an eastward push should carry the item east");
    assertEquals(0.0, velocity.getZ(), 1.0e-9);
  }

  @Test
  void neverThrowsTheItemBackTowardTheShooter() {
    final Vec3d awayFromShooter = new Vec3d(3.0, 0.0, -4.0);
    final Vec3d velocity = DisarmDrop.throwVelocity(awayFromShooter, 5.0);

    assertTrue(
        velocity.getX() * awayFromShooter.getX() + velocity.getZ() * awayFromShooter.getZ() > 0.0,
        "the throw must share a direction with away-from-the-shooter");
  }

  @Test
  void liftsTheItemSoItArcsRatherThanSliding() {
    assertEquals(DisarmDrop.ARC_LIFT, DisarmDrop.throwVelocity(EAST, 5.0).getY());
  }

  @Test
  void aFurtherThrowLeavesFaster() {
    assertTrue(
        DisarmDrop.throwVelocity(EAST, 10.0).horizontalLength()
            > DisarmDrop.throwVelocity(EAST, 2.0).horizontalLength());
  }

  @Test
  void scalesTheThrowByTheConfiguredDistance() {
    assertEquals(
        5.0 * DisarmDrop.SPEED_PER_BLOCK,
        DisarmDrop.throwVelocity(EAST, 5.0).horizontalLength(),
        1.0e-9);
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.0, -1.0})
  void aThrowOfNothingDropsTheItemWhereItStood(final double distance) {
    assertEquals(Vec3d.ZERO, DisarmDrop.throwVelocity(EAST, distance));
  }

  @Test
  void aDirectionOfNowhereDropsTheItemWhereItStood() {
    assertEquals(Vec3d.ZERO, DisarmDrop.throwVelocity(null, 5.0));
    assertEquals(Vec3d.ZERO, DisarmDrop.throwVelocity(new Vec3d(0.0, 1.0, 0.0), 5.0));
  }
}
