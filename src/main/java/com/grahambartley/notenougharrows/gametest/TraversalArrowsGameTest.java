package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.AgricultureArrows;
import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.TraversalArrows;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class TraversalArrowsGameTest implements FabricGameTest {
  private static final String BATCH = "traversal-arrows";

  private static final List<RegisteredArrow<?>> TRAVERSAL =
      List.of(
          TraversalArrows.ZIPLINE_ARROW,
          TraversalArrows.TOW_ARROW,
          TraversalArrows.UPDRAFT_ARROW,
          TraversalArrows.VINE_ARROW,
          TraversalArrows.TRAMPOLINE_ARROW,
          TraversalArrows.SCAFFOLD_ARROW,
          TraversalArrows.BRIDGE_ARROW);

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void everyTraversalArrowIsAmongTheModsArrows(TestContext context) {
    for (final RegisteredArrow<?> arrow : TRAVERSAL) {
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
  public void theTraversalArrowsFollowTheAgricultureArrows(TestContext context) {
    final List<RegisteredArrow<?>> registered = ModArrows.registered();

    context.assertEquals(
        registered.indexOf(TraversalArrows.ZIPLINE_ARROW),
        registered.indexOf(AgricultureArrows.BEE_ARROW) + 1,
        "Position of the zipline arrow in the creative tab order");
    context.complete();
  }
}
