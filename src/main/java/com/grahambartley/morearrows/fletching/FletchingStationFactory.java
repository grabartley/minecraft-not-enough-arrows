package com.grahambartley.morearrows.fletching;

import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class FletchingStationFactory {
  public static final String TITLE_KEY = "container.more-arrows.fletching_station";

  private FletchingStationFactory() {}

  public static NamedScreenHandlerFactory of(final World world, final BlockPos pos) {
    return new SimpleNamedScreenHandlerFactory(
        (syncId, playerInventory, player) ->
            new FletchingStationScreenHandler(
                syncId, playerInventory, ScreenHandlerContext.create(world, pos)),
        Text.translatable(TITLE_KEY));
  }
}
