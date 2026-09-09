package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.arrow.RegisteredArrow;
import com.grahambartley.morearrows.nock.ChargedCrossbowArrow;
import java.util.Collection;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.item.CrossbowItem;
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

public final class ChargedCrossbowArrowGameTest implements FabricGameTest {
  private static final BlockPos STANDING_ON = new BlockPos(0, 2, 0);
  private static final int WEAPON_SLOT = 0;
  private static final int QUIVER_SLOT = 1;
  private static final int A_QUIVER = 8;
  private static final int FULLY_DRAWN = 0;

  @CustomTestProvider
  public Collection<TestFunction> aChargedCrossbowReportsTheArrowItWillFire() {
    return ArrowTestSupport.perRegisteredArrow(
        NockedArrowTestSupport.BATCH,
        "morearrows.chargedcrossbowreportsitsarrow",
        ChargedCrossbowArrowGameTest::assertChargedCrossbowReportsItsArrow);
  }

  private static void assertChargedCrossbowReportsItsArrow(
      final TestContext context, final RegisteredArrow<?> arrow) {
    final ItemStack loaded =
        ChargedCrossbowArrow.loadedInto(crossbowChargedByAPlayerWith(context, arrow.item()));

    context.assertTrue(
        loaded.getItem() == arrow.item(),
        "A charged crossbow should report " + arrow.id() + " but reported " + loaded.getItem());
    context.complete();
  }

  @GameTest(
      templateName = EMPTY_STRUCTURE,
      batchId = NockedArrowTestSupport.BATCH,
      tickLimit = NockedArrowTestSupport.TICK_LIMIT)
  public void reportsNothingForACrossbowChargedWithAVanillaArrow(final TestContext context) {
    context.assertTrue(
        ChargedCrossbowArrow.loadedInto(crossbowChargedByAPlayerWith(context, Items.ARROW))
            .isEmpty(),
        "A vanilla arrow should leave the crossbow reporting nothing");
    context.complete();
  }

  @GameTest(
      templateName = EMPTY_STRUCTURE,
      batchId = NockedArrowTestSupport.BATCH,
      tickLimit = NockedArrowTestSupport.TICK_LIMIT)
  public void reportsNothingForAnUnchargedCrossbow(final TestContext context) {
    context.assertTrue(
        ChargedCrossbowArrow.loadedInto(new ItemStack(Items.CROSSBOW)).isEmpty(),
        "An uncharged crossbow should report nothing");
    context.complete();
  }

  @GameTest(
      templateName = EMPTY_STRUCTURE,
      batchId = NockedArrowTestSupport.BATCH,
      tickLimit = NockedArrowTestSupport.TICK_LIMIT)
  public void reportsNothingForAWeaponThatIsNotACrossbow(final TestContext context) {
    final ItemStack bow = new ItemStack(Items.BOW);
    bow.set(
        DataComponentTypes.CHARGED_PROJECTILES,
        ChargedProjectilesComponent.of(new ItemStack(ModArrows.TNT_ARROW.item())));

    context.assertTrue(
        ChargedCrossbowArrow.loadedInto(bow).isEmpty(),
        "Only a crossbow should report a charged arrow through this path");
    context.complete();
  }

  @GameTest(
      templateName = EMPTY_STRUCTURE,
      batchId = NockedArrowTestSupport.BATCH,
      tickLimit = NockedArrowTestSupport.TICK_LIMIT)
  public void readsTheFirstArrowFromAMultishotCrossbow(final TestContext context) {
    final ItemStack crossbow = new ItemStack(Items.CROSSBOW);
    crossbow.set(
        DataComponentTypes.CHARGED_PROJECTILES,
        ChargedProjectilesComponent.of(
            List.of(
                new ItemStack(ModArrows.GRAPPLE_ARROW.item()),
                new ItemStack(ModArrows.TNT_ARROW.item()),
                new ItemStack(ModArrows.TNT_ARROW.item()))));

    context.assertTrue(
        ChargedCrossbowArrow.loadedInto(crossbow).getItem() == ModArrows.GRAPPLE_ARROW.item(),
        "A multishot crossbow should report the first arrow it is loaded with");
    context.complete();
  }

  private static ItemStack crossbowChargedByAPlayerWith(
      final TestContext context, final Item arrow) {
    final ServerPlayerEntity player = MockPlayerSupport.playerAt(context, STANDING_ON);
    player.getInventory().clear();
    player.getInventory().selectedSlot = WEAPON_SLOT;
    player.getInventory().setStack(QUIVER_SLOT, new ItemStack(arrow, A_QUIVER));
    player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.CROSSBOW));

    final ItemStack crossbow = player.getStackInHand(Hand.MAIN_HAND);
    crossbow.use(context.getWorld(), player, Hand.MAIN_HAND);
    crossbow.onStoppedUsing(context.getWorld(), player, FULLY_DRAWN);

    context.assertTrue(
        CrossbowItem.isCharged(crossbow), "The crossbow should have charged before being read");
    return crossbow;
  }
}
