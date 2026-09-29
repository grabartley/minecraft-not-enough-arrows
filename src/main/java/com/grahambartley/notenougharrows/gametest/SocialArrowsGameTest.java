package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ChaosArrows;
import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.SocialArrows;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class SocialArrowsGameTest implements FabricGameTest {
  private static final String BATCH = "social-arrows";

  private static final List<RegisteredArrow<?>> SOCIAL =
      List.of(SocialArrows.COURIER_ARROW, SocialArrows.SNOW_GOLEM_ARROW, SocialArrows.MAGNET_ARROW);

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void everySocialArrowIsAmongTheModsArrows(TestContext context) {
    for (final RegisteredArrow<?> arrow : SOCIAL) {
      context.assertTrue(
          ModArrows.registered().contains(arrow),
          arrow.id() + " should be one of the mod's arrows");
      context.assertTrue(
          arrow.item().getDefaultStack().isIn(ItemTags.ARROWS),
          arrow.id() + " should be in the arrows tag so a bow fires it");
    }
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void theSocialArrowsFollowTheChaosArrowsInOrder(TestContext context) {
    final List<RegisteredArrow<?>> registered = ModArrows.registered();
    final int first = registered.indexOf(ChaosArrows.POLYMORPH_ARROW) + 1;

    context.assertEquals(
        SOCIAL,
        registered.subList(first, first + SOCIAL.size()),
        "The social arrows in the creative tab, after the polymorph arrow");
    context.complete();
  }
}
