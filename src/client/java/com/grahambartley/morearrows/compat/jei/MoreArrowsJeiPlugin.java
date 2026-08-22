package com.grahambartley.morearrows.compat.jei;

import com.grahambartley.morearrows.MoreArrows;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IModInfoRegistration;
import net.minecraft.util.Identifier;

@JeiPlugin
public final class MoreArrowsJeiPlugin implements IModPlugin {

  private static final Identifier PLUGIN_ID = Identifier.of(MoreArrows.MOD_ID, "jei_plugin");

  private static final String[] MOD_ALIASES = {"arrows", "morearrows", "ma"};

  @Override
  public Identifier getPluginUid() {
    return PLUGIN_ID;
  }

  @Override
  public void registerModInfo(final IModInfoRegistration registration) {
    registration.addModAliases(MoreArrows.MOD_ID, MOD_ALIASES);
  }
}
