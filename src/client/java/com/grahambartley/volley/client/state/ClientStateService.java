package com.grahambartley.volley.client.state;

import com.grahambartley.volley.config.ConfigPaths;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClientStateService {
  static final String CLIENT_STATE_FILE = "client-state.json";

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientStateService.class);
  private static volatile ClientState current = ClientState.defaults();

  private ClientStateService() {}

  public static ClientState get() {
    return current;
  }

  public static void load() {
    loadFromConfigDir(FabricLoader.getInstance().getConfigDir());
  }

  public static ClientState loadFromConfigDir(final Path configDir) {
    final Path statePath = statePath(configDir);
    final ClientState state = ClientStateFile.load(statePath);
    current = state;

    if (!Files.exists(statePath)) {
      ClientStateFile.save(statePath, state);
      LOGGER.info("Wrote default Volley client state to {}", statePath);
    }

    LOGGER.info("Volley client state loaded from {}", statePath);
    return state;
  }

  public static boolean update(final ClientState updated) {
    return updateInConfigDir(FabricLoader.getInstance().getConfigDir(), updated);
  }

  public static boolean updateInConfigDir(final Path configDir, final ClientState updated) {
    if (configDir == null || updated == null) {
      return false;
    }
    if (!ClientStateFile.save(statePath(configDir), updated)) {
      return false;
    }
    current = updated;
    return true;
  }

  static Path statePath(final Path configDir) {
    return configDir.resolve(ConfigPaths.MOD_DIR).resolve(CLIENT_STATE_FILE);
  }

  static void reset() {
    current = ClientState.defaults();
  }
}
