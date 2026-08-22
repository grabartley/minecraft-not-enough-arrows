package com.grahambartley.morearrows;

import com.grahambartley.morearrows.render.ArrowRendererRegistrar;
import net.fabricmc.api.ClientModInitializer;

public class MoreArrowsClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    ArrowRendererRegistrar.registerAll();
  }
}
