package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.arrow.RegisteredArrow;
import com.grahambartley.morearrows.nock.NockedBowArrow;
import java.util.Collection;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public final class NockedBowArrowGameTest implements FabricGameTest {
  private static final String BATCH = "nocked-arrow";
  private static final int TICK_LIMIT = 10;
  private static final BlockPos STANDING_ON = new BlockPos(0, 1, 0);
  private static final int WEAPON_SLOT = 0;
  private static final int QUIVER_SLOT = 1;
  private static final int A_QUIVER = 8;

  @CustomTestProvider
  public Collection<TestFunction> aDrawnBowReportsTheArrowItWillFire() {
    return ArrowTestSupport.perRegisteredArrow(
        BATCH,
        "morearrows.drawnbowreportsitsarrow",
        NockedBowArrowGameTest::assertDrawnBowReportsItsArrow);
  }

  private static void assertDrawnBowReportsItsArrow(
      final TestContext context, final RegisteredArrow<?> arrow) {
    final ServerPlayerEntity player = archerWith(context, arrow.item());
    draw(context, player, Items.BOW, Hand.MAIN_HAND);

    context.assertTrue(
        NockedBowArrow.drawnBy(player).getItem() == arrow.item(),
        "A drawn bow should report "
            + arrow.id()
            + " but reported "
            + NockedBowArrow.drawnBy(player).getItem());
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void reportsNothingForABowDrawnOnAVanillaArrow(final TestContext context) {
    final ServerPlayerEntity player = archerWith(context, Items.ARROW);
    draw(context, player, Items.BOW, Hand.MAIN_HAND);

    context.assertTrue(
        NockedBowArrow.drawnBy(player).isEmpty(),
        "A vanilla arrow should leave the bow reporting nothing");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void reportsNothingWhileNobodyIsDrawingTheBow(final TestContext context) {
    final ServerPlayerEntity player = archerWith(context, ModArrows.TNT_ARROW.item());
    player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.BOW));

    context.assertTrue(
        NockedBowArrow.drawnBy(player).isEmpty(), "A bow nobody is drawing should report nothing");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void reportsNothingWhileTheHolderIsDrawingSomethingThatIsNotABow(
      final TestContext context) {
    final ServerPlayerEntity player = archerWith(context, ModArrows.TNT_ARROW.item());
    draw(context, player, Items.CROSSBOW, Hand.MAIN_HAND);

    context.assertTrue(
        NockedBowArrow.drawnBy(player).isEmpty(),
        "Only a bow should report a nocked arrow through this path");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void readsTheArrowFromABowDrawnInTheOffHand(final TestContext context) {
    final ServerPlayerEntity player = archerWith(context, ModArrows.GRAPPLE_ARROW.item());
    draw(context, player, Items.BOW, Hand.OFF_HAND);

    context.assertTrue(
        NockedBowArrow.drawnBy(player).getItem() == ModArrows.GRAPPLE_ARROW.item(),
        "A bow drawn in the off hand should report its arrow too");
    context.complete();
  }

  private static ServerPlayerEntity archerWith(final TestContext context, final Item arrow) {
    final ServerPlayerEntity player = MockPlayerSupport.playerAt(context, STANDING_ON);
    player.getInventory().clear();
    player.getInventory().selectedSlot = WEAPON_SLOT;
    player.getInventory().setStack(QUIVER_SLOT, new ItemStack(arrow, A_QUIVER));
    return player;
  }

  private static void draw(
      final TestContext context,
      final ServerPlayerEntity player,
      final Item weapon,
      final Hand hand) {
    player.setStackInHand(hand, new ItemStack(weapon));
    player.getStackInHand(hand).use(context.getWorld(), player, hand);
  }
}
