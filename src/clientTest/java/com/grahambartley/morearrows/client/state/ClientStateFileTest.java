package com.grahambartley.morearrows.client.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

class ClientStateFileTest {

  @TempDir Path tempDir;

  @Test
  void yieldsDefaultsWhenThePathIsNull() {
    assertEquals(ClientState.defaults(), ClientStateFile.load(null));
  }

  @Test
  void yieldsDefaultsWhenTheFileIsAbsent() {
    assertEquals(
        ClientState.defaults(), ClientStateFile.load(tempDir.resolve("client-state.json")));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "\n\n"})
  void yieldsDefaultsWhenTheFileIsEmpty(final String contents) throws IOException {
    assertEquals(ClientState.defaults(), ClientStateFile.load(write(contents)));
  }

  @ParameterizedTest
  @ValueSource(strings = {"{ this is not json ", "[1,2,3]", "\"a string\"", "null"})
  void survivesACorruptedFileByFallingBackToDefaults(final String contents) throws IOException {
    assertEquals(ClientState.defaults(), ClientStateFile.load(write(contents)));
  }

  @Test
  void preservesACorruptedFileRatherThanOverwritingIt() throws IOException {
    final String contents = "{ this is not json ";
    final Path path = write(contents);

    ClientStateFile.load(path);

    assertFalse(Files.exists(path));
    final List<Path> backups = brokenBackups();
    assertEquals(1, backups.size());
    assertEquals(contents, Files.readString(backups.get(0), StandardCharsets.UTF_8));
  }

  @Test
  void leavesAValidFileInPlace() throws IOException {
    final Path path = write("{\"countdownRingScale\":1.5}");

    ClientStateFile.load(path);

    assertTrue(Files.exists(path));
    assertEquals(List.of(), brokenBackups());
  }

  @Test
  void readsValuesFromAValidFile() throws IOException {
    final Path path = write("{\"showCountdownRing\":false,\"countdownRingScale\":1.5}");

    final ClientState loaded = ClientStateFile.load(path);

    assertFalse(loaded.showCountdownRing());
    assertEquals(1.5f, loaded.countdownRingScale());
  }

  @Test
  void clampsAnOutOfRangeValueRatherThanRefusingToStart() throws IOException {
    final Path path = write("{\"countdownRingScale\":99999}");

    assertEquals(
        ClientState.COUNTDOWN_RING_SCALE_MAX, ClientStateFile.load(path).countdownRingScale());
  }

  @Test
  void survivesARestartByRoundTrippingThroughTheFile() {
    final Path path = tempDir.resolve("more-arrows").resolve("client-state.json");
    final ClientState saved = new ClientState(false, false, 1.75f);

    assertTrue(ClientStateFile.save(path, saved));

    assertEquals(saved, ClientStateFile.load(path));
  }

  @Test
  void createsTheModDirectoryWhenSaving() {
    final Path path = tempDir.resolve("more-arrows").resolve("client-state.json");

    assertTrue(ClientStateFile.save(path, ClientState.defaults()));
    assertTrue(Files.exists(path));
  }

  @Test
  void writesPrettyPrintedJsonSoPlayersCanEditItByHand() throws IOException {
    final Path path = tempDir.resolve("client-state.json");

    ClientStateFile.save(path, ClientState.defaults());

    assertTrue(Files.readString(path, StandardCharsets.UTF_8).contains("\n"));
  }

  @Test
  void leavesNoTemporaryFileBehindAfterSaving() throws IOException {
    final Path path = tempDir.resolve("client-state.json");

    ClientStateFile.save(path, ClientState.defaults());

    try (Stream<Path> entries = Files.list(tempDir)) {
      assertEquals(List.of(path), entries.toList());
    }
  }

  @Test
  void overwritesAnExistingFileWhenSaving() {
    final Path path = tempDir.resolve("client-state.json");
    ClientStateFile.save(path, ClientState.defaults());
    final ClientState updated = new ClientState(false, true, 0.5f);

    assertTrue(ClientStateFile.save(path, updated));
    assertEquals(updated, ClientStateFile.load(path));
  }

  @Test
  void refusesToSaveWithoutAPathOrState() {
    assertFalse(ClientStateFile.save(null, ClientState.defaults()));
    assertFalse(ClientStateFile.save(tempDir.resolve("client-state.json"), null));
  }

  private Path write(final String contents) throws IOException {
    final Path path = tempDir.resolve("client-state.json");
    Files.writeString(path, contents, StandardCharsets.UTF_8);
    return path;
  }

  private List<Path> brokenBackups() throws IOException {
    try (Stream<Path> entries = Files.list(tempDir)) {
      return entries.filter(p -> p.getFileName().toString().contains(".broken.")).toList();
    }
  }
}
