package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.ConfigFile;
import com.grahambartley.notenougharrows.config.ConfigPaths;
import com.grahambartley.notenougharrows.config.GrappleArrowConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import java.nio.file.Path;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.WorldSavePath;

public final class ServerConfigServiceGameTest implements FabricGameTest {
  private static final String BATCH = "server-config";

  @BeforeBatch(batchId = BATCH)
  public void resetConfigBeforeBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @AfterBatch(batchId = BATCH)
  public void restoreConfigAfterBatch(ServerWorld world) {
    ServerConfigService.update(world.getServer(), NotEnoughArrowsConfig.defaults());
    ServerConfigHolder.reset();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void updatingConfigPublishesItToTheServer(TestContext context) {
    final NotEnoughArrowsConfig updated = configWithRange(64);

    context.assertTrue(
        ServerConfigService.update(context.getWorld().getServer(), updated),
        "Updating the config should succeed on a running server");
    context.assertEquals(
        64, ServerConfigService.get().grapple().maxRangeBlocks(), "Live config after update");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void updatingConfigPersistsItIntoTheWorldSave(TestContext context) {
    final MinecraftServer server = context.getWorld().getServer();
    ServerConfigService.update(server, configWithRange(48));

    final Path configPath =
        new ConfigPaths(server.getSavePath(WorldSavePath.ROOT).normalize()).getServerConfigPath();

    context.assertEquals(
        48,
        ConfigFile.load(configPath).grapple().maxRangeBlocks(),
        "Config reloaded from the world save after update");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void broadcastingToEveryPlayerLeavesTheLiveConfigIntact(TestContext context) {
    ServerConfigService.update(context.getWorld().getServer(), configWithRange(96));

    ServerConfigService.broadcast(context.getWorld().getServer());

    context.assertEquals(
        96, ServerConfigService.get().grapple().maxRangeBlocks(), "Live config after broadcast");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void aSyncCarriesTheLiveConfigRatherThanTheOneLoadedAtStartup(TestContext context) {
    ServerConfigService.update(context.getWorld().getServer(), configWithRange(72));

    context.assertEquals(
        72,
        ServerConfigService.currentSyncPayload().config().grapple().maxRangeBlocks(),
        "A sync built after an update should carry the updated value");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void aLaterSyncSupersedesAnEarlierOne(TestContext context) {
    final MinecraftServer server = context.getWorld().getServer();
    ServerConfigService.update(server, configWithRange(24));
    final int firstSync =
        ServerConfigService.currentSyncPayload().config().grapple().maxRangeBlocks();

    ServerConfigService.update(server, configWithRange(88));

    context.assertEquals(
        24, firstSync, "First sync should have carried the value live at the time");
    context.assertEquals(
        88,
        ServerConfigService.currentSyncPayload().config().grapple().maxRangeBlocks(),
        "A rejoining client should be sent current values, not the ones from an earlier sync");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void syncingToAPlayerWithNoModChannelIsSkippedRatherThanFatal(TestContext context) {
    final ServerPlayerEntity player = context.createMockCreativeServerPlayerInWorld();

    ServerConfigService.syncTo(player);

    context.assertTrue(true, "Syncing to a player that cannot receive the payload must not throw");
    context.complete();
  }

  private static NotEnoughArrowsConfig configWithRange(final int maxRangeBlocks) {
    return NotEnoughArrowsConfig.defaults()
        .withGrapple(
            new GrappleArrowConfig(
                maxRangeBlocks,
                GrappleArrowConfig.DEFAULT_PULL_SPEED,
                GrappleArrowConfig.DEFAULT_PULL_ACCELERATION,
                true,
                true,
                GrappleArrowConfig.DEFAULT_ROPE_LENGTH_BLOCKS,
                false));
  }
}
