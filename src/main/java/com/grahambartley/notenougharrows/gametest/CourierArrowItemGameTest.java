package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.SocialArrows;
import com.grahambartley.notenougharrows.entity.CourierArrowEntity;
import com.grahambartley.notenougharrows.item.CourierArrowItem;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

public final class CourierArrowItemGameTest implements FabricGameTest {
  private static final String BATCH = "courier-arrow-item";
  private static final BlockPos STAND = new BlockPos(1, 2, 3);
  private static final int FULLY_DRAWN = 0;

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void theCourierArrowIsItsOwnItem(TestContext context) {
    context.assertTrue(
        SocialArrows.COURIER_ARROW.item() instanceof CourierArrowItem,
        "The courier arrow should be registered with its own item");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void theTooltipNamesWhatALoadedArrowCarries(TestContext context) {
    final List<Text> tooltip = tooltipOf(SocialTestSupport.loadedCourier());

    context.assertTrue(
        keyOf(tooltip.get(0)).equals(CourierArrowItem.CARRYING_KEY),
        "A loaded arrow's tooltip should say what it carries, said " + tooltip);
    context.assertTrue(
        tooltip.get(0).getString().contains(String.valueOf(SocialTestSupport.PAYLOAD_COUNT)),
        "and how many");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void theTooltipSaysAnEmptyArrowIsEmpty(TestContext context) {
    context.assertTrue(
        keyOf(tooltipOf(SocialTestSupport.emptyCourier()).get(0))
            .equals(CourierArrowItem.EMPTY_KEY),
        "An empty arrow's tooltip should say it is empty");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void onlyALoadedArrowGlints(TestContext context) {
    final Item courier = SocialArrows.COURIER_ARROW.item();

    context.assertTrue(courier.hasGlint(SocialTestSupport.loadedCourier()), "Loaded glints");
    context.assertFalse(courier.hasGlint(SocialTestSupport.emptyCourier()), "Empty does not");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void aSurvivalCopyThatSpendsNoArrowCarriesNoPayload(TestContext context) {
    final ItemStack copy = SocialTestSupport.loadedCourier();
    copy.set(DataComponentTypes.INTANGIBLE_PROJECTILE, Unit.INSTANCE);

    final PersistentProjectileEntity fired =
        SocialArrows.COURIER_ARROW
            .item()
            .createArrow(
                context.getWorld(), copy, context.createMockPlayer(GameMode.SURVIVAL), null);

    context.assertFalse(((CourierArrowEntity) fired).isLoaded(), "The copy flies empty");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aMultishotCrossbowSendsThePayloadOnlyOnce(TestContext context) {
    final ServerPlayerEntity shooter = ChaosTestSupport.survivalPlayerAt(context, STAND);
    shooter.getInventory().clear();
    final ItemStack crossbow = new ItemStack(Items.CROSSBOW);
    crossbow.addEnchantment(
        context
            .getWorld()
            .getRegistryManager()
            .getWrapperOrThrow(RegistryKeys.ENCHANTMENT)
            .getOrThrow(Enchantments.MULTISHOT),
        1);
    shooter.setStackInHand(Hand.MAIN_HAND, crossbow);
    shooter.getInventory().setStack(9, SocialTestSupport.loadedCourier());

    final ItemStack held = shooter.getStackInHand(Hand.MAIN_HAND);
    held.use(context.getWorld(), shooter, Hand.MAIN_HAND);
    held.onStoppedUsing(context.getWorld(), shooter, FULLY_DRAWN);
    held.use(context.getWorld(), shooter, Hand.MAIN_HAND);

    final List<CourierArrowEntity> fired =
        context
            .getWorld()
            .getEntitiesByClass(
                CourierArrowEntity.class, context.getTestBox().expand(8.0), it -> true);
    context.assertEquals(3, fired.size(), "Multishot fires three arrows");
    context.assertEquals(
        SocialTestSupport.PAYLOAD_COUNT,
        fired.stream().mapToInt(arrow -> arrow.payload().map(ItemStack::getCount).orElse(0)).sum(),
        "Diamonds carried between the three");
    context.complete();
  }

  private static List<Text> tooltipOf(final ItemStack stack) {
    final List<Text> tooltip = new ArrayList<>();
    stack.getItem().appendTooltip(stack, Item.TooltipContext.DEFAULT, tooltip, TooltipType.BASIC);
    return tooltip;
  }

  private static String keyOf(final Text text) {
    return text.getContent() instanceof TranslatableTextContent translatable
        ? translatable.getKey()
        : "";
  }
}
