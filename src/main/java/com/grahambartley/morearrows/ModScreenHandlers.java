package com.grahambartley.morearrows;

import com.grahambartley.morearrows.fletching.FletchingStationScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public final class ModScreenHandlers {
  public static final Identifier FLETCHING_STATION_ID =
      Identifier.of(MoreArrows.MOD_ID, "fletching_station");

  public static final ScreenHandlerType<FletchingStationScreenHandler> FLETCHING_STATION =
      new ScreenHandlerType<>(FletchingStationScreenHandler::new, FeatureFlags.VANILLA_FEATURES);

  private ModScreenHandlers() {}

  public static void register() {
    Registry.register(Registries.SCREEN_HANDLER, FLETCHING_STATION_ID, FLETCHING_STATION);
    MoreArrows.LOGGER.info("Registered screen handler {}", FLETCHING_STATION_ID);
  }
}
