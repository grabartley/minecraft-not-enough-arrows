package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.chaos.DiscPalette;
import com.grahambartley.notenougharrows.chaos.PartyShow;
import com.grahambartley.notenougharrows.tint.TintChoice;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class PartyShowGameTest implements FabricGameTest {
  private static final String BATCH = "party-show";

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void everyDiscThePartyArrowCarriesHasASongToPlay(TestContext context) {
    for (final TintChoice disc : DiscPalette.create().choices()) {
      context.assertTrue(
          Registries.ITEM.containsId(disc.ingredient()),
          disc.ingredient() + " should be a vanilla item");
      context.assertTrue(
          PartyShow.songOf(context.getWorld(), disc).isPresent(),
          "The " + disc.key() + " disc should resolve to a jukebox song");
    }
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aPartyChangesNoBlockAndLeavesNothingBehind(TestContext context) {
    final BlockPos spot = new BlockPos(8, 4, 8);
    final Vec3d at = Vec3d.ofCenter(context.getAbsolutePos(spot));
    final int before = entitiesAround(context, at);

    context.assertTrue(
        PartyShow.throwAt(context.getWorld(), at, DiscPalette.create().fallback()),
        "A party should be thrown");
    context.expectBlock(Blocks.AIR, spot);
    context.assertEquals(before, entitiesAround(context, at), "Entities around the party");
    context.assertTrue(
        context
            .getWorld()
            .getEntitiesByClass(
                FireworkRocketEntity.class, context.getTestBox().expand(8.0), rocket -> true)
            .isEmpty(),
        "A party spawns no firework rocket");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void aPartyNeedsSomewhereAndADisc(TestContext context) {
    context.assertFalse(
        PartyShow.throwAt(context.getWorld(), null, DiscPalette.create().fallback()),
        "No party without a place");
    context.assertFalse(
        PartyShow.throwAt(context.getWorld(), Vec3d.ZERO, null), "No party without a disc");
    context.complete();
  }

  private static int entitiesAround(final TestContext context, final Vec3d at) {
    return context
        .getWorld()
        .getOtherEntities((Entity) null, context.getTestBox().expand(8.0), entity -> true)
        .size();
  }
}
