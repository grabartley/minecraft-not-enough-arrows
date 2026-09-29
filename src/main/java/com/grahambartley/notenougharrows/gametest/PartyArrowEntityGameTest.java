package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ChaosArrows;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.entity.PartyArrowEntity;
import com.grahambartley.notenougharrows.item.TintedArrowItem;
import com.grahambartley.notenougharrows.tint.ArrowChoice;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class PartyArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "party-arrow";
  private static final String DISABLED_BATCH = "party-arrow-disabled";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);
  private static final int A_QUIVER = 8;

  @BeforeBatch(batchId = DISABLED_BATCH)
  public void switchThePartyOffBeforeBatch(ServerWorld world) {
    ChaosTestSupport.useChaos(chaos -> chaos.withParty(chaos.party().withEnabled(false)));
  }

  @AfterBatch(batchId = DISABLED_BATCH)
  public void restoreDefaultConfigAfterDisabledBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aPartyArrowBurstsOnTheWallChangesNothingAndIsSpent(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    TerrainArrowTestSupport.fireFromBow(context, quiverOf("pigstep"));

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.STONE, FiringRangeSupport.BACKSTOP);
          context.expectBlock(Blocks.AIR, FiringRangeSupport.IMPACT_FACE);
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, PartyArrowEntity.class) == null,
              "A party arrow that threw its party is spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aPartyArrowCarriesTheDiscItWasCraftedFrom(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    TerrainArrowTestSupport.fireFromBow(context, quiverOf("otherside"));

    context.runAtTick(
        1,
        () -> {
          final PartyArrowEntity arrow =
              FiringRangeSupport.firedArrow(context, PartyArrowEntity.class);
          context.assertTrue(arrow != null, "The party arrow should be in flight");
          context.assertEquals("otherside", arrow.tint().key(), "The disc the arrow carries");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aPartyArrowLeavesTheCreatureItStrikesUnhurt(TestContext context) {
    final CowEntity cow = ControlTestSupport.sturdyStillCowAt(context, TARGET_STAND);
    final float health = cow.getHealth();
    TerrainArrowTestSupport.fireFromBow(context, quiverOf("cat"));

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertEquals(health, cow.getHealth(), "The cow's health");
          context.assertTrue(
              FiringRangeSupport.arrowWasSpent(context), "The party arrow should be spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = DISABLED_BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aDisabledPartyArrowEmbedsAndCanBeRecovered(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    TerrainArrowTestSupport.fireFromBow(context, quiverOf("cat"));

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, PartyArrowEntity.class) != null,
              "A switched off party arrow stays in the wall to be picked up");
          context.complete();
        });
  }

  private static ItemStack quiverOf(final String disc) {
    final TintedArrowItem party = (TintedArrowItem) ChaosArrows.PARTY_ARROW.item();
    final ItemStack quiver = party.stackOf(party.palette().resolve(new ArrowChoice(disc)));
    quiver.setCount(A_QUIVER);
    return quiver;
  }
}
