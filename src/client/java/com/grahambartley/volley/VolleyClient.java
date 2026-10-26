package com.grahambartley.volley;

import com.grahambartley.volley.client.state.ClientStateService;
import com.grahambartley.volley.fletching.FletchingStationClientInteraction;
import com.grahambartley.volley.hud.CountdownRingRenderer;
import com.grahambartley.volley.hud.CountdownSync;
import com.grahambartley.volley.network.ModNetworkingClient;
import com.grahambartley.volley.render.ArrowRendererRegistrar;
import com.grahambartley.volley.render.BlockRenderLayerRegistrar;
import com.grahambartley.volley.render.CourierArrowPredicates;
import com.grahambartley.volley.render.TintedArrowColors;
import com.grahambartley.volley.reveal.BlockOutlineRenderer;
import com.grahambartley.volley.reveal.RevealSync;
import com.grahambartley.volley.reveal.TracerPathRenderer;
import com.grahambartley.volley.screen.FletchingStationScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.LightningEntityRenderer;

public class VolleyClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    ClientStateService.load();
    ArrowRendererRegistrar.registerAll();
    TintedArrowColors.registerAll();
    CourierArrowPredicates.register();
    EntityRendererRegistry.register(ModEntities.SHOCK_BOLT, LightningEntityRenderer::new);
    BlockRenderLayerRegistrar.registerAll();
    CountdownSync.register();
    CountdownRingRenderer.register();
    RevealSync.register();
    BlockOutlineRenderer.register();
    TracerPathRenderer.register();
    ModNetworkingClient.registerReceivers();
    FletchingStationClientInteraction.register();
    HandledScreens.register(ModScreenHandlers.FLETCHING_STATION, FletchingStationScreen::new);
  }
}
