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
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

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
