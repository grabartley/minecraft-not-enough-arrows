package com.grahambartley.notenougharrows.control;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MobSteeringTest {

  @ParameterizedTest
  @CsvSource({"0, 1, 0", "-1, 0, 90", "0, -1, 180", "1, 0, -90"})
  void facesTheWayMinecraftMeasuresYaw(final double dx, final double dz, final float expected) {
    final float yaw = MobSteering.yawToward(Vec3d.ZERO, new Vec3d(dx, 0.0, dz));
    assertEquals(0.0f, Math.floorMod(Math.round(yaw - expected), 360), 1.0e-3f);
  }
}
