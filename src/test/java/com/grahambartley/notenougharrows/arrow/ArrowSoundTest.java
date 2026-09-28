package com.grahambartley.notenougharrows.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import java.util.Optional;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ArrowSoundTest {
  private static final Identifier BEEP = Identifier.of(NotEnoughArrows.MOD_ID, "countdown_beep");

  @Test
  void anOwnSoundBelongsToNoSharedSystem() {
    final ArrowSound sound = ArrowSound.own(BEEP);

    assertEquals(BEEP, sound.sound());
    assertTrue(sound.sharedSystem().isEmpty());
  }

  @Test
  void aSharedSoundNamesTheSystemThatProducesIt() {
    final ArrowSound sound = ArrowSound.sharedBy("explosive_fuse", BEEP);

    assertEquals(BEEP, sound.sound());
    assertEquals(Optional.of("explosive_fuse"), sound.sharedSystem());
  }

  @Test
  void rejectsANullSound() {
    assertThrows(NullPointerException.class, () -> ArrowSound.own(null));
    assertThrows(NullPointerException.class, () -> ArrowSound.sharedBy("explosive_fuse", null));
  }

  @Test
  void rejectsANullSystemForASharedSound() {
    assertThrows(NullPointerException.class, () -> ArrowSound.sharedBy(null, BEEP));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "\t"})
  void rejectsABlankSystemName(final String system) {
    assertThrows(IllegalArgumentException.class, () -> ArrowSound.sharedBy(system, BEEP));
  }
}
