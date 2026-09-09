package com.grahambartley.morearrows;

import com.grahambartley.morearrows.client.state.ClientStateService;
import com.grahambartley.morearrows.hud.CountdownHudRenderer;
import com.grahambartley.morearrows.network.ModNetworkingClient;
import com.grahambartley.morearrows.render.ArrowRendererRegistrar;
import com.grahambartley.morearrows.render.BlockRenderLayerRegistrar;
import net.fabricmc.api.ClientModInitializer;

public class MoreArrowsClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    ClientStateService.load();
    ArrowRendererRegistrar.registerAll();
    BlockRenderLayerRegistrar.registerAll();
    CountdownHudRenderer.register();
    ModNetworkingClient.registerReceivers();
  }
}
