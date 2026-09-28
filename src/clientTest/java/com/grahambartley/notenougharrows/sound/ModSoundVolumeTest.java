package com.grahambartley.notenougharrows.sound;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import net.minecraft.util.Identifier;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ModSoundVolumeTest {
  private static final Identifier MOD_SOUND =
      Identifier.of(NotEnoughArrows.MOD_ID, "explosive_arrow_blast");
  private static final Identifier VANILLA_SOUND = Identifier.ofVanilla("entity.generic.explode");
  private static final Identifier OTHER_MOD_SOUND = Identifier.of("another-mod", "boom");

  @ParameterizedTest(name = "{0} at server {1} and client {2} plays at {3}")
  @CsvSource({
    "1.0, 1.0, 1.0, 1.0",
    "1.0, 0.5, 1.0, 0.5",
    "1.0, 1.0, 0.5, 0.5",
    "0.8, 0.5, 0.5, 0.2",
    "1.0, 0.0, 1.0, 0.0",
    "1.0, 1.0, 0.0, 0.0"
  })
  void scalesTheModsOwnSoundsByBothVolumes(
      final float adjusted, final float server, final float client, final float expected) {
    assertEquals(expected, ModSoundVolume.adjust(MOD_SOUND, adjusted, server, client), 1.0e-6f);
  }

  @ParameterizedTest(name = "server {0} and client {1} act as {2}")
  @CsvSource({"-1.0, 1.0, 0.0", "3.0, 1.0, 1.0", "1.0, -0.5, 0.0", "1.0, 2.0, 1.0"})
  void clampsAVolumeOutsideItsRange(final float server, final float client, final float expected) {
    assertEquals(expected, ModSoundVolume.adjust(MOD_SOUND, 1.0f, server, client), 1.0e-6f);
  }

  @ParameterizedTest(name = "server {0} and client {1}")
  @CsvSource({"0.0, 0.0", "0.5, 0.5", "1.0, 1.0"})
  void leavesTheGamesOwnSoundsAlone(final float server, final float client) {
    assertEquals(0.7f, ModSoundVolume.adjust(VANILLA_SOUND, 0.7f, server, client));
  }

  @ParameterizedTest(name = "server {0} and client {1}")
  @CsvSource({"0.0, 0.0", "0.5, 0.5", "1.0, 1.0"})
  void leavesAnotherModsSoundsAlone(final float server, final float client) {
    assertEquals(0.7f, ModSoundVolume.adjust(OTHER_MOD_SOUND, 0.7f, server, client));
  }
}
