package com.grahambartley.notenougharrows;

import com.grahambartley.notenougharrows.screen.NotEnoughArrowsConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public final class NotEnoughArrowsModMenu implements ModMenuApi {

  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return NotEnoughArrowsConfigScreen::create;
  }
}
