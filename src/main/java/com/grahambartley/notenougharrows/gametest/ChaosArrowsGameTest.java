package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ChaosArrows;
import com.grahambartley.notenougharrows.DiscoveryArrows;
import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class ChaosArrowsGameTest implements FabricGameTest {
  private static final String BATCH = "chaos-arrows";

  private static final List<RegisteredArrow<?>> CHAOS =
      List.of(
          ChaosArrows.PARTY_ARROW,
          ChaosArrows.CHICKEN_ARROW,
          ChaosArrows.PUFFER_ARROW,
          ChaosArrows.STINK_ARROW,
          ChaosArrows.BOOMERANG_ARROW,
          ChaosArrows.POLYMORPH_ARROW);

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void everyChaosArrowIsAmongTheModsArrows(TestContext context) {
    for (final RegisteredArrow<?> arrow : CHAOS) {
      context.assertTrue(
          ModArrows.registered().contains(arrow),
          arrow.id() + " should be one of the mod's arrows");
      context.assertTrue(
          arrow.item().getDefaultStack().isIn(ItemTags.ARROWS),
          arrow.id() + " should be in the arrows tag so a bow fires it");
    }
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void theChaosArrowsFollowTheDiscoveryArrowsInOrder(TestContext context) {
    final List<RegisteredArrow<?>> registered = ModArrows.registered();
    final int first = registered.indexOf(DiscoveryArrows.TRIPWIRE_ARROW) + 1;

    context.assertEquals(
        CHAOS,
        registered.subList(first, first + CHAOS.size()),
        "The chaos arrows in the creative tab, after the tripwire arrow");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void theBoomerangIsTrackedEveryTickSoItsReturnFlightDrawsSmoothly(TestContext context) {
    context.assertEquals(
        ChaosArrows.BOOMERANG_TRACKING_TICK_INTERVAL,
        ChaosArrows.BOOMERANG_ARROW.entityType().getTrackTickInterval(),
        "Boomerang tracking interval");
    context.complete();
  }
}
