package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.arrow.RegisteredArrow;
import com.grahambartley.morearrows.config.ExplosiveArrowConfig;
import com.grahambartley.morearrows.entity.ExplosiveArrowEntity;
import com.grahambartley.morearrows.explosive.ExplosiveTier;
import com.grahambartley.morearrows.fuse.FuseService;
import java.util.Collection;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class ExplosiveArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "explosive";

  @CustomTestProvider
  public Collection<TestFunction> aFiredArrowLightsItsFuse() {
    return List.of(
        tierTest("gunpowder", ModArrows.GUNPOWDER_ARROW, ExplosiveTier.GUNPOWDER),
        tierTest("tnt", ModArrows.TNT_ARROW, ExplosiveTier.TNT),
        tierTest("firecharge", ModArrows.FIRE_CHARGE_ARROW, ExplosiveTier.FIRE_CHARGE));
  }

  @GameTest(templateName = UtilityArrowTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 120)
  public void theTopTierLeavesFireWhereItDetonates(TestContext context) {
    UtilityArrowTestSupport.raiseBackstop(context);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, UtilityArrowTestSupport.SHOOTER_STAND),
        ModArrows.FIRE_CHARGE_ARROW.item());

    context.runAtTick(
        ExplosiveArrowConfig.DEFAULT_FIRE_CHARGE.delayTicks()
            + UtilityArrowTestSupport.LANDING_TICK
            + 20,
        () -> {
          context.assertTrue(
              firePlaced(context), "The top tier should leave a fire patch where it detonated");
          context.complete();
        });
  }

  private static boolean firePlaced(final TestContext context) {
    for (int x = 3; x <= 6; x++) {
      for (int y = 2; y <= 4; y++) {
        for (int z = 1; z <= 5; z++) {
          if (context.getBlockState(new BlockPos(x, y, z)).isOf(Blocks.FIRE)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private static TestFunction tierTest(
      final String label, final RegisteredArrow<?> arrow, final ExplosiveTier tier) {
    return new TestFunction(
        BATCH,
        "morearrows.explosivearrowlightsitsfuse." + label,
        UtilityArrowTestSupport.TEMPLATE,
        80,
        0L,
        true,
        context -> assertFuseLights(context, arrow, tier));
  }

  private static void assertFuseLights(
      final TestContext context, final RegisteredArrow<?> arrow, final ExplosiveTier tier) {
    UtilityArrowTestSupport.raiseBackstop(context);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, UtilityArrowTestSupport.SHOOTER_STAND),
        arrow.item());

    context.runAtTick(
        UtilityArrowTestSupport.LANDING_TICK,
        () -> {
          final ExplosiveArrowEntity landed = landedArrow(context, arrow);
          System.out.println(
              "[DIAG] "
                  + arrow.id()
                  + " landed="
                  + (landed != null)
                  + " allExplosiveInBox="
                  + context
                      .getWorld()
                      .getEntitiesByClass(
                          ExplosiveArrowEntity.class, context.getTestBox(), c -> true)
                      .stream()
                      .map(c -> c.getType().toString() + "@" + c.getBlockPos())
                      .toList()
                  + " fuses="
                  + FuseService.fusesIn(context.getWorld()).size());
          context.assertTrue(
              landed != null, "A fired " + arrow.id() + " should still exist where it landed");
          context.assertTrue(
              FuseService.fuseOn(context.getWorld(), landed.getUuid()) != null,
              "A fired " + arrow.id() + " should be burning its own fuse where it landed");
          context.complete();
        });
  }

  @Nullable
  private static ExplosiveArrowEntity landedArrow(
      final TestContext context, final RegisteredArrow<?> arrow) {
    return context
        .getWorld()
        .getEntitiesByClass(
            ExplosiveArrowEntity.class,
            context.getTestBox(),
            candidate -> candidate.getType() == arrow.entityType())
        .stream()
        .findFirst()
        .orElse(null);
  }
}
