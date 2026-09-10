package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.config.ServerConfigHolder;
import com.grahambartley.morearrows.fletching.FletchingStationScreenHandler;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

public final class FletchingStationInteractionGameTest implements FabricGameTest {
  private static final String BATCH = "fletching-station-interaction";
  private static final BlockPos TABLE = FletchingStationSupport.TABLE;
  private static final BlockPos AGAINST_THE_TABLE = TABLE.up();

  @BeforeBatch(batchId = BATCH)
  public void enableTheStationBeforeBatch(ServerWorld world) {
    ServerConfigHolder.set(configuredStationEnabled(true));
  }

  @AfterBatch(batchId = BATCH)
  public void restoreDefaultConfigAfterBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void rightClickingAFletchingTableOpensTheStation(TestContext context) {
    ServerConfigHolder.set(configuredStationEnabled(true));
    final ServerPlayerEntity player = playerAtTheTable(context);

    context.assertTrue(
        interact(context, player, ItemStack.EMPTY).isAccepted(),
        "Using a fletching table should be accepted rather than passed on");
    context.assertTrue(
        player.currentScreenHandler instanceof FletchingStationScreenHandler,
        "Using a fletching table should leave the player looking at the station");
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void openingTheStationLeavesTheVanillaBlockExactlyAsItWas(TestContext context) {
    ServerConfigHolder.set(configuredStationEnabled(true));
    final ServerPlayerEntity player = playerAtTheTable(context);
    final var before = context.getBlockState(TABLE);

    interact(context, player, ItemStack.EMPTY);

    context.assertTrue(
        context.getBlockState(TABLE) == before,
        "The station must not replace the block or change its state");
    context.assertTrue(
        context.getWorld().getBlockEntity(context.getAbsolutePos(TABLE)) == null,
        "The station must not give the vanilla block a block entity");
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aFletchingTableIsStillTheFletcherJobSite(TestContext context) {
    context.setBlockState(TABLE, Blocks.FLETCHING_TABLE);

    final RegistryEntry<PointOfInterestType> poi =
        PointOfInterestTypes.getTypeForState(context.getBlockState(TABLE)).orElse(null);

    context.assertTrue(poi != null, "A fletching table should still register a point of interest");
    context.assertTrue(
        poi.matchesKey(PointOfInterestTypes.FLETCHER),
        "A fletching table should still be the fletcher's job site");
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void sneakingSuppressesTheStationSoBlocksStillPlaceAgainstTheTable(TestContext context) {
    ServerConfigHolder.set(configuredStationEnabled(true));
    final ServerPlayerEntity player = playerAtTheTable(context);
    player.setSneaking(true);

    interact(context, player, new ItemStack(Items.STONE));

    context.assertFalse(
        player.currentScreenHandler instanceof FletchingStationScreenHandler,
        "Sneaking should suppress the station the way it suppresses every block interaction");
    context.expectBlock(Blocks.STONE, AGAINST_THE_TABLE);
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aDisabledStationLeavesTheBlockBehavingLikeVanilla(TestContext context) {
    ServerConfigHolder.set(configuredStationEnabled(false));
    final ServerPlayerEntity player = playerAtTheTable(context);

    context.assertFalse(
        interact(context, player, ItemStack.EMPTY).isAccepted(),
        "A disabled station should pass the interaction on the way vanilla does");
    context.assertFalse(
        player.currentScreenHandler instanceof FletchingStationScreenHandler,
        "A disabled station should open no screen");
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aDisabledStationLeavesCraftingTableRecipesWorking(TestContext context) {
    ServerConfigHolder.set(configuredStationEnabled(false));

    context.assertFalse(
        ServerConfigHolder.get().fletching().stationEnabled(),
        "This test needs the station disabled to mean anything");
    context.assertTrue(
        ArrowCraftingSupport.ropeArrowStillCrafts(context.getWorld()),
        "Disabling the station must never take away a crafting table recipe");
    context.complete();
  }

  private static MoreArrowsConfig configuredStationEnabled(final boolean enabled) {
    final MoreArrowsConfig defaults = MoreArrowsConfig.defaults();
    return defaults.withFletching(defaults.fletching().withStationEnabled(enabled));
  }

  private static ServerPlayerEntity playerAtTheTable(final TestContext context) {
    context.setBlockState(TABLE, Blocks.FLETCHING_TABLE);
    return MockPlayerSupport.playerAt(context, TABLE.east());
  }

  private static ActionResult interact(
      final TestContext context, final ServerPlayerEntity player, final ItemStack stack) {
    final BlockPos absolute = context.getAbsolutePos(TABLE);
    final BlockHitResult hit =
        new BlockHitResult(Vec3d.ofCenter(absolute), Direction.UP, absolute, false);
    player.setStackInHand(Hand.MAIN_HAND, stack);
    return player.interactionManager.interactBlock(
        player, context.getWorld(), stack, Hand.MAIN_HAND, hit);
  }
}
