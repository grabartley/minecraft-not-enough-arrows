package com.grahambartley.notenougharrows.client.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ClientStateServiceTest {

  @TempDir Path configDir;

  @AfterEach
  void tearDown() {
    ClientStateHolder.reset();
  }

  @Test
  void writesDefaultsOutWhenTheInstallationHasNoStateYet() {
    ClientStateService.loadFromConfigDir(configDir);

    assertTrue(Files.exists(statePath()));
    assertEquals(ClientState.defaults(), ClientStateFile.load(statePath()));
  }

  @Test
  void publishesTheLoadedStateForTheRestOfTheClient() throws IOException {
    write("{\"countdownRingScale\":1.5}");

    ClientStateService.loadFromConfigDir(configDir);

    assertEquals(1.5f, ClientStateService.get().countdownRingScale());
    assertEquals(ClientStateHolder.get(), ClientStateService.get());
  }

  @Test
  void fallsBackToDefaultsWhenTheStoredFileIsCorrupted() throws IOException {
    write("{ this is not json ");

    assertEquals(ClientState.defaults(), ClientStateService.loadFromConfigDir(configDir));
    assertEquals(ClientState.defaults(), ClientStateService.get());
  }

  @Test
  void leavesAnExistingFileAloneOnLoad() throws IOException {
    final String contents = "{\"showCountdownRing\": false}";
    write(contents);

    ClientStateService.loadFromConfigDir(configDir);

    assertEquals(contents, Files.readString(statePath(), StandardCharsets.UTF_8));
  }

  @Test
  void persistsAnUpdateSoItSurvivesARestart() {
    ClientStateService.loadFromConfigDir(configDir);
    final ClientState updated = new ClientState(false, false, 2.0f);

    assertTrue(ClientStateService.updateInConfigDir(configDir, updated));

    ClientStateHolder.reset();
    assertEquals(updated, ClientStateService.loadFromConfigDir(configDir));
  }

  @Test
  void publishesAnUpdateImmediatelyWithoutWaitingForAReload() {
    final ClientState updated = new ClientState(false, false, 2.0f);

    ClientStateService.updateInConfigDir(configDir, updated);

    assertEquals(updated, ClientStateService.get());
  }

  @Test
  void refusesAnUpdateWithoutADirectoryOrState() {
    assertFalse(ClientStateService.updateInConfigDir(null, ClientState.defaults()));
    assertFalse(ClientStateService.updateInConfigDir(configDir, null));
    assertEquals(ClientState.defaults(), ClientStateService.get());
  }

  @Test
  void keepsTwoInstallationsIndependent() {
    final Path first = configDir.resolve("first");
    final Path second = configDir.resolve("second");
    final ClientState firstState = new ClientState(false, false, 0.5f);
    final ClientState secondState = new ClientState(true, false, 2.0f);

    ClientStateService.updateInConfigDir(first, firstState);
    ClientStateService.updateInConfigDir(second, secondState);

    assertEquals(firstState, ClientStateService.loadFromConfigDir(first));
    assertEquals(secondState, ClientStateService.loadFromConfigDir(second));
  }

  private Path statePath() {
    return new ClientStatePaths(configDir).getClientStatePath();
  }

  private void write(final String contents) throws IOException {
    final Path path = statePath();
    Files.createDirectories(path.getParent());
    Files.writeString(path, contents, StandardCharsets.UTF_8);
  }
}
