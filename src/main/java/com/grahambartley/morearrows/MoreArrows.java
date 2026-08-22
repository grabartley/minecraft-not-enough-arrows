package com.grahambartley.morearrows;

import com.grahambartley.morearrows.network.ModNetworking;
import com.grahambartley.morearrows.server.ServerConfigSyncListener;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreArrows implements ModInitializer {
  public static final String MOD_ID = "more-arrows";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  @Override
  public void onInitialize() {
    ModArrows.register();
    ModItemGroups.register();
    ModNetworking.registerPayloads();
    ServerConfigSyncListener.register();

    LOGGER.info("More Arrows initialized");
  }
}
