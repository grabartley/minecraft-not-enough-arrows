package com.grahambartley.morearrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ServerConfigLoaderTest {

  @TempDir Path worldRoot;

  @AfterEach
  void tearDown() {
    ServerConfigHolder.reset();
  }

  @Test
  void writesDefaultsOutWhenTheWorldHasNoConfigYet() {
    ServerConfigLoader.loadFromWorldRoot(worldRoot);

    assertTrue(Files.exists(new ConfigPaths(worldRoot).getServerConfigPath()));
  }

  @Test
  void publishesTheLoadedConfigToTheHolder() throws IOException {
    writeConfig("{\"grapple\":{\"maxRangeBlocks\":64}}");

    ServerConfigLoader.loadFromWorldRoot(worldRoot);

    assertEquals(64, ServerConfigHolder.get().grapple().maxRangeBlocks());
  }

  @Test
  void leavesAnExistingConfigUntouched() throws IOException {
    final String contents = "{\"grapple\":{\"maxRangeBlocks\":64}}";
    final Path path = writeConfig(contents);

    ServerConfigLoader.loadFromWorldRoot(worldRoot);

    assertEquals(contents, Files.readString(path, StandardCharsets.UTF_8));
  }

  @Test
  void replacesAMalformedConfigWithDefaultsOnDisk() throws IOException {
    writeConfig("{ not json ");

    ServerConfigLoader.loadFromWorldRoot(worldRoot);

    final Path path = new ConfigPaths(worldRoot).getServerConfigPath();
    assertTrue(Files.exists(path));
    assertEquals(MoreArrowsConfig.defaults(), ConfigFile.load(path));
  }

  @Test
  void keepsTheMalformedConfigBesideTheReplacement() throws IOException {
    writeConfig("{ not json ");

    ServerConfigLoader.loadFromWorldRoot(worldRoot);

    try (var entries = Files.list(new ConfigPaths(worldRoot).getModDir())) {
      assertTrue(entries.anyMatch(p -> p.getFileName().toString().contains(".broken.")));
    }
  }

  @Test
  void returnsDefaultsForAWorldWithNoConfig() {
    assertEquals(MoreArrowsConfig.defaults(), ServerConfigLoader.loadFromWorldRoot(worldRoot));
  }

  private Path writeConfig(final String contents) throws IOException {
    final ConfigPaths paths = new ConfigPaths(worldRoot);
    Files.createDirectories(paths.getModDir());
    final Path path = paths.getServerConfigPath();
    Files.writeString(path, contents, StandardCharsets.UTF_8);
    return path;
  }
}
