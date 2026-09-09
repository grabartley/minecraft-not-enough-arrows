package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.arrow.RegisteredArrow;
import com.grahambartley.morearrows.nock.ChargedCrossbowArrow;
import java.util.Collection;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;

public final class ChargedCrossbowArrowGameTest implements FabricGameTest {

  @CustomTestProvider
  public Collection<TestFunction> aChargedCrossbowReportsTheArrowItWillFire() {
    return ArrowTestSupport.perRegisteredArrow(
        NockedArrowTestSupport.BATCH,
        "morearrows.chargedcrossbowreportsitsarrow",
        ChargedCrossbowArrowGameTest::assertChargedCrossbowReportsItsArrow);
  }

  private static void assertChargedCrossbowReportsItsArrow(
      final TestContext context, final RegisteredArrow<?> arrow) {
    final ItemStack loaded = ChargedCrossbowArrow.loadedInto(crossbowChargedWith(arrow.item()));

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
        ChargedCrossbowArrow.loadedInto(crossbowChargedWith(Items.ARROW)).isEmpty(),
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

  private static ItemStack crossbowChargedWith(final Item arrow) {
    final ItemStack crossbow = new ItemStack(Items.CROSSBOW);
    crossbow.set(
        DataComponentTypes.CHARGED_PROJECTILES,
        ChargedProjectilesComponent.of(new ItemStack(arrow)));
    return crossbow;
  }
}
