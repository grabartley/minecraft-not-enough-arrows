package com.grahambartley.morearrows.fletching;

import com.grahambartley.morearrows.server.ServerConfigService;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class FletchingStationInteraction {

  private FletchingStationInteraction() {}

  public static ActionResult use(final World world, final BlockPos pos, final PlayerEntity player) {
    if (!ServerConfigService.get().fletching().stationEnabled()) {
      return ActionResult.PASS;
    }
    if (world.isClient) {
      return ActionResult.SUCCESS;
    }
    player.openHandledScreen(FletchingStationFactory.of(world, pos));
    return ActionResult.CONSUME;
  }
}
