package com.grahambartley.notenougharrows.fletching;

import com.grahambartley.notenougharrows.config.ClientConfigHolder;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public final class FletchingStationClientInteraction {
  private FletchingStationClientInteraction() {}

  public static void register() {
    UseBlockCallback.EVENT.register(FletchingStationClientInteraction::onBlockUsed);
  }

  private static ActionResult onBlockUsed(
      final PlayerEntity player, final World world, final Hand hand, final BlockHitResult hit) {
    if (!world.isClient) {
      return ActionResult.PASS;
    }

    if (!ClientConfigHolder.isSynced()) {
      return ActionResult.PASS;
    }

    final boolean stationEnabled = ClientConfigHolder.get().fletching().stationEnabled();
    return FletchingStationInteraction.opensStation(
            stationEnabled, player, world, hit.getBlockPos())
        ? ActionResult.SUCCESS
        : ActionResult.PASS;
  }
}
