package com.grahambartley.notenougharrows.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SoundVolumeTest {

  @ParameterizedTest(name = "volume {0} at mod volume {1} plays at {2}")
  @CsvSource({
    "1.0, 1.0, 1.0",
    "1.0, 0.5, 0.5",
    "0.6, 0.5, 0.3",
    "2.0, 0.5, 1.0",
    "1.0, 0.0, 0.0",
    "0.0, 1.0, 0.0"
  })
  void scalesTheVolumeByTheModVolume(
      final float volume, final float modVolume, final float expected) {
    assertEquals(expected, SoundVolume.scale(volume, modVolume), 1.0e-6f);
  }

  @ParameterizedTest(name = "mod volume {0} acts as {1}")
  @CsvSource({"-0.5, 0.0", "1.5, 1.0", "7.0, 1.0"})
  void clampsAModVolumeOutsideItsRange(final float modVolume, final float effective) {
    assertEquals(effective, SoundVolume.scale(1.0f, modVolume), 1.0e-6f);
  }

  @ParameterizedTest(name = "volume {0}")
  @CsvSource({"-1.0", "-0.01"})
  void neverReturnsANegativeVolume(final float volume) {
    assertEquals(0.0f, SoundVolume.scale(volume, 1.0f));
  }
}
