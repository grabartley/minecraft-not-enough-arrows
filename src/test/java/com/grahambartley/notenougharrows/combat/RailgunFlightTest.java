package com.grahambartley.notenougharrows.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.config.RailgunArrowConfig;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class RailgunFlightTest {
  private static final double TOLERANCE = 1.0e-9;
  private static final double VANILLA_ARROW_GRAVITY = 0.05;

  @ParameterizedTest
  @CsvSource({"1.0, 3.0", "2.0, 6.0", "3.0, 9.0"})
  void multipliesTheLaunchSpeed(final float multiplier, final double expected) {
    final Vec3d launched = RailgunFlight.launchVelocity(new Vec3d(3.0, 0.0, 0.0), multiplier);

    assertEquals(expected, launched.x, TOLERANCE);
  }

  @Test
  void keepsTheDirectionItWasFiredIn() {
    final Vec3d fired = new Vec3d(1.0, 2.0, -3.0);

    final Vec3d launched = RailgunFlight.launchVelocity(fired, 4.0f);

    assertEquals(0.0, launched.normalize().subtract(fired.normalize()).length(), TOLERANCE);
  }

  @ParameterizedTest
  @ValueSource(
      floats = {
        RailgunArrowConfig.SPEED_MULTIPLIER_MIN,
        RailgunArrowConfig.DEFAULT_SPEED_MULTIPLIER,
        RailgunArrowConfig.SPEED_MULTIPLIER_MAX
      })
  void everyMultiplierTheConfigurationCanProduceLaunchesAtLeastAsFastAsAnOrdinaryArrow(
      final float multiplier) {
    final Vec3d fired = new Vec3d(2.0, 0.0, 0.0);

    assertTrue(
        RailgunFlight.launchVelocity(fired, multiplier).length() >= fired.length() - TOLERANCE,
        "A railgun arrow should never leave the bow slower than an ordinary arrow");
  }

  @Test
  void aMissingVelocityLaunchesNothing() {
    assertEquals(Vec3d.ZERO, RailgunFlight.launchVelocity(null, 3.0f));
  }

  @ParameterizedTest
  @CsvSource({"1.0, 0.05", "0.5, 0.025", "0.25, 0.0125", "2.0, 0.1"})
  void scalesGravityByTheConfiguredFactor(final float factor, final double expected) {
    assertEquals(expected, RailgunFlight.gravity(VANILLA_ARROW_GRAVITY, factor), TOLERANCE);
  }

  @ParameterizedTest
  @ValueSource(
      floats = {
        RailgunArrowConfig.GRAVITY_FACTOR_MIN,
        RailgunArrowConfig.DEFAULT_GRAVITY_FACTOR,
        RailgunArrowConfig.GRAVITY_FACTOR_MAX
      })
  void everyFactorTheConfigurationCanProduceStillPullsTheArrowDown(final float factor) {
    final double gravity = RailgunFlight.gravity(VANILLA_ARROW_GRAVITY, factor);

    assertTrue(gravity > 0.0, "A railgun arrow must always fall, gravity was " + gravity);
  }

  @Test
  void theLightestSettingStillFallsFasterThanNothing() {
    final float lightest = new RailgunArrowConfig(3.0f, 0.0f).gravityFactor();

    assertTrue(
        RailgunFlight.gravity(VANILLA_ARROW_GRAVITY, lightest) > 0.0,
        "The configuration must never hand the flight a factor that stops the arrow falling");
  }
}
