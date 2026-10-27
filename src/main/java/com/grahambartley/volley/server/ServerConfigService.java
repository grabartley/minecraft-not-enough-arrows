package com.grahambartley.volley.server;

import com.grahambartley.volley.config.ConfigFile;
import com.grahambartley.volley.config.ConfigPaths;
import com.grahambartley.volley.config.ServerConfigHolder;
import com.grahambartley.volley.config.VolleyConfig;
import com.grahambartley.volley.network.ServerConfigPayloads.SyncServerConfigS2CPayload;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServerConfigService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ServerConfigService.class);

  private ServerConfigService() {}

  public static VolleyConfig get() {
    return ServerConfigHolder.get();
  }

  public static void loadFromSession(final LevelStorage.Session session) {
    if (session == null) {
      ServerConfigHolder.reset();
      return;
    }
    loadFromWorldRoot(session.getDirectory(WorldSavePath.ROOT).normalize());
  }

  public static VolleyConfig loadFromWorldRoot(final Path worldRoot) {
    final Path configPath = configPathIn(worldRoot);
    final VolleyConfig config = ConfigFile.load(configPath);
    ServerConfigHolder.set(config);

    if (!Files.exists(configPath)) {
      ConfigFile.save(configPath, config);
      LOGGER.info("Wrote default Volley server config to {}", configPath);
    }

    LOGGER.info("Volley server config loaded for world {}", worldRoot.getFileName());
    return config;
  }

  public static boolean update(final MinecraftServer server, final VolleyConfig updated) {
    if (server == null || updated == null) {
      return false;
    }
    if (!ConfigFile.save(configPathIn(server.getSavePath(WorldSavePath.ROOT)), updated)) {
      return false;
    }
    ServerConfigHolder.set(updated);
    broadcast(server);
    return true;
  }

  public static SyncServerConfigS2CPayload currentSyncPayload() {
    return new SyncServerConfigS2CPayload(ServerConfigHolder.get());
  }

  public static void syncTo(final ServerPlayerEntity player) {
    if (player != null) {
      ModdedClients.send(player, currentSyncPayload());
    }
  }

  public static void broadcast(final MinecraftServer server) {
    if (server == null) {
      return;
    }
    server.getPlayerManager().getPlayerList().forEach(ServerConfigService::syncTo);
  }

  private static Path configPathIn(final Path worldRoot) {
    return new ConfigPaths(worldRoot.normalize()).getServerConfigPath();
  }
}
