package com.grahambartley.notenougharrows.sound;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import net.minecraft.util.Identifier;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ModSoundVolumeTest {
  private static final Identifier MOD_SOUND =
      Identifier.of(NotEnoughArrows.MOD_ID, "smoke_arrow_impact");
  private static final Identifier VANILLA_SOUND = Identifier.ofVanilla("block.fire.extinguish");
  private static final Identifier OTHER_MOD_SOUND = Identifier.of("another-mod", "smoke");

  @ParameterizedTest(name = "volume {0} at mod volume {1} plays at {2}")
  @CsvSource({"1.0, 1.0, 1.0", "1.0, 0.5, 0.5", "0.8, 0.5, 0.4", "1.0, 0.0, 0.0"})
  void scalesTheModsOwnSoundsByTheModVolume(
      final float volume, final float modVolume, final float expected) {
    assertEquals(expected, ModSoundVolume.adjust(MOD_SOUND, volume, modVolume), 1.0e-6f);
  }

  @ParameterizedTest(name = "mod volume {0}")
  @CsvSource({"0.0", "0.5", "1.0"})
  void leavesTheGamesOwnSoundsAlone(final float modVolume) {
    assertEquals(0.7f, ModSoundVolume.adjust(VANILLA_SOUND, 0.7f, modVolume));
  }

  @ParameterizedTest(name = "mod volume {0}")
  @CsvSource({"0.0", "0.5", "1.0"})
  void leavesAnotherModsSoundsAlone(final float modVolume) {
    assertEquals(0.7f, ModSoundVolume.adjust(OTHER_MOD_SOUND, 0.7f, modVolume));
  }
}
