package com.grahambartley.morearrows.config;

import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class ConfigPaths {
  static final String MOD_DIR = "more-arrows";
  static final String SERVER_CONFIG_FILE = "server-config.json";
  static final String JSON_EXTENSION = ".json";
  static final String BROKEN_MARKER = ".broken.";

  private final Path modDir;

  public ConfigPaths(final Path worldDir) {
    this.modDir = Objects.requireNonNull(worldDir, "worldDir").resolve(MOD_DIR);
  }

  public Path getModDir() {
    return modDir;
  }

  public Path getServerConfigPath() {
    return modDir.resolve(SERVER_CONFIG_FILE);
  }

  public static Path brokenBackupPath(final Path original, final Instant when) {
    final String fileName = original.getFileName().toString();
    final String baseName =
        fileName.endsWith(JSON_EXTENSION)
            ? fileName.substring(0, fileName.length() - JSON_EXTENSION.length())
            : fileName;
    final String timestamp = DateTimeFormatter.ISO_INSTANT.format(when).replace(":", "-");
    return original.resolveSibling(baseName + BROKEN_MARKER + timestamp + JSON_EXTENSION);
  }
}
