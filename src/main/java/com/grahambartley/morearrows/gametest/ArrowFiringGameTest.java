package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.arrow.ArrowTagAudit;
import com.grahambartley.morearrows.arrow.RegisteredArrow;
import com.grahambartley.morearrows.entity.BaseArrowEntity;
import java.util.Collection;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

public final class ArrowFiringGameTest implements FabricGameTest {
  private static final String BATCH = "arrow-firing";
  private static final Vec3d DISPENSE_POS = new Vec3d(0.5, 1.5, 0.5);

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void everyRegisteredArrowIsNockableByBowsAndCrossbows(TestContext context) {
    final List<Identifier> unfireable =
        ArrowTagAudit.unfireable(registeredArrowIds(), taggedArrowIds());

    context.assertTrue(
        unfireable.isEmpty(),
        "Bows and crossbows only draw items in "
            + ItemTags.ARROWS.id()
            + ", so these arrows could never be fired: "
            + unfireable);
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void everyRegisteredArrowIsDispensedAsAProjectile(TestContext context) {
    for (final RegisteredArrow<?> arrow : ModArrows.registered()) {
      context.assertTrue(
          DispenserBlock.BEHAVIORS.get(arrow.item()) instanceof ProjectileDispenserBehavior,
          "Arrow " + arrow.id() + " should be shot by a dispenser rather than dropped as an item");
    }
    context.complete();
  }

  @CustomTestProvider
  public Collection<TestFunction> anArrowIsNeverThrownFromTheHand() {
    return ArrowTestSupport.perRegisteredArrow(
        BATCH,
        "morearrows.arrowisneverthrownfromthehand",
        ArrowFiringGameTest::assertArrowIsNeverThrownFromTheHand);
  }

  @CustomTestProvider
  public Collection<TestFunction> aDispensedArrowHasNoShooter() {
    return ArrowTestSupport.perRegisteredArrow(
        BATCH,
        "morearrows.dispensedarrowhasnoshooter",
        ArrowFiringGameTest::assertDispensedArrowHasNoShooter);
  }

  private static void assertDispensedArrowHasNoShooter(
      final TestContext context, final RegisteredArrow<?> arrow) {
    final BaseArrowEntity dispensed = dispense(context, arrow);

    context.assertTrue(
        dispensed.getOwner() == null,
        "A dispenser is not an entity, so a dispensed " + arrow.id() + " has no owner");
    context.assertTrue(
        dispensed.shooter().isEmpty(),
        "A dispensed " + arrow.id() + " should resolve no living shooter");
    context.assertTrue(
        dispensed.shootingPlayer().isEmpty(),
        "A dispensed " + arrow.id() + " should resolve no shooting player");
    context.complete();
  }

  private static void assertArrowIsNeverThrownFromTheHand(
      final TestContext context, final RegisteredArrow<?> arrow) {
    final PlayerEntity holder = context.createMockPlayer(GameMode.SURVIVAL);
    final ItemStack held = new ItemStack(arrow.item());
    holder.setStackInHand(Hand.MAIN_HAND, held);

    final TypedActionResult<ItemStack> result =
        arrow.item().use(context.getWorld(), holder, Hand.MAIN_HAND);

    context.assertTrue(
        result.getResult() == ActionResult.PASS,
        "An arrow only leaves a weapon, so using "
            + arrow.id()
            + " in hand should do nothing, but returned "
            + result.getResult());
    context.assertEquals(
        held.getCount(), 1, "Arrows left in hand after using " + arrow.id() + " in hand");
    context.dontExpectEntity(arrow.entityType());
    context.complete();
  }

  static BaseArrowEntity dispense(final TestContext context, final RegisteredArrow<?> arrow) {
    return (BaseArrowEntity)
        arrow
            .item()
            .createEntity(
                context.getWorld(),
                context.getAbsolute(DISPENSE_POS),
                new ItemStack(arrow.item()),
                Direction.EAST);
  }

  private static List<Identifier> registeredArrowIds() {
    return ModArrows.registered().stream().map(RegisteredArrow::id).toList();
  }

  private static List<Identifier> taggedArrowIds() {
    return Registries.ITEM.getEntryList(ItemTags.ARROWS).stream()
        .flatMap(entries -> entries.stream())
        .map(entry -> Registries.ITEM.getId(entry.value()))
        .toList();
  }
}
