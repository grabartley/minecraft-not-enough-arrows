package com.grahambartley.morearrows.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.morearrows.config.ConfigFile;
import com.grahambartley.morearrows.config.ConfigPaths;
import com.grahambartley.morearrows.config.GrappleArrowConfig;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.config.ServerConfigHolder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ServerConfigServiceTest {

  @TempDir Path worldRoot;

  @AfterEach
  void tearDown() {
    ServerConfigHolder.reset();
  }

  @Test
  void gatesMutationBehindOperatorPermissionLevelTwo() {
    assertEquals(2, ServerConfigService.OP_PERMISSION_LEVEL);
  }

  @Test
  void writesDefaultsOutWhenTheWorldHasNoConfigYet() {
    ServerConfigService.loadFromWorldRoot(worldRoot);

    assertTrue(Files.exists(new ConfigPaths(worldRoot).getServerConfigPath()));
  }

  @Test
  void publishesTheLoadedConfigSoTheServerCanReadIt() throws IOException {
    writeConfig("{\"grapple\":{\"maxRangeBlocks\":64}}");

    ServerConfigService.loadFromWorldRoot(worldRoot);

    assertEquals(64, ServerConfigService.get().grapple().maxRangeBlocks());
  }

  @Test
  void leavesAnExistingConfigUntouched() throws IOException {
    final String contents = "{\"grapple\":{\"maxRangeBlocks\":64}}";
    final Path path = writeConfig(contents);

    ServerConfigService.loadFromWorldRoot(worldRoot);

    assertEquals(contents, Files.readString(path, StandardCharsets.UTF_8));
  }

  @Test
  void replacesAMalformedConfigWithDefaultsOnDisk() throws IOException {
    writeConfig("{ not json ");

    ServerConfigService.loadFromWorldRoot(worldRoot);

    final Path path = new ConfigPaths(worldRoot).getServerConfigPath();
    assertTrue(Files.exists(path));
    assertEquals(MoreArrowsConfig.defaults(), ConfigFile.load(path));
  }

  @Test
  void keepsTheMalformedConfigBesideTheReplacement() throws IOException {
    writeConfig("{ not json ");

    ServerConfigService.loadFromWorldRoot(worldRoot);

    try (var entries = Files.list(new ConfigPaths(worldRoot).getModDir())) {
      assertTrue(entries.anyMatch(p -> p.getFileName().toString().contains(".broken.")));
    }
  }

  @Test
  void resetsToDefaultsWhenThereIsNoSaveSession() {
    ServerConfigHolder.set(
        MoreArrowsConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 1.0f, 0.2f, true, true, 8, false)));

    ServerConfigService.loadFromSession(null);

    assertEquals(MoreArrowsConfig.defaults(), ServerConfigService.get());
  }

  @Test
  void refusesToUpdateWithoutAServerOrConfig() {
    assertEquals(false, ServerConfigService.update(null, MoreArrowsConfig.defaults()));
  }

  @Test
  void broadcastingWithoutAServerIsANoOp() {
    ServerConfigService.broadcast(null);

    assertEquals(MoreArrowsConfig.defaults(), ServerConfigService.get());
  }

  @Test
  void syncingToNoPlayerIsANoOp() {
    ServerConfigService.syncTo(null);

    assertEquals(MoreArrowsConfig.defaults(), ServerConfigService.get());
  }

  private Path writeConfig(final String contents) throws IOException {
    final ConfigPaths paths = new ConfigPaths(worldRoot);
    Files.createDirectories(paths.getModDir());
    final Path path = paths.getServerConfigPath();
    Files.writeString(path, contents, StandardCharsets.UTF_8);
    return path;
  }
}
