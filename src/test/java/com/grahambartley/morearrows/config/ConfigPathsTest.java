package com.grahambartley.morearrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ConfigPathsTest {

  @Test
  void nestsTheModDirectoryUnderTheWorldDirectory() {
    final Path worldDir = Path.of("saves", "New World");

    assertEquals(worldDir.resolve("more-arrows"), new ConfigPaths(worldDir).getModDir());
  }

  @Test
  void resolvesTheServerConfigInsideTheModDirectory() {
    final ConfigPaths paths = new ConfigPaths(Path.of("saves", "New World"));

    assertEquals(paths.getModDir().resolve("server-config.json"), paths.getServerConfigPath());
  }

  @Test
  void givesTwoWorldsIndependentConfigPaths() {
    final Path first = new ConfigPaths(Path.of("saves", "World A")).getServerConfigPath();
    final Path second = new ConfigPaths(Path.of("saves", "World B")).getServerConfigPath();

    assertNotEquals(first, second);
  }

  @Test
  void rejectsANullWorldDirectory() {
    assertThrows(NullPointerException.class, () -> new ConfigPaths(null));
  }

  @Test
  void namesTheBrokenBackupBesideTheOriginal() {
    final Path original = Path.of("saves", "New World", "more-arrows", "server-config.json");
    final Path backup =
        ConfigPaths.brokenBackupPath(original, Instant.parse("2026-08-22T21:30:00Z"));

    assertEquals(original.getParent(), backup.getParent());
    assertEquals("server-config.broken.2026-08-22T21-30-00Z.json", backup.getFileName().toString());
  }

  @Test
  void keepsBrokenBackupsFromCollidingAcrossTimestamps() {
    final Path original = Path.of("server-config.json");

    assertNotEquals(
        ConfigPaths.brokenBackupPath(original, Instant.parse("2026-08-22T21:30:00Z")),
        ConfigPaths.brokenBackupPath(original, Instant.parse("2026-08-22T21:31:00Z")));
  }

  @Test
  void producesAFileNameThatIsSafeOnWindows() {
    final Path backup =
        ConfigPaths.brokenBackupPath(
            Path.of("server-config.json"), Instant.parse("2026-08-22T21:30:00Z"));

    assertTrue(backup.getFileName().toString().indexOf(':') < 0);
  }
}
