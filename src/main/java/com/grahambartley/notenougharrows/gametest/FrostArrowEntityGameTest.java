package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class FrostArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "frost-arrow";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);
  private static final BlockPos BYSTANDER_STAND = new BlockPos(3, 3, 5);
  private static final int LONG_AFTER_LANDING = 150;

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowFiredFromABowBuildsFreezeOnTheEntityItHits(TestContext context) {
    final CowEntity target = ControlTestSupport.sturdyStillCowAt(context, TARGET_STAND);
    MockPlayerSupport.fireEastStraight(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.FROST_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              target.isFrozen(),
              "A frost arrow should leave what it hit frozen hard enough to take freeze damage,"
                  + " but frozen ticks were "
                  + target.getFrozenTicks());
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aDispensedArrowBuildsFreezeJustTheSame(TestContext context) {
    final CowEntity target = ControlTestSupport.sturdyStillCowAt(context, TARGET_STAND);
    FiringRangeSupport.dispenseEast(context, new BlockPos(1, 3, 3), ModArrows.FROST_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              target.isFrozen(), "A dispensed frost arrow should freeze what it hit just the same");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 200)
  public void theFreezeIsHeldRatherThanThawingStraightAway(TestContext context) {
    final CowEntity target = ControlTestSupport.sturdyStillCowAt(context, TARGET_STAND);
    MockPlayerSupport.fireEastStraight(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.FROST_ARROW.item());

    context.runAtTick(
        LONG_AFTER_LANDING,
        () -> {
          context.assertTrue(
              target.isFrozen(),
              "Vanilla thaws two ticks of freeze every tick, so the arrow has to hold its target"
                  + " frozen for its whole duration, but frozen ticks were "
                  + target.getFrozenTicks());
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aTargetWearingLeatherIsLeftUnfrozen(TestContext context) {
    final CowEntity target = ControlTestSupport.sturdyStillCowAt(context, TARGET_STAND);
    target.equipStack(EquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));
    MockPlayerSupport.fireEastStraight(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.FROST_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              target.getFrozenTicks() == 0,
              "Leather should keep a frost arrow from freezing its wearer, as powder snow does");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowThatHitsOnlyABlockFreezesNobody(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final CowEntity bystander = ControlTestSupport.stillCowAt(context, BYSTANDER_STAND);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.FROST_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              bystander.getFrozenTicks() == 0, "An arrow that hits a block should freeze nobody");
          context.complete();
        });
  }
}
