package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.agriculture.ShearService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.BoggedEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;

public final class ShearServiceGameTest implements FabricGameTest {
  private static final String BATCH = "shear-service";
  private static final BlockPos CENTER = TerrainTestSupport.CENTER;
  private static final BlockPos STAND = new BlockPos(3, 2, 3);

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aSheepIsShornAndItsWoolGoesToTheShooter(TestContext context) {
    final SheepEntity sheep = still(context, EntityType.SHEEP);
    sheep.setColor(DyeColor.LIME);
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);

    context.assertTrue(ShearService.shearEntity(sheep, shooter), "The sheep should be shorn");
    context.assertTrue(sheep.isSheared(), "The sheep is bare");
    context.assertTrue(shooter.getInventory().count(Items.LIME_WOOL) > 0, "Wool granted");
    context.assertEquals(
        TerrainTestSupport.droppedCount(context, Items.LIME_WOOL), 0, "Wool on the ground");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aShornSheepIsNotShornAgain(TestContext context) {
    final SheepEntity sheep = still(context, EntityType.SHEEP);
    sheep.setSheared(true);

    context.assertFalse(
        ShearService.shearEntity(sheep, context.createMockPlayer(GameMode.SURVIVAL)),
        "Shears do nothing to a bare sheep");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aMooshroomBecomesACowAndItsMushroomsGoToTheShooter(TestContext context) {
    final MooshroomEntity mooshroom = still(context, EntityType.MOOSHROOM);
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);

    context.assertTrue(ShearService.shearEntity(mooshroom, shooter), "Shorn");
    context.assertTrue(mooshroom.isRemoved(), "The mooshroom is gone");
    context.assertEquals(
        context
            .getWorld()
            .getEntitiesByClass(
                CowEntity.class, context.getTestBox(), cow -> !(cow instanceof MooshroomEntity))
            .size(),
        1,
        "Cows left in its place");
    context.assertEquals(shooter.getInventory().count(Items.RED_MUSHROOM), 5, "Mushrooms granted");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aSnowGolemLosesItsPumpkinToTheShooter(TestContext context) {
    final SnowGolemEntity golem = still(context, EntityType.SNOW_GOLEM);
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);

    context.assertTrue(ShearService.shearEntity(golem, shooter), "Shorn");
    context.assertFalse(golem.hasPumpkin(), "The golem's pumpkin is off");
    context.assertEquals(shooter.getInventory().count(Items.CARVED_PUMPKIN), 1, "Pumpkin granted");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aBoggedIsShornAsShearsWouldShearIt(TestContext context) {
    final BoggedEntity bogged = still(context, EntityType.BOGGED);

    context.assertTrue(
        ShearService.shearEntity(bogged, context.createMockPlayer(GameMode.SURVIVAL)), "Shorn");
    context.assertTrue(bogged.isSheared(), "The bogged is bare");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aCowIsNotSomethingShearsShear(TestContext context) {
    final CowEntity cow = still(context, EntityType.COW);

    context.assertFalse(
        ShearService.shearEntity(cow, context.createMockPlayer(GameMode.SURVIVAL)), "Not shorn");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aShearingWithNoShooterLeavesItsWoolOnTheGround(TestContext context) {
    final SheepEntity sheep = still(context, EntityType.SHEEP);
    sheep.setColor(DyeColor.WHITE);

    context.assertTrue(ShearService.shearEntity(sheep, null), "Shorn with nobody behind it");
    context.assertTrue(
        TerrainTestSupport.droppedCount(context, Items.WHITE_WOOL) > 0, "Wool on the ground");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aFullInventoryLosesNoWool(TestContext context) {
    final SheepEntity sheep = still(context, EntityType.SHEEP);
    sheep.setColor(DyeColor.WHITE);
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);
    AgricultureTestSupport.fillInventory(shooter);

    ShearService.shearEntity(sheep, shooter);

    context.assertTrue(
        TerrainTestSupport.droppedCount(context, Items.WHITE_WOOL) > 0,
        "Wool with nowhere to go should stay on the ground");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aSheepWhereTheShooterMayNotBuildIsLeftAlone(TestContext context) {
    final SheepEntity sheep = still(context, EntityType.SHEEP);

    TerrainTestSupport.withTheBorderElsewhere(
        context, () -> context.assertFalse(ShearService.shearEntity(sheep, null), "Not shorn"));
    context.assertFalse(sheep.isSheared(), "Still woolly");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aFullHiveGivesUpItsHoneycombAndIsEmptied(TestContext context) {
    context.setBlockState(
        CENTER,
        Blocks.BEEHIVE
            .getDefaultState()
            .with(BeehiveBlock.HONEY_LEVEL, BeehiveBlock.FULL_HONEY_LEVEL));
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);

    context.assertTrue(shearBlock(context, Direction.WEST, shooter), "Shorn");
    context.checkBlockState(
        CENTER, state -> state.get(BeehiveBlock.HONEY_LEVEL) == 0, () -> "The hive is emptied");
    context.assertEquals(shooter.getInventory().count(Items.HONEYCOMB), 3, "Honeycomb granted");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aHiveShortOfFullIsLeftAlone(TestContext context) {
    context.setBlockState(
        CENTER, Blocks.BEE_NEST.getDefaultState().with(BeehiveBlock.HONEY_LEVEL, 4));

    context.assertFalse(shearBlock(context, Direction.WEST, null), "Not shorn");
    context.checkBlockState(
        CENTER, state -> state.get(BeehiveBlock.HONEY_LEVEL) == 4, () -> "Honey left in");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aPumpkinIsCarvedOnTheFaceStruckAndItsSeedsGoToTheShooter(TestContext context) {
    context.setBlockState(CENTER, Blocks.PUMPKIN);
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);

    context.assertTrue(shearBlock(context, Direction.WEST, shooter), "Carved");
    context.checkBlockState(
        CENTER,
        state ->
            state.isOf(Blocks.CARVED_PUMPKIN)
                && state.get(CarvedPumpkinBlock.FACING) == Direction.WEST,
        () -> "The pumpkin should be carved facing west");
    context.assertEquals(shooter.getInventory().count(Items.PUMPKIN_SEEDS), 4, "Seeds granted");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aPumpkinStruckFromAboveIsCarvedTowardTheShooter(TestContext context) {
    context.setBlockState(CENTER, Blocks.PUMPKIN);

    ShearService.shearBlock(
        context.getWorld(), context.getAbsolutePos(CENTER), Direction.UP, Direction.SOUTH, null);

    context.checkBlockState(
        CENTER,
        state -> state.get(CarvedPumpkinBlock.FACING) == Direction.SOUTH,
        () -> "A top hit carves the side facing the shooter");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aPumpkinWhereTheShooterMayNotBuildIsLeftAlone(TestContext context) {
    context.setBlockState(CENTER, Blocks.PUMPKIN);

    TerrainTestSupport.withTheBorderElsewhere(
        context, () -> context.assertFalse(shearBlock(context, Direction.WEST, null), "Refused"));
    context.expectBlock(Blocks.PUMPKIN, CENTER);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aBlockShearsDoNothingToIsLeftAlone(TestContext context) {
    context.setBlockState(CENTER, Blocks.STONE);

    context.assertFalse(shearBlock(context, Direction.WEST, null), "Stone is not shorn");
    context.expectBlock(Blocks.STONE, CENTER);
    context.complete();
  }

  private static boolean shearBlock(
      final TestContext context, final Direction face, final PlayerEntity shooter) {
    return ShearService.shearBlock(
        context.getWorld(), context.getAbsolutePos(CENTER), face, Direction.NORTH, shooter);
  }

  private static <E extends net.minecraft.entity.mob.MobEntity> E still(
      final TestContext context, final EntityType<E> type) {
    final E mob = context.spawnEntity(type, STAND);
    mob.setAiDisabled(true);
    return mob;
  }
}
