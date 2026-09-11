package com.grahambartley.notenougharrows.client.state;

import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClientStateService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClientStateService.class);

  private ClientStateService() {}

  public static ClientState get() {
    return ClientStateHolder.get();
  }

  public static void load() {
    loadFromConfigDir(FabricLoader.getInstance().getConfigDir());
  }

  public static ClientState loadFromConfigDir(final Path configDir) {
    final Path statePath = new ClientStatePaths(configDir).getClientStatePath();
    final ClientState state = ClientStateFile.load(statePath);
    ClientStateHolder.set(state);

    if (!Files.exists(statePath)) {
      ClientStateFile.save(statePath, state);
      LOGGER.info("Wrote default Not Enough Arrows client state to {}", statePath);
    }

    LOGGER.info("Not Enough Arrows client state loaded from {}", statePath);
    return state;
  }

  public static boolean update(final ClientState updated) {
    return updateInConfigDir(FabricLoader.getInstance().getConfigDir(), updated);
  }

  public static boolean updateInConfigDir(final Path configDir, final ClientState updated) {
    if (configDir == null || updated == null) {
      return false;
    }
    if (!ClientStateFile.save(new ClientStatePaths(configDir).getClientStatePath(), updated)) {
      return false;
    }
    ClientStateHolder.set(updated);
    return true;
  }
}
