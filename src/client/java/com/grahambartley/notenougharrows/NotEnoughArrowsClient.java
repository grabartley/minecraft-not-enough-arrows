package com.grahambartley.notenougharrows;

import com.grahambartley.notenougharrows.client.state.ClientStateService;
import com.grahambartley.notenougharrows.fletching.FletchingStationClientInteraction;
import com.grahambartley.notenougharrows.hud.CountdownRingRenderer;
import com.grahambartley.notenougharrows.hud.CountdownSync;
import com.grahambartley.notenougharrows.network.ModNetworkingClient;
import com.grahambartley.notenougharrows.render.ArrowRendererRegistrar;
import com.grahambartley.notenougharrows.render.BlockRenderLayerRegistrar;
import com.grahambartley.notenougharrows.render.TintedArrowColors;
import com.grahambartley.notenougharrows.reveal.BlockOutlineRenderer;
import com.grahambartley.notenougharrows.reveal.RevealSync;
import com.grahambartley.notenougharrows.reveal.TracerPathRenderer;
import com.grahambartley.notenougharrows.screen.FletchingStationScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.LightningEntityRenderer;

public class NotEnoughArrowsClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    ClientStateService.load();
    ArrowRendererRegistrar.registerAll();
    TintedArrowColors.registerAll();
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
