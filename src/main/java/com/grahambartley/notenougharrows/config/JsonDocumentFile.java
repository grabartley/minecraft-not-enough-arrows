package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JsonDocumentFile {
  private static final Logger LOGGER = LoggerFactory.getLogger(JsonDocumentFile.class);
  private static final String TEMP_SUFFIX = ".tmp";

  private JsonDocumentFile() {}

  public static <T> T load(
      final Path path,
      final String label,
      final Function<String, T> decoder,
      final Supplier<T> defaults) {
    if (path == null || !Files.exists(path)) {
      return defaults.get();
    }

    final String json;
    try {
      json = Files.readString(path, StandardCharsets.UTF_8);
    } catch (final IOException ex) {
      LOGGER.warn(
          "Could not read Not Enough Arrows {} at {}, using defaults: {}",
          label,
          path,
          ex.getMessage());
      return defaults.get();
    }

    if (json.isBlank()) {
      LOGGER.warn("Not Enough Arrows {} at {} is empty, using defaults", label, path);
      return defaults.get();
    }

    try {
      return decoder.apply(json);
    } catch (final JsonParseException | IllegalStateException ex) {
      final Path backup = backUpBrokenFile(path);
      LOGGER.warn(
          "Not Enough Arrows {} at {} is malformed, using defaults. Broken file kept at {}: {}",
          label,
          path,
          backup == null ? "<backup failed>" : backup,
          ex.getMessage());
      return defaults.get();
    }
  }

  public static boolean save(final Path path, final String label, final String json) {
    if (path == null || json == null) {
      return false;
    }

    try {
      final Path parent = path.getParent();
      if (parent != null) {
        Files.createDirectories(parent);
      }
      final Path temp = path.resolveSibling(path.getFileName() + TEMP_SUFFIX);
      Files.writeString(temp, json, StandardCharsets.UTF_8);
      Files.move(temp, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
      return true;
    } catch (final IOException ex) {
      LOGGER.error("Could not save Not Enough Arrows {} at {}: {}", label, path, ex.getMessage());
      return false;
    }
  }

  static Path backUpBrokenFile(final Path path) {
    try {
      final Path backup = ConfigPaths.brokenBackupPath(path, Instant.now());
      Files.move(path, backup, StandardCopyOption.REPLACE_EXISTING);
      return backup;
    } catch (final IOException ex) {
      LOGGER.warn("Could not preserve malformed Not Enough Arrows file: {}", ex.getMessage());
      return null;
    }
  }
}
