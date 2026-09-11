package com.grahambartley.notenougharrows.client.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ClientStatePathsTest {

  @Test
  void nestsTheModDirectoryUnderTheClientConfigDirectory() {
    final Path configDir = Path.of("run", "config");

    assertEquals(
        configDir.resolve("not-enough-arrows"), new ClientStatePaths(configDir).getModDir());
  }

  @Test
  void resolvesTheClientStateInsideTheModDirectory() {
    final ClientStatePaths paths = new ClientStatePaths(Path.of("run", "config"));

    assertEquals(paths.getModDir().resolve("client-state.json"), paths.getClientStatePath());
  }

  @Test
  void staysOutOfTheWorldSaveSoItSurvivesSwitchingWorlds() {
    final Path configDir = Path.of("run", "config");
    final Path statePath = new ClientStatePaths(configDir).getClientStatePath();

    assertEquals(configDir, statePath.getParent().getParent());
  }

  @Test
  void rejectsANullConfigDirectory() {
    assertThrows(NullPointerException.class, () -> new ClientStatePaths(null));
  }
}
