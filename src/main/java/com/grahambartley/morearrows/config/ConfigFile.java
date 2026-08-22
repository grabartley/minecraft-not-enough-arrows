package com.grahambartley.morearrows.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigFile {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigFile.class);
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final String TEMP_SUFFIX = ".tmp";

  private ConfigFile() {}

  public static MoreArrowsConfig load(final Path path) {
    if (path == null || !Files.exists(path)) {
      return MoreArrowsConfig.defaults();
    }

    final String json;
    try {
      json = Files.readString(path, StandardCharsets.UTF_8);
    } catch (final IOException ex) {
      LOGGER.warn(
          "Could not read More Arrows config at {}, using defaults: {}", path, ex.getMessage());
      return MoreArrowsConfig.defaults();
    }

    if (json.isBlank()) {
      LOGGER.warn("More Arrows config at {} is empty, using defaults", path);
      return MoreArrowsConfig.defaults();
    }

    try {
      final JsonObject root = GSON.fromJson(json, JsonObject.class);
      if (root == null) {
        throw new JsonParseException("Config root is not a JSON object");
      }
      return MoreArrowsConfig.fromJson(root);
    } catch (final JsonParseException | IllegalStateException ex) {
      final Path backup = backUpBrokenFile(path);
      LOGGER.warn(
          "More Arrows config at {} is malformed, using defaults. Broken file kept at {}: {}",
          path,
          backup == null ? "<backup failed>" : backup,
          ex.getMessage());
      return MoreArrowsConfig.defaults();
    }
  }

  public static boolean save(final Path path, final MoreArrowsConfig config) {
    if (path == null || config == null) {
      return false;
    }

    try {
      final Path parent = path.getParent();
      if (parent != null) {
        Files.createDirectories(parent);
      }
      final Path temp = path.resolveSibling(path.getFileName() + TEMP_SUFFIX);
      Files.writeString(temp, GSON.toJson(config.toJson()), StandardCharsets.UTF_8);
      Files.move(temp, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
      return true;
    } catch (final IOException ex) {
      LOGGER.error("Could not save More Arrows config at {}: {}", path, ex.getMessage());
      return false;
    }
  }

  static Path backUpBrokenFile(final Path path) {
    try {
      final Path backup = ConfigPaths.brokenBackupPath(path, Instant.now());
      Files.move(path, backup, StandardCopyOption.REPLACE_EXISTING);
      return backup;
    } catch (final IOException ex) {
      LOGGER.warn("Could not preserve malformed More Arrows config: {}", ex.getMessage());
      return null;
    }
  }
}
