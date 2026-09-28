package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.ModDataComponents;
import com.grahambartley.notenougharrows.entity.TintedArrowEntity;
import com.grahambartley.notenougharrows.item.TintedArrowItem;
import com.grahambartley.notenougharrows.tint.ArrowChoice;
import com.grahambartley.notenougharrows.tint.TintChoice;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

public final class TintedArrowEntityGameTest implements FabricGameTest {
  private static final Vec3d DISPENSE_POS = new Vec3d(0.5, 1.5, 0.5);

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void aFiredArrowCarriesTheChoiceOfTheStackItWasFiredFrom(final TestContext context) {
    final TintedArrowEntity fired = fire(context, paintStack(context, "red"));

    context.assertEquals(fired.tint().key(), "red", "A red paint arrow should fly red");
    context.assertEquals(
        choiceOf(fired.getItemStack()), "red", "A red paint arrow should carry red");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void aDispensedArrowCarriesTheChoiceOfTheStackItWasDispensedFrom(
      final TestContext context) {
    final TintedArrowEntity dispensed =
        (TintedArrowEntity)
            paintItem()
                .createEntity(
                    context.getWorld(),
                    context.getAbsolute(DISPENSE_POS),
                    paintStack(context, "lime"),
                    Direction.EAST);

    context.assertEquals(dispensed.tint().key(), "lime", "A dispensed lime arrow should fly lime");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void aRecoveredArrowComesBackCarryingItsChoiceAndFiresItAgain(final TestContext context) {
    final PlayerEntity player = context.createMockPlayer(GameMode.SURVIVAL);
    final TintedArrowEntity fired = fire(context, player, paintStack(context, "blue"));
    fired.setNoClip(true);

    fired.onPlayerCollision(player);

    final ItemStack recovered = recoveredPaint(context, player);
    context.assertEquals(
        choiceOf(recovered), "blue", "A recovered blue paint arrow should still be blue");

    final TintedArrowEntity refired = fire(context, player, recovered);
    context.assertEquals(refired.tint().key(), "blue", "A re-fired blue arrow should fly blue");
    context.assertEquals(
        choiceOf(refired.getItemStack()), "blue", "A re-fired blue arrow should still carry blue");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void anArrowSavedAndReloadedKeepsItsChoice(final TestContext context) {
    final TintedArrowEntity fired = fire(context, paintStack(context, "purple"));

    final TintedArrowEntity reloaded = reload(context, fired);

    context.assertEquals(
        reloaded.tint().key(), "purple", "A reloaded purple arrow should still fly purple");
    context.assertEquals(
        choiceOf(reloaded.getItemStack()),
        "purple",
        "A reloaded purple arrow should still carry purple");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void anArrowReloadedFromAnUnknownChoiceFliesAtTheDefaultButKeepsWhatItCarried(
      final TestContext context) {
    final ItemStack unknown = new ItemStack(paintItem());
    unknown.set(ModDataComponents.ARROW_CHOICE, new ArrowChoice("mauve"));
    final TintedArrowEntity fired = fire(context, unknown);

    final TintedArrowEntity reloaded = reload(context, fired);

    context.assertEquals(
        reloaded.tint(),
        paintItem().palette().fallback(),
        "An arrow carrying a choice no palette knows should fly at the default");
    context.assertEquals(
        reloaded.getItemStack().get(ModDataComponents.ARROW_CHOICE),
        new ArrowChoice("mauve"),
        "An arrow carrying a choice no palette knows should give that choice back untouched");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void anArrowReloadedWithoutItsItemFliesAtTheDefault(final TestContext context) {
    final TintedArrowEntity fired = fire(context, paintStack(context, "red"));
    final NbtCompound saved = new NbtCompound();
    fired.saveNbt(saved);
    saved.remove("item");

    final Optional<Entity> reloaded = EntityType.getEntityFromNbt(saved, context.getWorld());

    context.assertTrue(reloaded.isPresent(), "An arrow saved without its item should still load");
    context.assertEquals(
        ((TintedArrowEntity) reloaded.get()).tint(),
        paintItem().palette().fallback(),
        "An arrow saved without its item should fly at the default");
    context.complete();
  }

  private static TintedArrowEntity fire(final TestContext context, final ItemStack ammunition) {
    return fire(context, context.createMockPlayer(GameMode.SURVIVAL), ammunition);
  }

  private static TintedArrowEntity fire(
      final TestContext context, final PlayerEntity shooter, final ItemStack ammunition) {
    final PersistentProjectileEntity arrow =
        paintItem().createArrow(context.getWorld(), ammunition, shooter, new ItemStack(Items.BOW));
    return (TintedArrowEntity) arrow;
  }

  private static TintedArrowEntity reload(
      final TestContext context, final TintedArrowEntity arrow) {
    final NbtCompound saved = new NbtCompound();
    arrow.saveNbt(saved);
    final Optional<Entity> reloaded = EntityType.getEntityFromNbt(saved, context.getWorld());
    context.assertTrue(reloaded.isPresent(), "A saved tinted arrow should load again");
    return (TintedArrowEntity) reloaded.get();
  }

  private static ItemStack recoveredPaint(final TestContext context, final PlayerEntity player) {
    for (int slot = 0; slot < player.getInventory().size(); slot++) {
      final ItemStack stack = player.getInventory().getStack(slot);
      if (stack.isOf(paintItem())) {
        return stack;
      }
    }
    context.throwGameTestException("The player should have recovered the paint arrow");
    return ItemStack.EMPTY;
  }

  private static ItemStack paintStack(final TestContext context, final String key) {
    final Optional<TintChoice> choice = paintItem().palette().find(key);
    context.assertTrue(choice.isPresent(), "The paint arrow should offer " + key);
    return paintItem().stackOf(choice.get());
  }

  private static String choiceOf(final ItemStack stack) {
    return paintItem().choiceOf(stack).key();
  }

  private static TintedArrowItem paintItem() {
    return (TintedArrowItem) ModArrows.PAINT_ARROW.item();
  }
}
