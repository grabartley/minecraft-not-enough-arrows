package com.grahambartley.morearrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ConfigFileTest {

  @TempDir Path tempDir;

  @Test
  void yieldsDefaultsWhenThePathIsNull() {
    assertEquals(MoreArrowsConfig.defaults(), ConfigFile.load(null));
  }

  @Test
  void yieldsDefaultsWhenTheFileIsAbsent() {
    assertEquals(
        MoreArrowsConfig.defaults(), ConfigFile.load(tempDir.resolve("server-config.json")));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "\n\n"})
  void yieldsDefaultsWhenTheFileIsEmpty(final String contents) throws IOException {
    final Path path = write("server-config.json", contents);

    assertEquals(MoreArrowsConfig.defaults(), ConfigFile.load(path));
  }

  @Test
  void yieldsDefaultsWhenTheFileIsNotValidJson() throws IOException {
    final Path path = write("server-config.json", "{ this is not json ");

    assertEquals(MoreArrowsConfig.defaults(), ConfigFile.load(path));
  }

  @Test
  void preservesAMalformedFileRatherThanOverwritingIt() throws IOException {
    final String contents = "{ this is not json ";
    final Path path = write("server-config.json", contents);

    ConfigFile.load(path);

    assertFalse(Files.exists(path));
    final List<Path> backups = brokenBackups();
    assertEquals(1, backups.size());
    assertEquals(contents, Files.readString(backups.get(0), StandardCharsets.UTF_8));
  }

  @Test
  void leavesAValidFileInPlace() throws IOException {
    final Path path = write("server-config.json", "{\"grapple\":{\"maxRangeBlocks\":64}}");

    ConfigFile.load(path);

    assertTrue(Files.exists(path));
    assertEquals(List.of(), brokenBackups());
  }

  @Test
  void readsValuesFromAValidFile() throws IOException {
    final Path path = write("server-config.json", "{\"grapple\":{\"maxRangeBlocks\":64}}");

    assertEquals(64, ConfigFile.load(path).grapple().maxRangeBlocks());
  }

  @Test
  void clampsAnOutOfRangeValueRatherThanRefusingToStart() throws IOException {
    final Path path = write("server-config.json", "{\"grapple\":{\"maxRangeBlocks\":99999}}");

    assertEquals(
        GrappleArrowConfig.MAX_RANGE_BLOCKS_MAX, ConfigFile.load(path).grapple().maxRangeBlocks());
  }

  @Test
  void survivesARestartByRoundTrippingThroughTheFile() {
    final Path path = tempDir.resolve("more-arrows").resolve("server-config.json");
    final MoreArrowsConfig saved =
        MoreArrowsConfig.defaults()
            .withPhysics(new PhysicsArrowConfig(4, List.of("minecraft:bedrock"), 9, false))
            .withGrapple(new GrappleArrowConfig(64, 2.0f, false, true, 8, true));

    assertTrue(ConfigFile.save(path, saved));

    assertEquals(saved, ConfigFile.load(path));
  }

  @Test
  void createsTheModDirectoryWhenSaving() {
    final Path path = tempDir.resolve("more-arrows").resolve("server-config.json");

    assertTrue(ConfigFile.save(path, MoreArrowsConfig.defaults()));
    assertTrue(Files.exists(path));
  }

  @Test
  void writesPrettyPrintedJsonSoOperatorsCanEditItByHand() throws IOException {
    final Path path = tempDir.resolve("server-config.json");
    ConfigFile.save(path, MoreArrowsConfig.defaults());

    assertTrue(Files.readString(path, StandardCharsets.UTF_8).contains("\n"));
  }

  @Test
  void leavesNoTemporaryFileBehindAfterSaving() throws IOException {
    final Path path = tempDir.resolve("server-config.json");
    ConfigFile.save(path, MoreArrowsConfig.defaults());

    try (Stream<Path> entries = Files.list(tempDir)) {
      assertEquals(List.of(path), entries.toList());
    }
  }

  @Test
  void overwritesAnExistingFileWhenSaving() {
    final Path path = tempDir.resolve("server-config.json");
    ConfigFile.save(path, MoreArrowsConfig.defaults());
    final MoreArrowsConfig updated =
        MoreArrowsConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 2.0f, false, true, 8, true));

    assertTrue(ConfigFile.save(path, updated));
    assertEquals(updated, ConfigFile.load(path));
  }

  @Test
  void refusesToSaveWithoutAPathOrConfig() {
    assertFalse(ConfigFile.save(null, MoreArrowsConfig.defaults()));
    assertFalse(ConfigFile.save(tempDir.resolve("server-config.json"), null));
  }

  @Test
  void keepsTwoWorldsIndependent() {
    final Path worldA = tempDir.resolve("World A");
    final Path worldB = tempDir.resolve("World B");
    final MoreArrowsConfig configA =
        MoreArrowsConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 2.0f, true, true, 8, false));
    final MoreArrowsConfig configB =
        MoreArrowsConfig.defaults()
            .withGrapple(new GrappleArrowConfig(16, 0.5f, false, false, 4, true));

    ConfigFile.save(new ConfigPaths(worldA).getServerConfigPath(), configA);
    ConfigFile.save(new ConfigPaths(worldB).getServerConfigPath(), configB);

    final MoreArrowsConfig loadedA = ConfigFile.load(new ConfigPaths(worldA).getServerConfigPath());
    final MoreArrowsConfig loadedB = ConfigFile.load(new ConfigPaths(worldB).getServerConfigPath());

    assertEquals(configA, loadedA);
    assertEquals(configB, loadedB);
    assertNotEquals(loadedA, loadedB);
  }

  private Path write(final String fileName, final String contents) throws IOException {
    final Path path = tempDir.resolve(fileName);
    Files.writeString(path, contents, StandardCharsets.UTF_8);
    return path;
  }

  private List<Path> brokenBackups() throws IOException {
    try (Stream<Path> entries = Files.list(tempDir)) {
      return entries.filter(p -> p.getFileName().toString().contains(".broken.")).toList();
    }
  }
}
