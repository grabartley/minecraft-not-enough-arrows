package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.DiscoveryArrows;
import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.TraversalArrows;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class DiscoveryArrowsGameTest implements FabricGameTest {
  private static final String BATCH = "discovery-arrows";

  private static final List<RegisteredArrow<?>> DISCOVERY =
      List.of(
          DiscoveryArrows.TORCH_ARROW,
          DiscoveryArrows.BEACON_ARROW,
          DiscoveryArrows.PROSPECTOR_ARROW,
          DiscoveryArrows.SONAR_ARROW,
          DiscoveryArrows.TRACER_ARROW,
          DiscoveryArrows.TRIPWIRE_ARROW);

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void everyDiscoveryArrowIsAmongTheModsArrows(TestContext context) {
    for (final RegisteredArrow<?> arrow : DISCOVERY) {
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
  public void theDiscoveryArrowsFollowTheTraversalArrowsInOrder(TestContext context) {
    final List<RegisteredArrow<?>> registered = ModArrows.registered();
    final int first = registered.indexOf(TraversalArrows.BRIDGE_ARROW) + 1;

    context.assertEquals(
        DISCOVERY,
        registered.subList(first, first + DISCOVERY.size()),
        "The discovery arrows in the creative tab, after the bridge arrow");
    context.complete();
  }
}
