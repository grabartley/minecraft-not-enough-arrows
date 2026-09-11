package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import java.util.Collection;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;

final class ArrowTestSupport {
  static final int TICK_LIMIT = 10;

  @FunctionalInterface
  interface PerArrowBody {
    void run(TestContext context, RegisteredArrow<?> arrow);
  }

  private ArrowTestSupport() {}

  static Collection<TestFunction> perRegisteredArrow(
      final String batchId, final String behavior, final PerArrowBody body) {
    return ModArrows.registered().stream()
        .map(
            arrow ->
                new TestFunction(
                    batchId,
                    behavior + "." + arrow.id().getPath(),
                    FabricGameTest.EMPTY_STRUCTURE,
                    TICK_LIMIT,
                    0L,
                    true,
                    context -> body.run(context, arrow)))
        .toList();
  }
}
