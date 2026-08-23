package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.arrow.RegisteredArrow;
import java.util.Collection;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity.PickupPermission;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;
import net.minecraft.util.Unit;
import net.minecraft.world.GameMode;

public final class ArrowPickupGameTest implements FabricGameTest {
  private static final String BATCH = "arrow-pickup";

  @CustomTestProvider
  public Collection<TestFunction> anArrowFiredInSurvivalIsRecoverable() {
    return ArrowTestSupport.perRegisteredArrow(
        BATCH,
        "morearrows.survivalarrowisrecoverable",
        ArrowPickupGameTest::assertSurvivalArrowIsRecoverable);
  }

  @CustomTestProvider
  public Collection<TestFunction> anArrowFiredInCreativeIsNotRecoverable() {
    return ArrowTestSupport.perRegisteredArrow(
        BATCH,
        "morearrows.creativearrowisnotrecoverable",
        ArrowPickupGameTest::assertCreativeArrowIsNotRecoverable);
  }

  @CustomTestProvider
  public Collection<TestFunction> aDispensedArrowIsRecoverable() {
    return ArrowTestSupport.perRegisteredArrow(
        BATCH,
        "morearrows.dispensedarrowisrecoverable",
        ArrowPickupGameTest::assertDispensedArrowIsRecoverable);
  }

  @CustomTestProvider
  public Collection<TestFunction> aFiredArrowIsRecoveredAsItsOwnItem() {
    return ArrowTestSupport.perRegisteredArrow(
        BATCH,
        "morearrows.arrowisrecoveredasitsownitem",
        ArrowPickupGameTest::assertArrowIsRecoveredAsItsOwnItem);
  }

  private static void assertSurvivalArrowIsRecoverable(
      final TestContext context, final RegisteredArrow<?> arrow) {
    final PersistentProjectileEntity fired =
        shootFromBow(context, arrow, new ItemStack(arrow.item()));

    context.assertTrue(
        fired.pickupType == PickupPermission.ALLOWED,
        "A survival-fired " + arrow.id() + " should be recoverable, was " + fired.pickupType);
    context.complete();
  }

  private static void assertCreativeArrowIsNotRecoverable(
      final TestContext context, final RegisteredArrow<?> arrow) {
    final ItemStack ammunition = new ItemStack(arrow.item());
    ammunition.set(DataComponentTypes.INTANGIBLE_PROJECTILE, Unit.INSTANCE);

    final PersistentProjectileEntity fired = shootFromBow(context, arrow, ammunition);

    context.assertTrue(
        fired.pickupType == PickupPermission.CREATIVE_ONLY,
        "A creative-fired " + arrow.id() + " should not be recoverable, was " + fired.pickupType);
    context.complete();
  }

  private static void assertDispensedArrowIsRecoverable(
      final TestContext context, final RegisteredArrow<?> arrow) {
    final PersistentProjectileEntity dispensed = ArrowFiringGameTest.dispense(context, arrow);

    context.assertTrue(
        dispensed.pickupType == PickupPermission.ALLOWED,
        "A dispensed " + arrow.id() + " should be recoverable, was " + dispensed.pickupType);
    context.complete();
  }

  private static void assertArrowIsRecoveredAsItsOwnItem(
      final TestContext context, final RegisteredArrow<?> arrow) {
    final PersistentProjectileEntity fired =
        shootFromBow(context, arrow, new ItemStack(arrow.item()));

    context.assertTrue(
        fired.getItemStack().isOf(arrow.item()),
        "Recovering " + arrow.id() + " should return that arrow, not " + fired.getItemStack());
    context.complete();
  }

  private static PersistentProjectileEntity shootFromBow(
      final TestContext context, final RegisteredArrow<?> arrow, final ItemStack ammunition) {
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);
    return arrow
        .item()
        .createArrow(context.getWorld(), ammunition, shooter, new ItemStack(Items.BOW));
  }
}
