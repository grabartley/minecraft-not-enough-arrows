package com.grahambartley.notenougharrows.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.minecraft.util.math.random.Random;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ModExplosionTest {

  @ParameterizedTest(name = "rolls {0} and {1} pitch at {2}")
  @CsvSource({"0.5, 0.5, 0.7", "1.0, 0.0, 0.84", "0.0, 1.0, 0.56", "0.75, 0.25, 0.77"})
  void spreadsItsPitchTheWayVanillaExplosionsDo(
      final float first, final float second, final float pitch) {
    final Random random = mock(Random.class);
    when(random.nextFloat()).thenReturn(first, second);

    assertEquals(pitch, ModExplosion.pitch(random), 1.0e-6f);
  }
}
