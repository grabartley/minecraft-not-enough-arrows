package com.grahambartley.morearrows.screen;

import com.grahambartley.morearrows.MoreArrows;
import net.minecraft.util.Identifier;

public final class FletchingStationTextures {
  public static final Identifier SHEET =
      Identifier.of(MoreArrows.MOD_ID, "textures/gui/container/fletching_station.png");

  public static final int PANEL_U = 0;
  public static final int PANEL_V = 0;
  public static final int PANEL_WIDTH = 176;
  public static final int PANEL_HEIGHT = 166;

  public static final int ROW_V = 166;
  public static final int ROW_IDLE_U = 0;
  public static final int ROW_HOVERED_U = 16;
  public static final int ROW_SELECTED_U = 32;

  public static final int SCROLLER_V = 166;
  public static final int SCROLLER_U = 48;
  public static final int SCROLLER_DISABLED_U = 60;

  private FletchingStationTextures() {}

  public static int rowU(final int index, final int selected, final int hovered) {
    if (index == selected) {
      return ROW_SELECTED_U;
    }
    return index == hovered ? ROW_HOVERED_U : ROW_IDLE_U;
  }

  public static int scrollerU(final boolean scrollable) {
    return scrollable ? SCROLLER_U : SCROLLER_DISABLED_U;
  }
}
