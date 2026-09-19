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

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowFiredFromABowBuildsFreezeOnTheEntityItHits(TestContext context) {
    final CowEntity target = ControlTestSupport.stillCowAt(context, TARGET_STAND);
    MockPlayerSupport.fireEastStraight(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.FROST_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              target.getFrozenTicks() > 0,
              "A frost arrow should leave freeze built up on what it hit, but frozen ticks were "
                  + target.getFrozenTicks());
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aDispensedArrowBuildsFreezeJustTheSame(TestContext context) {
    final CowEntity target = ControlTestSupport.stillCowAt(context, TARGET_STAND);
    FiringRangeSupport.dispenseEast(context, new BlockPos(1, 3, 3), ModArrows.FROST_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              target.getFrozenTicks() > 0,
              "A dispensed frost arrow should build freeze on what it hit");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aTargetWearingLeatherIsLeftUnfrozen(TestContext context) {
    final CowEntity target = ControlTestSupport.stillCowAt(context, TARGET_STAND);
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
