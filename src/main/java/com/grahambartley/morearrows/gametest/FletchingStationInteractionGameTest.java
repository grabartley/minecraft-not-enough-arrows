package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.fletching.FletchingStationInteraction;
import com.grahambartley.morearrows.fletching.FletchingStationScreenHandler;
import com.grahambartley.morearrows.server.ServerConfigService;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestTypes;

public final class FletchingStationInteractionGameTest implements FabricGameTest {
  private static final String BATCH = "fletching-station-interaction";
  private static final String TEMPLATE = "more-arrows:fire_pad";
  private static final int TICK_LIMIT = 40;
  private static final int SETTLE_TICKS = 20;

  private static final BlockPos TABLE = new BlockPos(3, 2, 3);
  private static final BlockPos ABOVE_TABLE = new BlockPos(3, 3, 3);
  private static final BlockPos BESIDE_TABLE = new BlockPos(4, 2, 3);
  private static final BlockPos ACROSS_THE_ROOM = new BlockPos(1, 2, 1);
  private static final BlockPos THE_FLETCHERS_CORNER = new BlockPos(5, 2, 5);

  private static final String ENABLE_STATION = "morearrows config fletching stationenabled true";
  private static final String DISABLE_STATION = "morearrows config fletching stationenabled false";
  private static final String RESET_CONFIG = "morearrows config reset";

  private static final int CRAFTING_GRID_WIDTH = 3;
  private static final int CRAFTING_GRID_HEIGHT = 3;
  private static final int TNT_ARROWS_PER_CRAFT = 8;

  @BeforeBatch(batchId = BATCH)
  public void startFromTheDefaultConfigBeforeBatch(ServerWorld world) {
    ServerConfigService.update(world.getServer(), MoreArrowsConfig.defaults());
  }

