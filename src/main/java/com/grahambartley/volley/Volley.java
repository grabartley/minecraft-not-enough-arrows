package com.grahambartley.volley;

import com.grahambartley.volley.agriculture.BeeSwarmWarden;
import com.grahambartley.volley.anchor.AnchorService;
import com.grahambartley.volley.blast.BlastService;
import com.grahambartley.volley.chaos.PufferService;
import com.grahambartley.volley.chaos.StinkCloudService;
import com.grahambartley.volley.command.VolleyCommand;
import com.grahambartley.volley.control.ControlHoldService;
import com.grahambartley.volley.control.ControlPersistence;
import com.grahambartley.volley.control.DisarmFetchService;
import com.grahambartley.volley.control.FrostGripService;
import com.grahambartley.volley.control.SmokeCloudService;
import com.grahambartley.volley.countdown.CountdownBroadcaster;
import com.grahambartley.volley.disguise.DisguiseService;
import com.grahambartley.volley.fletching.FletchingStationInteraction;
import com.grahambartley.volley.fuse.FuseService;
import com.grahambartley.volley.grapple.GrappleFallGuard;
import com.grahambartley.volley.grapple.GrappleService;
import com.grahambartley.volley.network.ModNetworking;
import com.grahambartley.volley.nock.NockedArrowBroadcaster;
import com.grahambartley.volley.redstone.RedstoneChargeService;
import com.grahambartley.volley.reveal.WatcherService;
import com.grahambartley.volley.server.ServerConfigSyncListener;
import com.grahambartley.volley.server.ServerConfigUpdateReceiver;
import com.grahambartley.volley.social.MagnetPull;
import com.grahambartley.volley.social.SnowGolemMelt;
import com.grahambartley.volley.structure.TimedStructureService;
import com.grahambartley.volley.tow.TowService;
import com.grahambartley.volley.updraft.UpdraftService;
import com.grahambartley.volley.zipline.PendingAnchorService;
import com.grahambartley.volley.zipline.RideBroadcaster;
import com.grahambartley.volley.zipline.RideService;
import com.grahambartley.volley.zipline.SpanService;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Volley implements ModInitializer {
  public static final String MOD_ID = "volley";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  @Override
  public void onInitialize() {
    ModDataComponents.register();
    ModBlocks.register();
    ModArrows.register();
    ModEntities.register();
    ModItemGroups.register();
    ModRecipes.register();
    ModScreenHandlers.register();
    ModSounds.register();
    ModNetworking.registerPayloads();
    TimedStructureService.register();
    FuseService.register();
    CountdownBroadcaster.register();
    BlastService.register();
    AnchorService.register();
    GrappleService.register();
    GrappleFallGuard.register();
    PendingAnchorService.register();
    SpanService.register();
    RideService.register();
    RideBroadcaster.register();
    TowService.register();
    UpdraftService.register();
    RedstoneChargeService.register();
    ControlPersistence.register();
    ControlHoldService.register();
    FrostGripService.register();
    DisarmFetchService.register();
    SmokeCloudService.register();
    BeeSwarmWarden.register();
    StinkCloudService.register();
    PufferService.register();
    DisguiseService.register();
    SnowGolemMelt.register();
    MagnetPull.register();
    WatcherService.register();
    NockedArrowBroadcaster.register();
    FletchingStationInteraction.register();
    ServerConfigSyncListener.register();
    ServerConfigUpdateReceiver.register();
    CommandRegistrationCallback.EVENT.register(
        (dispatcher, registryAccess, environment) -> VolleyCommand.register(dispatcher));

    LOGGER.info("Volley initialized");
  }
}
