package com.grahambartley.notenougharrows.render;

import com.grahambartley.notenougharrows.ModBlocks;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public final class BlockRenderLayerRegistrar {

  private BlockRenderLayerRegistrar() {}

  public static void registerAll() {
    BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ROPE, RenderLayer.getCutout());
  }
}
