package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.item.TintedArrowItem;
import com.grahambartley.notenougharrows.item.TintedArrowItems;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class TintedArrowItemsGameTest implements FabricGameTest {

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void listsEveryTintedArrowAndNothingElse(final TestContext context) {
    final List<TintedArrowItem> expected =
        ModArrows.registered().stream()
            .filter(arrow -> arrow.item() instanceof TintedArrowItem)
            .map(arrow -> (TintedArrowItem) arrow.item())
            .toList();

    context.assertEquals(
        TintedArrowItems.registered(), expected, "Every tinted arrow should be listed, in order");
    context.assertTrue(
        TintedArrowItems.registered().contains(ModArrows.PAINT_ARROW.item()),
        "The paint arrow should be listed as tinted");
    context.assertFalse(
        TintedArrowItems.registered().contains(ModArrows.RUST_ARROW.item()),
        "The rust arrow should not be listed as tinted");
    context.complete();
  }
}
