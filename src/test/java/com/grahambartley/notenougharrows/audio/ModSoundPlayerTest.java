package com.grahambartley.notenougharrows.audio;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class ModSoundPlayerTest {
  private static final List<Path> SOURCE_ROOTS =
      List.of(Path.of("src/main/java"), Path.of("src/client/java"));
  private static final Path GAMETESTS =
      Path.of("src/main/java/com/grahambartley/notenougharrows/gametest");
  private static final Path PLAYER =
      Path.of("src/main/java/com/grahambartley/notenougharrows/audio/ModSoundPlayer.java");
  private static final Pattern DIRECT_PLAY =
      Pattern.compile("\\.playSound(FromEntity|ToPlayer)?\\s*\\(|(?<![.\\w])playSound\\s*\\(");

  private static boolean playsDirectly(final Path source) {
    try {
      return DIRECT_PLAY.matcher(Files.readString(source)).find();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static Stream<Path> sources(final Path root) {
    try {
      return Files.walk(root).filter(path -> path.toString().endsWith(".java")).toList().stream();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Test
  void isTheOnlyPlaceTheModPlaysASound() {
    final List<Path> bypassing =
        SOURCE_ROOTS.stream()
            .flatMap(ModSoundPlayerTest::sources)
            .filter(path -> !path.startsWith(GAMETESTS))
            .filter(path -> !path.equals(PLAYER))
            .filter(ModSoundPlayerTest::playsDirectly)
            .toList();

    assertTrue(
        bypassing.isEmpty(),
        "These play a sound without ModSoundPlayer, so sound.volume cannot reach them: "
            + bypassing);
  }

  @Test
  void theCheckRecognisesTheCallsItIsLookingFor() {
    assertTrue(DIRECT_PLAY.matcher("world.playSound(null, x, y, z").find());
    assertTrue(DIRECT_PLAY.matcher("    playSound(SoundEvents.ENTITY_ARROW_HIT, 1f, 1f);").find());
    assertTrue(DIRECT_PLAY.matcher("world.playSoundFromEntity(null, e").find());
    assertTrue(!DIRECT_PLAY.matcher("ModSoundPlayer.play(world, at").find());
    assertTrue(!DIRECT_PLAY.matcher("ModSoundPlayer.playFrom(this, sound").find());
  }
}
