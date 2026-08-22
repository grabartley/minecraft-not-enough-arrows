package com.grahambartley.morearrows.config;

import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServerConfigLoader {
  private static final Logger LOGGER = LoggerFactory.getLogger(ServerConfigLoader.class);

  private ServerConfigLoader() {}

  public static void loadFromSession(final LevelStorage.Session session) {
    if (session == null) {
      ServerConfigHolder.reset();
      return;
    }
    loadFromWorldRoot(session.getDirectory(WorldSavePath.ROOT).normalize());
  }

  public static MoreArrowsConfig loadFromWorldRoot(final Path worldRoot) {
    final Path configPath = new ConfigPaths(worldRoot).getServerConfigPath();
    final MoreArrowsConfig config = ConfigFile.load(configPath);
    ServerConfigHolder.set(config);

    if (!Files.exists(configPath)) {
      ConfigFile.save(configPath, config);
      LOGGER.info("Wrote default More Arrows server config to {}", configPath);
    }

    LOGGER.info("More Arrows server config loaded for world {}", worldRoot.getFileName());
    return config;
  }
}
