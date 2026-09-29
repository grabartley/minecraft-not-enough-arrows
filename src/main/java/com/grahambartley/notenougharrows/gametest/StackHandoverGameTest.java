package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.world.StackHandover;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

public final class StackHandoverGameTest implements FabricGameTest {
  private static final String BATCH = "stack-handover";
  private static final BlockPos STAND = new BlockPos(3, 2, 3);

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void handsTheWholeStackToARecipientWithRoom(TestContext context) {
    final ServerPlayerEntity recipient = recipient(context);

    StackHandover.handTo(recipient, new ItemStack(Items.DIAMOND, 40));

    context.assertEquals(40, recipient.getInventory().count(Items.DIAMOND), "Diamonds held");
    context.assertEquals(0, SocialTestSupport.droppedNearby(context, Items.DIAMOND), "Drops");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void dropsWhatDoesNotFitAtTheRecipientsFeet(TestContext context) {
    final ServerPlayerEntity recipient = recipient(context);
    AgricultureTestSupport.fillInventory(recipient);
    recipient.getInventory().setStack(0, new ItemStack(Items.DIAMOND, 50));

    StackHandover.handTo(recipient, new ItemStack(Items.DIAMOND, 40));

    context.assertEquals(64, recipient.getInventory().count(Items.DIAMOND), "Diamonds held");
    context.assertEquals(26, SocialTestSupport.droppedNearby(context, Items.DIAMOND), "Drops");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aFullCreativeRecipientLosesNothing(TestContext context) {
    final ServerPlayerEntity recipient = recipient(context);
    recipient.changeGameMode(GameMode.CREATIVE);
    AgricultureTestSupport.fillInventory(recipient);

    StackHandover.handTo(recipient, new ItemStack(Items.DIAMOND, 40));

    context.assertEquals(
        40,
        SocialTestSupport.droppedNearby(context, Items.DIAMOND),
        "Vanilla would delete a creative player's overflow; the courier drops it");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void grantingReturnsWhatDidNotFit(TestContext context) {
    final ServerPlayerEntity recipient = recipient(context);
    AgricultureTestSupport.fillInventory(recipient);
    recipient.getInventory().setStack(0, new ItemStack(Items.DIAMOND, 60));

    final ItemStack left =
        StackHandover.grant(recipient.getInventory(), new ItemStack(Items.DIAMOND, 10));

    context.assertEquals(6, left.getCount(), "Diamonds left over");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void grantingAnUnstackableItemUsesAnEmptySlot(TestContext context) {
    final ServerPlayerEntity recipient = recipient(context);
    final ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
    sword.setDamage(30);

    final ItemStack left = StackHandover.grant(recipient.getInventory(), sword);

    context.assertTrue(left.isEmpty(), "The sword fits");
    context.assertEquals(
        30,
        recipient
            .getInventory()
            .getStack(recipient.getInventory().getSlotWithStack(sword))
            .getDamage(),
        "The sword keeps its wear");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void dropsAStackAtAPoint(TestContext context) {
    final Vec3d at = context.getAbsolute(new Vec3d(3.5, 3.0, 3.5));

    StackHandover.drop(context.getWorld(), at, new ItemStack(Items.DIAMOND, 7));

    context.assertEquals(7, SocialTestSupport.droppedNearby(context, Items.DIAMOND), "Drops");
    context.complete();
  }

  private static ServerPlayerEntity recipient(final TestContext context) {
    final ServerPlayerEntity recipient = ChaosTestSupport.survivalPlayerAt(context, STAND);
    recipient.getInventory().clear();
    return recipient;
  }
}