  @AfterBatch(batchId = BATCH)
  public void restoreTheDefaultConfigAfterBatch(ServerWorld world) {
    ServerConfigService.update(world.getServer(), MoreArrowsConfig.defaults());
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void aFletchingTableOffersTheStation(TestContext context) {
    run(context, RESET_CONFIG);
    final ServerPlayerEntity player = playerAtAFletchingTable(context);

    context.assertTrue(
        offersStation(context, player, TABLE),
        "An enabled station should be offered when a player uses a fletching table");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void theOfferedStationOpensOntoTheTableItWasOfferedFrom(TestContext context) {
    run(context, RESET_CONFIG);
    final ServerPlayerEntity player = playerAtAFletchingTable(context);

    FletchingStationInteraction.openFor(player, context.getWorld(), context.getAbsolutePos(TABLE));

    context.assertTrue(
        player.currentScreenHandler instanceof FletchingStationScreenHandler,
        "Opening the station should leave the station's own screen handler in front of the player");
    context.assertTrue(
        player.currentScreenHandler.canUse(player),
        "The opened station should be bound to the fletching table it was opened from");

    context.removeBlock(TABLE);
    context.assertFalse(
        player.currentScreenHandler.canUse(player),
        "A station whose fletching table is gone should no longer be usable");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void aDisabledStationIsNeverOffered(TestContext context) {
    run(context, RESET_CONFIG);
    final ServerPlayerEntity player = playerAtAFletchingTable(context);
    context.assertTrue(
        run(context, DISABLE_STATION), "An operator should be able to disable the station");

    context.assertFalse(
        offersStation(context, player, TABLE),
        "A disabled station should not be offered when the table is used");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void aDisabledStationLeavesTheTableDoingNothingAtAll(TestContext context) {
    run(context, RESET_CONFIG);
    final ServerPlayerEntity player = playerAtAFletchingTable(context);
    run(context, DISABLE_STATION);

    use(context, player, TABLE);

    context.assertFalse(
        player.currentScreenHandler instanceof FletchingStationScreenHandler,
        "A disabled station should not open when the table is used");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void reEnablingTheStationTakesEffectOnTheNextUseWithNoRestart(TestContext context) {
    run(context, RESET_CONFIG);
    final ServerPlayerEntity player = playerAtAFletchingTable(context);
    run(context, DISABLE_STATION);
    context.assertFalse(
        offersStation(context, player, TABLE), "The station should start this test disabled");

    context.assertTrue(
        run(context, ENABLE_STATION), "An operator should be able to re-enable the station");

    context.assertTrue(
        offersStation(context, player, TABLE),
        "Re-enabling the station should offer it on the very next use, with no restart");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void sneakingWithABlockInHandPlacesAgainstTheTableRatherThanOpeningIt(
      TestContext context) {
    run(context, RESET_CONFIG);
    final ServerPlayerEntity player = playerAtAFletchingTable(context);
    player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.STONE));
    player.setSneaking(true);

    context.assertFalse(
        offersStation(context, player, TABLE),
        "Sneak placing against the table should not offer the station");

    use(context, player, TABLE);

    context.assertFalse(
        player.currentScreenHandler instanceof FletchingStationScreenHandler,
        "Sneak placing against the table should not open the station");
    context.assertTrue(
        context.getBlockState(ABOVE_TABLE).isOf(Blocks.STONE),
        "Sneak placing against the table should place the held block above it");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void sneakingWithEmptyHandsStillOffersTheStation(TestContext context) {
    run(context, RESET_CONFIG);
    final ServerPlayerEntity player = playerAtAFletchingTable(context);
    player.setSneaking(true);

    context.assertTrue(
        offersStation(context, player, TABLE),
        "Sneaking with nothing to place should still offer the station");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void noOtherBlockIsEverMistakenForTheStation(TestContext context) {
    run(context, RESET_CONFIG);
    final ServerPlayerEntity player = playerAtAFletchingTable(context);
    context.setBlockState(ACROSS_THE_ROOM, Blocks.CHEST);

    context.assertFalse(
        offersStation(context, player, ACROSS_THE_ROOM),
        "A block that is not a fletching table should never offer the station");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void everyOtherBlockKeepsItsOwnInteractionUntouched(TestContext context) {
    run(context, RESET_CONFIG);
    final ServerPlayerEntity player = playerAtAFletchingTable(context);
    context.setBlockState(ACROSS_THE_ROOM, Blocks.CHEST);

    use(context, player, ACROSS_THE_ROOM);

    context.assertTrue(
        player.currentScreenHandler instanceof GenericContainerScreenHandler,
        "A chest beside the station must still open its own vanilla screen");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void aClientThatCouldNotDrawTheStationIsNeverSentIt(TestContext context) {
    run(context, RESET_CONFIG);
    final ServerPlayerEntity player = playerAtAFletchingTable(context);

    context.assertFalse(
        FletchingStationInteraction.canDrawTheStation(player),
        "A connection that never declared it can receive this mod's payloads has no station to"
            + " draw, so it must never be sent one");

    use(context, player, TABLE);

    context.assertFalse(
        player.currentScreenHandler instanceof FletchingStationScreenHandler,
        "A client that cannot draw the station must not have one opened on it");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void theStationLeavesAPlainVanillaFletchingTableBehind(TestContext context) {
    run(context, RESET_CONFIG);
    final ServerPlayerEntity player = playerAtAFletchingTable(context);

    FletchingStationInteraction.openFor(player, context.getWorld(), context.getAbsolutePos(TABLE));

    context.assertTrue(
        context.getBlockState(TABLE).isOf(Blocks.FLETCHING_TABLE),
        "The station must not replace the vanilla fletching table");
    context.assertTrue(
        context.getWorld().getBlockEntity(context.getAbsolutePos(TABLE)) == null,
        "The station must not give the fletching table a block entity");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void aUsedFletchingTableIsStillAFletcherJobSite(TestContext context) {
    run(context, RESET_CONFIG);
    final ServerPlayerEntity player = playerAtAFletchingTable(context);

    FletchingStationInteraction.openFor(player, context.getWorld(), context.getAbsolutePos(TABLE));

    context.assertTrue(
        context
            .getWorld()
            .getPointOfInterestStorage()
            .hasTypeAt(PointOfInterestTypes.FLETCHER, context.getAbsolutePos(TABLE)),
        "A fletching table the station opened from should still be a fletcher job site");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void anExistingFletcherKeepsItsProfessionWhenTheStationOpens(TestContext context) {
    run(context, RESET_CONFIG);
    final ServerPlayerEntity player = playerAtAFletchingTable(context);
    final VillagerEntity fletcher = context.spawnEntity(EntityType.VILLAGER, THE_FLETCHERS_CORNER);
    fletcher.setAiDisabled(true);
    fletcher.setVillagerData(
        fletcher.getVillagerData().withProfession(VillagerProfession.FLETCHER));

    FletchingStationInteraction.openFor(player, context.getWorld(), context.getAbsolutePos(TABLE));

    context.runAtTick(
        SETTLE_TICKS,
        () -> {
          context.assertTrue(
              fletcher.getVillagerData().getProfession() == VillagerProfession.FLETCHER,
              "Opening the station must not disturb an existing fletcher's profession");
          context.assertTrue(
              context
                  .getWorld()
                  .getPointOfInterestStorage()
                  .hasTypeAt(PointOfInterestTypes.FLETCHER, context.getAbsolutePos(TABLE)),
              "The fletcher's job site should survive the station being opened beside it");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void theStationSettingNeverChangesWhatACraftingTableProduces(TestContext context) {
    run(context, RESET_CONFIG);

    final ItemStack withTheStationOn = craftATntArrow(context);

    run(context, DISABLE_STATION);
    final ItemStack withTheStationOff = craftATntArrow(context);

    context.assertTrue(
        withTheStationOff.isOf(ModArrows.TNT_ARROW.item()),
        "A crafting table recipe must still produce its arrow while the station is disabled");
    context.assertEquals(
        TNT_ARROWS_PER_CRAFT,
        withTheStationOff.getCount(),
        "Crafting table yield while the station is disabled");
    context.assertEquals(
        withTheStationOn.getCount(),
        withTheStationOff.getCount(),
        "Crafting table yield should not move when the station setting does");
    context.complete();
  }

  private static boolean offersStation(
      final TestContext context, final ServerPlayerEntity player, final BlockPos target) {
    return FletchingStationInteraction.opensStation(
        ServerConfigService.get().fletching().stationEnabled(),
        player,
        context.getWorld(),
        context.getAbsolutePos(target));
  }

  private static ItemStack craftATntArrow(final TestContext context) {
    final ItemStack gunpowderArrow = new ItemStack(ModArrows.GUNPOWDER_ARROW.item());
    final ItemStack tnt = new ItemStack(Items.TNT);
    final CraftingRecipeInput input =
        CraftingRecipeInput.create(
            CRAFTING_GRID_WIDTH,
            CRAFTING_GRID_HEIGHT,
            List.of(
                gunpowderArrow,
                gunpowderArrow,
                gunpowderArrow,
                gunpowderArrow,
                tnt,
                gunpowderArrow,
                gunpowderArrow,
                gunpowderArrow,
                gunpowderArrow));

    final ServerWorld world = context.getWorld();
    return world
        .getRecipeManager()
        .getFirstMatch(RecipeType.CRAFTING, input, world)
        .map(entry -> entry.value().craft(input, world.getRegistryManager()))
        .orElse(ItemStack.EMPTY);
  }

  private static ServerPlayerEntity playerAtAFletchingTable(final TestContext context) {
    context.setBlockState(TABLE, Blocks.FLETCHING_TABLE);
    return MockPlayerSupport.playerAt(context, BESIDE_TABLE);
  }

  private static void use(
      final TestContext context, final ServerPlayerEntity player, final BlockPos target) {
    final BlockPos pos = context.getAbsolutePos(target);
    player.interactionManager.interactBlock(
        player,
        context.getWorld(),
        player.getStackInHand(Hand.MAIN_HAND),
        Hand.MAIN_HAND,
        new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false));
  }

  private static boolean run(final TestContext context, final String command) {
    final ServerCommandSource source = context.getWorld().getServer().getCommandSource();
    try {
      return context
              .getWorld()
              .getServer()
              .getCommandManager()
              .getDispatcher()
              .execute(command, source)
          > 0;
    } catch (final CommandSyntaxException ex) {
      return false;
    }
  }
}
