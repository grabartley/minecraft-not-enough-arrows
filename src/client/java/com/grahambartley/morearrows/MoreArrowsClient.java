package com.grahambartley.morearrows;

import com.grahambartley.morearrows.client.state.ClientStateService;
import com.grahambartley.morearrows.hud.CountdownRingRenderer;
import com.grahambartley.morearrows.hud.CountdownSync;
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
    CountdownSync.register();
    CountdownRingRenderer.register();
    ModNetworkingClient.registerReceivers();
  }
}
