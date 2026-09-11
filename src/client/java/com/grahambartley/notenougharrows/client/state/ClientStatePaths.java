package com.grahambartley.notenougharrows.client.state;

import com.grahambartley.notenougharrows.config.ConfigPaths;
import java.nio.file.Path;
import java.util.Objects;

public final class ClientStatePaths {
  static final String CLIENT_STATE_FILE = "client-state.json";

  private final Path modDir;

  public ClientStatePaths(final Path configDir) {
    this.modDir = Objects.requireNonNull(configDir, "configDir").resolve(ConfigPaths.MOD_DIR);
  }

  public Path getModDir() {
    return modDir;
  }

  public Path getClientStatePath() {
    return modDir.resolve(CLIENT_STATE_FILE);
  }
}
