package com.grahambartley.morearrows.render;

import com.grahambartley.morearrows.ModBlocks;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public final class BlockRenderLayerRegistrar {

  private BlockRenderLayerRegistrar() {}

  public static void registerAll() {
    BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ROPE, RenderLayer.getCutout());
  }
}
