package com.grahambartley.volley;

import com.grahambartley.volley.screen.VolleyConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public final class VolleyModMenu implements ModMenuApi {

  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return VolleyConfigScreen::create;
  }
}
