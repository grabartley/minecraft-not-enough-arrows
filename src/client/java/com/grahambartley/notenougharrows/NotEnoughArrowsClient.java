package com.grahambartley.notenougharrows;

import com.grahambartley.notenougharrows.client.state.ClientStateService;
import com.grahambartley.notenougharrows.fletching.FletchingStationClientInteraction;
import com.grahambartley.notenougharrows.hud.CountdownRingRenderer;
import com.grahambartley.notenougharrows.hud.CountdownSync;
import com.grahambartley.notenougharrows.network.ModNetworkingClient;
import com.grahambartley.notenougharrows.render.ArrowRendererRegistrar;
import com.grahambartley.notenougharrows.render.BlockRenderLayerRegistrar;
import com.grahambartley.notenougharrows.screen.FletchingStationScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class NotEnoughArrowsClient implements ClientModInitializer {
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
