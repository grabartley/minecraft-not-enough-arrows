package com.grahambartley.morearrows.server;

import com.grahambartley.morearrows.config.ConfigFile;
import com.grahambartley.morearrows.config.ConfigPaths;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.config.ServerConfigHolder;
import com.grahambartley.morearrows.network.ServerConfigPayloads.SyncServerConfigS2CPayload;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServerConfigService {
  public static final int OP_PERMISSION_LEVEL = 2;

  private static final Logger LOGGER = LoggerFactory.getLogger(ServerConfigService.class);

  private ServerConfigService() {}

  public static MoreArrowsConfig get() {
    return ServerConfigHolder.get();
  }

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

  public static boolean update(final MinecraftServer server, final MoreArrowsConfig updated) {
    if (server == null || updated == null) {
      return false;
    }
    if (!ConfigFile.save(configPathFor(server), updated)) {
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
    if (player == null || !ServerPlayNetworking.canSend(player, SyncServerConfigS2CPayload.ID)) {
      return;
    }
    ServerPlayNetworking.send(player, currentSyncPayload());
  }

  public static void broadcast(final MinecraftServer server) {
    if (server == null) {
      return;
    }
    server.getPlayerManager().getPlayerList().forEach(ServerConfigService::syncTo);
  }

  private static Path configPathFor(final MinecraftServer server) {
    return new ConfigPaths(server.getSavePath(WorldSavePath.ROOT).normalize())
        .getServerConfigPath();
  }
}
