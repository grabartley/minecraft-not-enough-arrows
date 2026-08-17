package com.grahambartley.morearrows;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreArrows implements ModInitializer {
  public static final String MOD_ID = "more-arrows";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  @Override
  public void onInitialize() {
    LOGGER.info("More Arrows initialized");
  }
}
