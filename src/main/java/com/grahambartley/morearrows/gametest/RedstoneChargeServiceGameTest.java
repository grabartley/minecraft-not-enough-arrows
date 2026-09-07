package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModBlocks;
import com.grahambartley.morearrows.block.RedstoneChargeBlock;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.config.ServerConfigHolder;
import com.grahambartley.morearrows.config.UtilityArrowConfig;
import com.grahambartley.morearrows.redstone.RedstoneChargeService;
import com.grahambartley.morearrows.server.ServerConfigService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class RedstoneChargeServiceGameTest implements FabricGameTest {
  private static final String BATCH = "redstone-charge-lifecycle";
  private static final String TEMPLATE = "more-arrows:fire_pad";
  private static final BlockPos CHARGE = new BlockPos(3, 3, 3);
  private static final BlockPos LAMP = new BlockPos(3, 3, 4);
  private static final int STRENGTH = 15;
  private static final int SHORT_DURATION_TICKS = 10;
  private static final int LONG_DURATION_TICKS = 400;

  @BeforeBatch(batchId = BATCH)
  public void forgetChargesBeforeBatch(ServerWorld world) {
    RedstoneChargeService.forget();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aChargeLightsARedstoneLampBesideIt(TestContext context) {
    context.setBlockState(LAMP, Blocks.REDSTONE_LAMP);

    context.assertTrue(
        charge(context, STRENGTH, LONG_DURATION_TICKS), "Charging an air position should succeed");

    context.runAtTick(
        5,
        () -> {
          context.checkBlockState(
              LAMP,
              state -> state.get(Properties.LIT),
              () -> "A charge beside a redstone lamp should light it");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aLampGoesDarkOnceTheChargeExpires(TestContext context) {
    context.setBlockState(LAMP, Blocks.REDSTONE_LAMP);
    charge(context, STRENGTH, SHORT_DURATION_TICKS);

    context.runAtTick(
        SHORT_DURATION_TICKS + 10,
        () -> {
          context.expectBlock(Blocks.AIR, CHARGE);
          context.checkBlockState(
              LAMP,
              state -> !state.get(Properties.LIT),
              () -> "The lamp should go dark once the charge expires");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aChargeHoldsUntilItsDurationElapses(TestContext context) {
    charge(context, STRENGTH, LONG_DURATION_TICKS);

    context.runAtTick(
        30,
        () -> {
          context.expectBlock(ModBlocks.REDSTONE_CHARGE, CHARGE);
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aChargeStillExpiresWhenTheTrackerHasBeenLost(TestContext context) {
    charge(context, STRENGTH, SHORT_DURATION_TICKS);
    RedstoneChargeService.forget();

    context.runAtTick(
        SHORT_DURATION_TICKS + 15,
        () -> {
          context.expectBlock(Blocks.AIR, CHARGE);
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aChargeCarriesTheStrengthItWasGiven(TestContext context) {
    charge(context, 7, LONG_DURATION_TICKS);

    context.checkBlockState(
        CHARGE,
        state -> state.get(RedstoneChargeBlock.POWER) == 7,
        () -> "The charge should carry the strength it was given");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aDurationOfZeroChargesNothingAtAll(TestContext context) {
    context.assertFalse(charge(context, STRENGTH, 0), "A zero duration should charge nothing");
    context.expectBlock(Blocks.AIR, CHARGE);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aStrengthOfZeroChargesNothingAtAll(TestContext context) {
    context.assertFalse(
        charge(context, 0, LONG_DURATION_TICKS), "A zero strength should charge nothing");
    context.expectBlock(Blocks.AIR, CHARGE);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void theLiveServerConfigDecidesTheStrengthOfACharge(TestContext context) {
    final MoreArrowsConfig previous = ServerConfigService.get();
    try {
      ServerConfigHolder.set(configWithSignal(9, LONG_DURATION_TICKS));
      RedstoneChargeService.charge(context.getWorld(), context.getAbsolutePos(CHARGE), null);
    } finally {
      ServerConfigHolder.set(previous);
    }

    context.checkBlockState(
        CHARGE,
        state -> state.get(RedstoneChargeBlock.POWER) == 9,
        () -> "The charge should take its strength from the live server config");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void theLiveServerConfigDecidesHowLongAChargeHolds(TestContext context) {
    final MoreArrowsConfig previous = ServerConfigService.get();
    try {
      ServerConfigHolder.set(configWithSignal(STRENGTH, SHORT_DURATION_TICKS));
      RedstoneChargeService.charge(context.getWorld(), context.getAbsolutePos(CHARGE), null);
    } finally {
      ServerConfigHolder.set(previous);
    }

    context.expectBlock(ModBlocks.REDSTONE_CHARGE, CHARGE);
    context.runAtTick(
        SHORT_DURATION_TICKS + 10,
        () -> {
          context.expectBlock(Blocks.AIR, CHARGE);
          context.complete();
        });
  }

  private static boolean charge(
      final TestContext context, final int strength, final int durationTicks) {
    return RedstoneChargeService.charge(
        context.getWorld(), context.getAbsolutePos(CHARGE), null, strength, durationTicks);
  }

  private static MoreArrowsConfig configWithSignal(final int strength, final int durationTicks) {
    final UtilityArrowConfig utility = UtilityArrowConfig.defaults();
    return MoreArrowsConfig.defaults()
        .withUtility(
            utility
                .withRedstoneSignalStrength(strength)
                .withRedstoneSignalDurationTicks(durationTicks));
  }
}
