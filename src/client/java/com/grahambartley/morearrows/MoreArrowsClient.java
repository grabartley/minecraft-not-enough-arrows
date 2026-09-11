package com.grahambartley.morearrows;

import com.grahambartley.morearrows.client.state.ClientStateService;
import com.grahambartley.morearrows.fletching.FletchingStationClientInteraction;
import com.grahambartley.morearrows.hud.CountdownRingRenderer;
import com.grahambartley.morearrows.hud.CountdownSync;
import com.grahambartley.morearrows.network.ModNetworkingClient;
import com.grahambartley.morearrows.render.ArrowRendererRegistrar;
import com.grahambartley.morearrows.render.BlockRenderLayerRegistrar;
import com.grahambartley.morearrows.screen.FletchingStationScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class MoreArrowsClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    ClientStateService.load();
    ArrowRendererRegistrar.registerAll();
    BlockRenderLayerRegistrar.registerAll();
    CountdownSync.register();
    CountdownRingRenderer.register();
    ModNetworkingClient.registerReceivers();
    FletchingStationClientInteraction.register();
    HandledScreens.register(ModScreenHandlers.FLETCHING_STATION, FletchingStationScreen::new);
  }
}
