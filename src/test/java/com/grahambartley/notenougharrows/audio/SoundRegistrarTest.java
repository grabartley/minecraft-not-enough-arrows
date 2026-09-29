package com.grahambartley.notenougharrows.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SoundRegistrarTest {

  private static Identifier sound(final String path) {
    return Identifier.of(NotEnoughArrows.MOD_ID, path);
  }

  @Test
  void namespacesADeclaredSoundUnderTheModId() {
    final SoundEvent event = new SoundRegistrar().declare("smoke_arrow_impact");

    assertEquals(sound("smoke_arrow_impact"), event.getId());
  }

  @Test
  void listsDeclaredSoundsInDeclarationOrder() {
    final SoundRegistrar registrar = new SoundRegistrar();
    registrar.declare("countdown_beep");
    registrar.declare("smoke_arrow_impact");
    registrar.declare("ender_teleport");

    assertEquals(
        List.of(sound("countdown_beep"), sound("smoke_arrow_impact"), sound("ender_teleport")),
        registrar.ids());
  }

  @Test
  void writesEveryDeclaredSoundToTheRegistryUnderItsOwnId() {
    final SoundRegistrar registrar = new SoundRegistrar();
    final SoundEvent beep = registrar.declare("countdown_beep");
    final SoundEvent smoke = registrar.declare("smoke_arrow_impact");
    final Map<Identifier, SoundEvent> registry = new LinkedHashMap<>();

    registrar.registerInto(registry::put);

    assertEquals(List.of(beep.getId(), smoke.getId()), List.copyOf(registry.keySet()));
    assertSame(beep, registry.get(beep.getId()));
    assertSame(smoke, registry.get(smoke.getId()));
  }

  @Test
  void writesNothingWhenNothingWasDeclared() {
    final Map<Identifier, SoundEvent> registry = new LinkedHashMap<>();

    new SoundRegistrar().registerInto(registry::put);

    assertTrue(registry.isEmpty());
  }

  @Test
  void rejectsTheSameSoundDeclaredTwice() {
    final SoundRegistrar registrar = new SoundRegistrar();
    registrar.declare("countdown_beep");

    assertThrows(IllegalArgumentException.class, () -> registrar.declare("countdown_beep"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "Beep", "countdown-beep", "countdown beep", "sounds/beep", "a.b"})
  void rejectsAPathTheResourcePackCouldNotName(final String path) {
    assertThrows(IllegalArgumentException.class, () -> new SoundRegistrar().declare(path));
  }

  @Test
  void rejectsANullPath() {
    assertThrows(NullPointerException.class, () -> new SoundRegistrar().declare(null));
  }

  @Test
  void rejectsASoundDeclaredAfterTheRegistryWasWritten() {
    final SoundRegistrar registrar = new SoundRegistrar();
    registrar.registerInto((id, event) -> {});

    assertThrows(IllegalStateException.class, () -> registrar.declare("late_sound"));
  }

  @Test
  void rejectsWritingToTheRegistryTwice() {
    final SoundRegistrar registrar = new SoundRegistrar();
    registrar.declare("countdown_beep");
    registrar.registerInto((id, event) -> {});

    assertThrows(IllegalStateException.class, () -> registrar.registerInto((id, event) -> {}));
  }

  @Test
  void rejectsANullRegistry() {
    assertThrows(NullPointerException.class, () -> new SoundRegistrar().registerInto(null));
  }

  @Test
  void aSoundDeclaredWithAReachTravelsThatFarAtAnyVolume() {
    final SoundEvent beep = new SoundRegistrar().declareReaching("countdown_beep", 48.0f);

    assertEquals(48.0f, beep.getDistanceToTravel(0.5f));
    assertEquals(48.0f, beep.getDistanceToTravel(2.0f));
  }

  @Test
  void aSoundDeclaredWithoutAReachTravelsByVolume() {
    final SoundEvent smoke = new SoundRegistrar().declare("smoke_arrow_impact");

    assertEquals(16.0f, smoke.getDistanceToTravel(1.0f));
    assertEquals(32.0f, smoke.getDistanceToTravel(2.0f));
  }

  @ParameterizedTest
  @ValueSource(floats = {0.0f, -16.0f, Float.NaN})
  void refusesAReachThatIsNotPositive(final float blocks) {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SoundRegistrar().declareReaching("countdown_beep", blocks));
  }

  @Test
  void aSoundDeclaredWithAReachStillCannotBeDeclaredTwice() {
    final SoundRegistrar registrar = new SoundRegistrar();
    registrar.declare("countdown_beep");

    assertThrows(
        IllegalArgumentException.class, () -> registrar.declareReaching("countdown_beep", 48.0f));
  }
}
