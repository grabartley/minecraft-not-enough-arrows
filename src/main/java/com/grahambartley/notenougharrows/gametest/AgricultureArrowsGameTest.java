package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.AgricultureArrows;
import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class AgricultureArrowsGameTest implements FabricGameTest {
  private static final String BATCH = "agriculture-arrows";

  private static final List<RegisteredArrow<?>> AGRICULTURE =
      List.of(
          AgricultureArrows.BLOSSOM_ARROW,
          AgricultureArrows.HARVEST_ARROW,
          AgricultureArrows.TILL_ARROW,
          AgricultureArrows.SAPLING_ARROW,
          AgricultureArrows.SHEAR_ARROW,
          AgricultureArrows.BEE_ARROW);

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void everyAgricultureArrowIsAmongTheModsArrows(TestContext context) {
    for (final RegisteredArrow<?> arrow : AGRICULTURE) {
      context.assertTrue(
          ModArrows.registered().contains(arrow),
          arrow.id() + " should be one of the mod's arrows");
      context.assertTrue(
          ModArrows.catalog().definitions().stream()
              .anyMatch(definition -> definition.id().equals(arrow.id())),
          arrow.id() + " should be in the arrow catalogue");
    }
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void theAgricultureArrowsFollowTheTerrainArrows(TestContext context) {
    final List<RegisteredArrow<?>> registered = ModArrows.registered();

    context.assertEquals(
        registered.indexOf(AgricultureArrows.BLOSSOM_ARROW),
        registered.indexOf(ModArrows.WEB_ARROW) + 1,
        "Position of the blossom arrow in the creative tab order");
    context.complete();
  }
}
