package com.grahambartley.notenougharrows.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ModSoundPlayerTest {
  private static final SoundEvent SOUND =
      SoundEvent.of(Identifier.of(NotEnoughArrows.MOD_ID, "smoke_arrow_impact"));
  private static final Vec3d AT = new Vec3d(1.5, 64.0, -3.5);

  private static final List<Path> SOURCE_ROOTS =
      List.of(Path.of("src/main/java"), Path.of("src/client/java"));
  private static final Path GAMETESTS =
      Path.of("src/main/java/com/grahambartley/notenougharrows/gametest");
  private static final Path PLAYER =
      Path.of("src/main/java/com/grahambartley/notenougharrows/audio/ModSoundPlayer.java");
  private static final Pattern BYPASS =
      Pattern.compile(
          "\\.playSound(FromEntity|ToPlayer)?\\s*\\(|(?<![.\\w])playSound\\s*\\(|SoundEvents\\.");

  private record Sent(
      Vec3d at, SoundEvent sound, SoundCategory category, float volume, float pitch) {}

  private final List<Sent> sent = new ArrayList<>();
  private final ModSoundPlayer.SoundSink sink =
      (at, sound, category, volume, pitch) ->
          sent.add(new Sent(at, sound, category, volume, pitch));

  @ParameterizedTest(name = "volume {0} at server volume {1} is sent at {2}")
  @CsvSource({"1.0, 1.0, 1.0", "1.0, 0.5, 0.5", "0.8, 0.5, 0.4", "4.0, 0.25, 1.0", "2.0, 1.0, 2.0"})
  void sendsTheSoundScaledByTheServerVolume(
      final float volume, final float serverVolume, final float expected) {
    ModSoundPlayer.play(sink, serverVolume, AT, SOUND, SoundCategory.NEUTRAL, volume, 1.2f);

    assertEquals(List.of(new Sent(AT, SOUND, SoundCategory.NEUTRAL, expected, 1.2f)), sent);
  }

  @Test
  void keepsThePositionSoundCategoryAndPitchItWasGiven() {
    ModSoundPlayer.play(sink, 1.0f, AT, SOUND, SoundCategory.PLAYERS, 1.0f, 0.6f);

    assertEquals(List.of(new Sent(AT, SOUND, SoundCategory.PLAYERS, 1.0f, 0.6f)), sent);
  }

  @Test
  void sendsNothingWhenTheServerVolumeIsZero() {
    ModSoundPlayer.play(sink, 0.0f, AT, SOUND, SoundCategory.NEUTRAL, 1.0f, 1.0f);

    assertTrue(sent.isEmpty());
  }

  @ParameterizedTest(name = "volume {0}")
  @CsvSource({"0.0", "-1.0"})
  void sendsNothingForASoundWithNoVolume(final float volume) {
    ModSoundPlayer.play(sink, 1.0f, AT, SOUND, SoundCategory.NEUTRAL, volume, 1.0f);

    assertTrue(sent.isEmpty());
  }

  @ParameterizedTest(name = "rolls {0} and {1} pitch at {2}")
  @CsvSource({"0.5, 0.5, 0.7", "1.0, 0.0, 0.84", "0.0, 1.0, 0.56"})
  void anExplosionPitchSpreadsLikeVanillas(
      final float first, final float second, final float pitch) {
    final Random random = mock(Random.class);
    when(random.nextFloat()).thenReturn(first, second);

    assertEquals(pitch, ModSoundPlayer.explosionPitch(random), 1.0e-6f);
  }

  @Test
  void anExplosionIsAsLoudAsVanillas() {
    assertEquals(4.0f, ModSoundPlayer.EXPLOSION_VOLUME);
  }

  @Test
  void isTheOnlyPlaceTheModReachesForAVanillaSoundOrPlaysOne() {
    final List<Path> bypassing =
        SOURCE_ROOTS.stream()
            .flatMap(ModSoundPlayerTest::sources)
            .filter(path -> !path.startsWith(GAMETESTS))
            .filter(path -> !path.equals(PLAYER))
            .filter(ModSoundPlayerTest::bypasses)
            .toList();

    assertTrue(
        bypassing.isEmpty(),
        "These play or name a sound outside ModSoundPlayer, so a mod volume cannot reach it: "
            + bypassing);
  }

  @Test
  void theBypassCheckRecognisesTheCallsItIsLookingFor() {
    assertTrue(BYPASS.matcher("world.playSound(null, x, y, z").find());
    assertTrue(BYPASS.matcher("    playSound(SoundEvents.ENTITY_ARROW_HIT, 1f, 1f);").find());
    assertTrue(BYPASS.matcher("world.playSoundFromEntity(null, e").find());
    assertTrue(BYPASS.matcher("        SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST);").find());
    assertFalse(BYPASS.matcher("ModSoundPlayer.play(world, at").find());
    assertFalse(BYPASS.matcher("ModSoundPlayer.playFrom(this, sound").find());
    assertFalse(BYPASS.matcher("ModSounds.SMOKE_ARROW_IMPACT").find());
  }

  private static boolean bypasses(final Path source) {
    try {
      return BYPASS.matcher(Files.readString(source)).find();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static Stream<Path> sources(final Path root) {
    try (Stream<Path> walk = Files.walk(root)) {
      return walk.filter(path -> path.toString().endsWith(".java")).toList().stream();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
