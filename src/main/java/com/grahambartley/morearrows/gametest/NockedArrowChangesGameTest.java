package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.nock.NockedArrowChanges;
import java.util.UUID;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class NockedArrowChangesGameTest implements FabricGameTest {
  private static final String BATCH = "nocked-arrow";
  private static final int TICK_LIMIT = 10;
  private static final UUID AN_ARCHER = UUID.nameUUIDFromBytes("archer".getBytes());

  private static ItemStack tntArrow() {
    return new ItemStack(ModArrows.TNT_ARROW.item());
  }

  private static ItemStack grappleArrow() {
    return new ItemStack(ModArrows.GRAPPLE_ARROW.item());
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void staysSilentForAnArcherWhoIsNotDrawingAnything(final TestContext context) {
    final NockedArrowChanges changes = new NockedArrowChanges();

    context.assertFalse(
        changes.record(AN_ARCHER, ItemStack.EMPTY),
        "An archer drawing nothing should not be worth a packet");
    context.assertFalse(
        changes.record(AN_ARCHER, ItemStack.EMPTY),
        "An archer still drawing nothing should not be worth a packet");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void reportsAChangeWhenAnArcherStartsDrawingAModArrow(final TestContext context) {
    final NockedArrowChanges changes = new NockedArrowChanges();

    context.assertTrue(
        changes.record(AN_ARCHER, tntArrow()), "Starting a draw should be worth a packet");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void staysSilentWhileTheSameArrowStaysNocked(final TestContext context) {
    final NockedArrowChanges changes = new NockedArrowChanges();
    changes.record(AN_ARCHER, tntArrow());

    context.assertFalse(
        changes.record(AN_ARCHER, tntArrow()),
        "Holding a draw should not resend the same arrow every tick");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void reportsAChangeWhenTheNockedArrowIsSwapped(final TestContext context) {
    final NockedArrowChanges changes = new NockedArrowChanges();
    changes.record(AN_ARCHER, tntArrow());

    context.assertTrue(
        changes.record(AN_ARCHER, grappleArrow()), "Swapping arrows should be worth a packet");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void reportsAChangeWhenAnArcherStopsDrawingThenStaysSilent(final TestContext context) {
    final NockedArrowChanges changes = new NockedArrowChanges();
    changes.record(AN_ARCHER, tntArrow());

    context.assertTrue(
        changes.record(AN_ARCHER, ItemStack.EMPTY), "Releasing a draw should be worth a packet");
    context.assertFalse(
        changes.record(AN_ARCHER, ItemStack.EMPTY),
        "Having already released should not be worth another packet");
    context.complete();
  }

  @GameTest(templateName = EMPTY_STRUCTURE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void resendsAfterAnArcherIsForgottenSoARejoinResyncs(final TestContext context) {
    final NockedArrowChanges changes = new NockedArrowChanges();
    changes.record(AN_ARCHER, tntArrow());

    changes.forget(AN_ARCHER);

    context.assertTrue(
        changes.record(AN_ARCHER, tntArrow()),
        "An archer who reconnects should be resent their nocked arrow");
    context.complete();
  }
}
