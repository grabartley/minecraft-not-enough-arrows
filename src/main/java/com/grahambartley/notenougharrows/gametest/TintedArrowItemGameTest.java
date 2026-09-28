package com.grahambartley.notenougharrows.gametest;

import com.google.gson.JsonObject;
import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.ModDataComponents;
import com.grahambartley.notenougharrows.item.TintedArrowItem;
import com.grahambartley.notenougharrows.tint.ArrowChoice;
import com.grahambartley.notenougharrows.tint.TintChoice;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryOps;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

public final class TintedArrowItemGameTest implements FabricGameTest {

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void offersOneVariantPerChoice(final TestContext context) {
    final List<ItemStack> variants = paintItem().variants();
    final Set<String> carried =
        variants.stream()
            .map(stack -> paintItem().choiceOf(stack).key())
            .collect(Collectors.toSet());

    context.assertEquals(variants.size(), 16, "The paint arrow should offer one variant per dye");
    context.assertEquals(carried.size(), 16, "Every paint arrow variant should carry its own dye");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void namesTheChoiceItCarries(final TestContext context) {
    for (final TintChoice choice : paintItem().palette().choices()) {
      final Text name = paintItem().stackOf(choice).getName();

      context.assertTrue(
          name.getContent() instanceof TranslatableTextContent translatable
              && translatable.getKey().equals(TintedArrowItem.NAME_KEY)
              && translatable.getArgs()[0] instanceof Text label
              && label.getContent() instanceof TranslatableTextContent labelKey
              && labelKey.getKey().equals(choice.labelKey()),
          "The "
              + choice.key()
              + " paint arrow should say it is "
              + choice.key()
              + ", was "
              + name);
    }
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void aStackWithoutAChoiceIsTheDefault(final TestContext context) {
    final ItemStack stack = new ItemStack(paintItem());
    stack.remove(ModDataComponents.ARROW_CHOICE);

    context.assertEquals(
        paintItem().choiceOf(stack),
        paintItem().palette().fallback(),
        "A paint arrow carrying no dye should read as the default");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void aPlainNewStackIsTheDefaultAndStacksWithACraftedDefault(final TestContext context) {
    final ItemStack plain = new ItemStack(paintItem());
    final ItemStack crafted = paintItem().stackOf(paintItem().palette().fallback());

    context.assertEquals(
        paintItem().choiceOf(plain),
        paintItem().palette().fallback(),
        "A paint arrow made without a dye should read as the default");
    context.assertTrue(
        ItemStack.areItemsAndComponentsEqual(plain, crafted),
        "A plain paint arrow and a crafted default one should stack together");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void twoDifferentChoicesDoNotStack(final TestContext context) {
    final ItemStack red = paintItem().stackOf(paintItem().palette().find("red").orElseThrow());
    final ItemStack blue = paintItem().stackOf(paintItem().palette().find("blue").orElseThrow());

    context.assertFalse(
        ItemStack.areItemsAndComponentsEqual(red, blue),
        "A red and a blue paint arrow should not stack together");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void aSavedStackWithAnUnknownChoiceStillLoadsAtTheDefault(final TestContext context) {
    final JsonObject components = new JsonObject();
    components.addProperty(ModDataComponents.ARROW_CHOICE_ID.toString(), "mauve");
    final JsonObject saved = new JsonObject();
    saved.addProperty("id", ModArrows.PAINT_ARROW.id().toString());
    saved.addProperty("count", 3);
    saved.add("components", components);

    final DataResult<ItemStack> loaded =
        ItemStack.CODEC.parse(
            RegistryOps.of(JsonOps.INSTANCE, context.getWorld().getRegistryManager()), saved);

    context.assertTrue(loaded.isSuccess(), "A paint arrow with an unknown dye should still load");
    final ItemStack stack = loaded.getOrThrow();
    context.assertEquals(stack.getCount(), 3, "The loaded stack should keep its count");
    context.assertEquals(
        stack.get(ModDataComponents.ARROW_CHOICE),
        new ArrowChoice("mauve"),
        "The loaded stack should keep the choice it carried");
    context.assertEquals(
        paintItem().choiceOf(stack),
        paintItem().palette().fallback(),
        "A paint arrow with an unknown dye should read and render as the default");
    context.complete();
  }

  private static TintedArrowItem paintItem() {
    return (TintedArrowItem) ModArrows.PAINT_ARROW.item();
  }
}
