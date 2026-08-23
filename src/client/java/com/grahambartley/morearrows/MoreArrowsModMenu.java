package com.grahambartley.morearrows;

import com.grahambartley.morearrows.screen.MoreArrowsConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public final class MoreArrowsModMenu implements ModMenuApi {

  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return MoreArrowsConfigScreen::create;
  }
}
